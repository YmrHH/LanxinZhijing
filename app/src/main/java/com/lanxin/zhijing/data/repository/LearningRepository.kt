package com.lanxin.zhijing.data.repository

import androidx.room.withTransaction
import com.lanxin.zhijing.data.ChatMessage
import com.lanxin.zhijing.data.KnowledgeNode
import com.lanxin.zhijing.data.KnowledgeRelation
import com.lanxin.zhijing.data.LearningItem
import com.lanxin.zhijing.data.MockData
import com.lanxin.zhijing.data.ai.FeynmanEvaluationResult
import com.lanxin.zhijing.data.ai.NodeQuestionContext
import com.lanxin.zhijing.data.local.AppDatabase
import com.lanxin.zhijing.data.local.LocalDbConstants
import com.lanxin.zhijing.data.ai.ImportSource
import com.lanxin.zhijing.data.local.entity.ChatMessageEntity
import com.lanxin.zhijing.data.local.entity.ImportRecordEntity
import com.lanxin.zhijing.data.local.entity.KnowledgeNodeEntity
import com.lanxin.zhijing.data.local.entity.KnowledgeRelationEntity
import com.lanxin.zhijing.data.local.entity.LearningContentEntity
import com.lanxin.zhijing.data.local.entity.MasteryRecordEntity
import com.lanxin.zhijing.data.local.entity.ReviewRecordEntity
import com.lanxin.zhijing.data.local.learningMetaJson
import com.lanxin.zhijing.data.local.toChatMessage
import com.lanxin.zhijing.data.local.toKnowledgeNode
import com.lanxin.zhijing.data.local.toKnowledgeRelation
import com.lanxin.zhijing.data.local.toLearningItem
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LearningRepository(
    private val database: AppDatabase
) {

    private val learningContentDao get() = database.learningContentDao()
    private val knowledgeNodeDao get() = database.knowledgeNodeDao()
    private val knowledgeRelationDao get() = database.knowledgeRelationDao()
    private val chatMessageDao get() = database.chatMessageDao()
    private val reviewRecordDao get() = database.reviewRecordDao()
    private val masteryRecordDao get() = database.masteryRecordDao()
    private val importRecordDao get() = database.importRecordDao()

    val learningItems: Flow<List<LearningItem>> =
        learningContentDao.getAllContents().map { list ->
            list
                .filter { it.sourceType != LocalDbConstants.SOURCE_TREE_ROOT }
                .sortedByDescending { it.updatedAt }
                .map { it.toLearningItem() }
        }

    val knowledgeNodesForTree: Flow<List<KnowledgeNode>> =
        knowledgeNodeDao.getNodesByContentId(LocalDbConstants.CONTENT_TREE_ROOT_ID).map { entities ->
            entities
                .filter { it.id != LocalDbConstants.NODE_DERIVATIVE }
                .map { it.toKnowledgeNode() }
        }

    val centerTreeNode: Flow<KnowledgeNode?> =
        knowledgeNodeDao.getNodeById(LocalDbConstants.NODE_DERIVATIVE).map { it?.toKnowledgeNode() }

    val knowledgeRelations: Flow<List<KnowledgeRelation>> =
        knowledgeRelationDao.getRelationsByContentId(LocalDbConstants.CONTENT_TREE_ROOT_ID).map { list ->
            list.map { it.toKnowledgeRelation() }
        }

    val knowledgeGraphEdges: Flow<List<Pair<String, String>>> =
        knowledgeRelationDao.getRelationsByContentId(LocalDbConstants.CONTENT_TREE_ROOT_ID).map { list ->
            list.map { it.fromNodeId to it.toNodeId }
        }

    fun getChatMessages(nodeId: String): Flow<List<ChatMessage>> =
        chatMessageDao.getMessagesByNodeId(nodeId).map { list -> list.map { it.toChatMessage() } }

    val profileKnowledgeCount: Flow<Int> =
        knowledgeNodeDao.getAllNodes().map { it.size }

    val profileReviewPendingCount: Flow<Int> =
        knowledgeNodeDao.getAllNodes().map { nodes ->
            nodes.count { it.mastery < 45 }
        }

    val latestFeynmanForDerivative: Flow<ReviewRecordEntity?> =
        reviewRecordDao.getRecordsByNodeId(LocalDbConstants.NODE_DERIVATIVE).map { it.firstOrNull() }

    fun getKnowledgeNode(nodeId: String): Flow<KnowledgeNode?> =
        knowledgeNodeDao.getNodeById(nodeId).map { it?.toKnowledgeNode() }

    /**
     * 为 AI 节点追问 / 分步提示构建上下文：当前节点 + 知识树上通过边相邻的节点（最多 6 个）。
     */
    suspend fun buildNodeQuestionContext(nodeId: String): NodeQuestionContext {
        val entity = knowledgeNodeDao.getNodeById(nodeId).first()
        val allEntities = knowledgeNodeDao.getAllNodes().first()
        val idSet = allEntities.map { it.id }.toSet()
        val byId = allEntities.associateBy { it.id }
        val relations = knowledgeRelationDao.getRelationsByContentId(LocalDbConstants.CONTENT_TREE_ROOT_ID).first()
        val neighborIds = relations.asSequence()
            .filter { it.fromNodeId == nodeId || it.toNodeId == nodeId }
            .map { if (it.fromNodeId == nodeId) it.toNodeId else it.fromNodeId }
            .filter { it != nodeId && it in idSet }
            .distinct()
            .take(6)
            .toList()
        val relatedNodes = neighborIds.mapNotNull { byId[it]?.toKnowledgeNode() }
        if (entity != null) {
            return NodeQuestionContext(
                nodeId = entity.id,
                nodeTitle = entity.title,
                nodeDescription = entity.description,
                mastery = entity.mastery,
                relatedNodes = relatedNodes,
                recentMistakes = emptyList(),
                recentReviewFeedback = emptyList()
            )
        }
        return NodeQuestionContext(
            nodeId = nodeId,
            nodeTitle = "未知节点",
            nodeDescription = "",
            mastery = 0,
            relatedNodes = relatedNodes,
            recentMistakes = emptyList(),
            recentReviewFeedback = emptyList()
        )
    }

    suspend fun initializeIfNeeded() {
        if (learningContentDao.countContents() > 0) return
        val now = System.currentTimeMillis()
        database.withTransaction {
            val treeRoot = LearningContentEntity(
                id = LocalDbConstants.CONTENT_TREE_ROOT_ID,
                title = "导数与单调性",
                sourceType = LocalDbConstants.SOURCE_TREE_ROOT,
                rawText = "请你不用公式，讲给同学听：为什么导数可以判断函数的增减？",
                createdAt = now,
                updatedAt = now
            )
            learningContentDao.upsertContent(treeRoot)

            val learningRows = MockData.learningItems.map { item ->
                LearningContentEntity(
                    id = item.id,
                    title = item.title,
                    sourceType = LocalDbConstants.SOURCE_LEARNING,
                    rawText = learningMetaJson(item.progress, item.status),
                    createdAt = now,
                    updatedAt = now
                )
            }
            learningContentDao.upsertContents(learningRows)

            val nodeEntities = buildList {
                add(
                    KnowledgeNodeEntity(
                        id = LocalDbConstants.NODE_DERIVATIVE,
                        contentId = LocalDbConstants.CONTENT_TREE_ROOT_ID,
                        title = "导数与单调性",
                        description = "围绕导数符号、单调区间、极值判断形成的核心学习节点",
                        mastery = 42,
                        status = "focus",
                        createdAt = now,
                        updatedAt = now
                    )
                )
                MockData.knowledgeNodes.forEach { n ->
                    add(
                        KnowledgeNodeEntity(
                            id = n.id,
                            contentId = LocalDbConstants.CONTENT_TREE_ROOT_ID,
                            title = n.label,
                            description = n.label,
                            mastery = n.progress,
                            status = n.type,
                            createdAt = now,
                            updatedAt = now
                        )
                    )
                }
            }
            knowledgeNodeDao.upsertNodes(nodeEntities)

            val titleToId = nodeEntities.associate { it.title to it.id }
            fun resolveId(title: String): String =
                titleToId[title] ?: title

            val relationEntities = listOf(
                KnowledgeRelationEntity(
                    id = "rel_fun_def",
                    contentId = LocalDbConstants.CONTENT_TREE_ROOT_ID,
                    fromNodeId = resolveId("函数基础"),
                    fromTitle = "函数基础",
                    toNodeId = resolveId("导数定义"),
                    toTitle = "导数定义",
                    relationType = "前置"
                ),
                KnowledgeRelationEntity(
                    id = "rel_def_der",
                    contentId = LocalDbConstants.CONTENT_TREE_ROOT_ID,
                    fromNodeId = resolveId("导数定义"),
                    fromTitle = "导数定义",
                    toNodeId = LocalDbConstants.NODE_DERIVATIVE,
                    toTitle = "导数与单调性",
                    relationType = "前置"
                ),
                KnowledgeRelationEntity(
                    id = "rel_sym_mon",
                    contentId = LocalDbConstants.CONTENT_TREE_ROOT_ID,
                    fromNodeId = resolveId("导数符号"),
                    fromTitle = "导数符号",
                    toNodeId = resolveId("单调区间"),
                    toTitle = "单调区间",
                    relationType = "影响"
                ),
                KnowledgeRelationEntity(
                    id = "rel_mon_ext",
                    contentId = LocalDbConstants.CONTENT_TREE_ROOT_ID,
                    fromNodeId = resolveId("单调区间"),
                    fromTitle = "单调区间",
                    toNodeId = resolveId("极值判断"),
                    toTitle = "极值判断",
                    relationType = "前置"
                ),
                KnowledgeRelationEntity(
                    id = "rel_der_par",
                    contentId = LocalDbConstants.CONTENT_TREE_ROOT_ID,
                    fromNodeId = LocalDbConstants.NODE_DERIVATIVE,
                    fromTitle = "导数与单调性",
                    toNodeId = resolveId("参数讨论"),
                    toTitle = "参数讨论",
                    relationType = "扩展"
                )
            )
            knowledgeRelationDao.upsertRelations(relationEntities)

            val chatSeeds = MockData.initialChatMessages.mapIndexed { index, msg ->
                ChatMessageEntity(
                    id = msg.id,
                    nodeId = LocalDbConstants.NODE_DERIVATIVE,
                    role = msg.role,
                    content = msg.content,
                    createdAt = now + index
                )
            }
            chatMessageDao.insertMessages(chatSeeds)
        }
    }

    suspend fun addChatMessage(nodeId: String, role: String, content: String) {
        val entity = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            nodeId = nodeId,
            role = role,
            content = content,
            createdAt = System.currentTimeMillis()
        )
        chatMessageDao.insertMessage(entity)
    }

    suspend fun saveFeynmanReview(
        nodeId: String,
        question: String,
        userAnswer: String,
        result: FeynmanEvaluationResult
    ) {
        val now = System.currentTimeMillis()
        val record = ReviewRecordEntity(
            id = UUID.randomUUID().toString(),
            nodeId = nodeId,
            question = question,
            userAnswer = userAnswer,
            score = result.score,
            level = result.level,
            strengths = result.strengths.joinToString("\n"),
            weaknesses = result.weaknesses.joinToString("\n"),
            suggestions = result.suggestions.joinToString("\n"),
            masteryBefore = result.masteryBefore,
            masteryAfter = result.masteryAfter,
            createdAt = now
        )
        reviewRecordDao.insertRecord(record)
        val masteryRecord = MasteryRecordEntity(
            id = UUID.randomUUID().toString(),
            nodeId = nodeId,
            masteryBefore = result.masteryBefore,
            masteryAfter = result.masteryAfter,
            reason = "费曼复述评分",
            createdAt = now
        )
        masteryRecordDao.insertRecord(masteryRecord)
        val newStatus = when {
            result.masteryAfter >= 70 -> "good"
            result.masteryAfter >= 50 -> "medium"
            else -> "weak"
        }
        val nodeStatus = if (nodeId == LocalDbConstants.NODE_DERIVATIVE) "focus" else newStatus
        knowledgeNodeDao.updateMastery(
            nodeId = nodeId,
            mastery = result.masteryAfter,
            status = nodeStatus,
            updatedAt = now
        )
        if (nodeId == LocalDbConstants.NODE_DERIVATIVE) {
            learningContentDao.upsertContent(
                LearningContentEntity(
                    id = LocalDbConstants.NODE_DERIVATIVE,
                    title = "导数与单调性",
                    sourceType = LocalDbConstants.SOURCE_LEARNING,
                    rawText = learningMetaJson(result.masteryAfter, newStatus),
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }

    suspend fun getLatestImportOnce(): ImportRecordEntity? = importRecordDao.getLatestImportOnce()

    suspend fun importAndPersist(
        title: String,
        rawText: String,
        importSource: ImportSource,
        fileUri: String? = null,
        imageUri: String? = null
    ) {
        val id = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val resolvedTitle = title.trim().ifBlank {
            rawText.lineSequence().firstOrNull { it.isNotBlank() }?.take(32)?.trim() ?: "导入内容"
        }
        database.withTransaction {
            importRecordDao.insertRecord(
                ImportRecordEntity(
                    id = id,
                    sourceType = importSource.name,
                    title = resolvedTitle,
                    rawText = rawText,
                    fileUri = fileUri,
                    imageUri = imageUri,
                    createdAt = now
                )
            )
            learningContentDao.upsertContent(
                LearningContentEntity(
                    id = id,
                    title = resolvedTitle,
                    sourceType = LocalDbConstants.SOURCE_IMPORTED,
                    rawText = learningMetaJson(0, "medium"),
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }

    suspend fun updateNodeMastery(
        nodeId: String,
        masteryBefore: Int,
        masteryAfter: Int,
        reason: String
    ) {
        val now = System.currentTimeMillis()
        masteryRecordDao.insertRecord(
            MasteryRecordEntity(
                id = UUID.randomUUID().toString(),
                nodeId = nodeId,
                masteryBefore = masteryBefore,
                masteryAfter = masteryAfter,
                reason = reason,
                createdAt = now
            )
        )
        val status = when {
            masteryAfter >= 70 -> "good"
            masteryAfter >= 50 -> "medium"
            else -> "weak"
        }
        knowledgeNodeDao.updateMastery(nodeId, masteryAfter, status, now)
    }
}
