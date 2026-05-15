package com.lanxin.zhijing.data.local

import com.lanxin.zhijing.data.ChatMessage
import com.lanxin.zhijing.data.KnowledgeNode
import com.lanxin.zhijing.data.KnowledgeRelation
import com.lanxin.zhijing.data.LearningItem
import com.lanxin.zhijing.data.local.entity.ChatMessageEntity
import com.lanxin.zhijing.data.local.entity.KnowledgeNodeEntity
import com.lanxin.zhijing.data.local.entity.KnowledgeRelationEntity
import com.lanxin.zhijing.data.local.entity.LearningContentEntity

fun LearningContentEntity.toLearningItem(): LearningItem {
    if (sourceType == LocalDbConstants.SOURCE_TREE_ROOT) {
        return LearningItem(id, title, 0, "medium")
    }
    val (progress, status) = parseLearningMeta(rawText)
    return LearningItem(id = id, title = title, progress = progress, status = status)
}

private fun parseLearningMeta(rawText: String): Pair<Int, String> {
    if (!rawText.contains("\"progress\"")) return 0 to "medium"
    return try {
        val progress = rawText.substringAfter("\"progress\":").substringBefore(",").trim().toIntOrNull() ?: 0
        val status = rawText.substringAfter("\"status\":\"").substringBefore("\"").ifEmpty { "medium" }
        progress to status
    } catch (_: Exception) {
        0 to "medium"
    }
}

fun KnowledgeNodeEntity.toKnowledgeNode(): KnowledgeNode =
    KnowledgeNode(id = id, label = title, progress = mastery, type = status)

fun KnowledgeRelationEntity.toKnowledgeRelation(): KnowledgeRelation =
    KnowledgeRelation(from = fromTitle, relation = relationType, to = toTitle)

fun ChatMessageEntity.toChatMessage(): ChatMessage =
    ChatMessage(id = id, role = role, content = content)

fun learningMetaJson(progress: Int, status: String): String =
    """{"progress":$progress,"status":"$status"}"""
