package com.lanxin.zhijing.data.ai

import com.lanxin.zhijing.data.KnowledgeNode
import com.lanxin.zhijing.data.KnowledgeRelation
import com.lanxin.zhijing.data.MockData

data class LearningAnalysisResult(
    val contentType: String,
    val coreTopic: String,
    val relatedKnowledgePoints: List<String>,
    val possibleWeakness: String,
    val suggestedPath: List<String>,
    val nodes: List<KnowledgeNode>,
    val relations: List<KnowledgeRelation>
)

data class NodeQuestionContext(
    val nodeId: String,
    val nodeTitle: String,
    val nodeDescription: String,
    val mastery: Int,
    val relatedNodes: List<KnowledgeNode>,
    val recentMistakes: List<String>,
    val recentReviewFeedback: List<String>
)

data class FeynmanEvaluationRequest(
    val nodeId: String,
    val nodeTitle: String,
    val question: String,
    val userAnswer: String,
    val masteryBefore: Int
)

data class FeynmanEvaluationResult(
    val score: Int,
    val level: String,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val suggestions: List<String>,
    val masteryBefore: Int,
    val masteryAfter: Int,
    val nextTasks: List<String>
)

/** 无导入或分析失败时的内置占位，与 Mock 默认主线一致 */
fun builtInDefaultLearningAnalysis(): LearningAnalysisResult =
    LearningAnalysisResult(
        contentType = "数学错题",
        coreTopic = "导数与函数单调性",
        relatedKnowledgePoints = MockData.analysisTags,
        possibleWeakness = "你可能不是不会求导，而是不熟悉「导数符号变化」和「函数增减性」的关系。",
        suggestedPath = listOf("导数定义", "几何意义", "导数符号", "单调性", "极值判断"),
        nodes = MockData.knowledgeNodes,
        relations = MockData.relations
    )
