package com.lanxin.zhijing.data.ai

import com.lanxin.zhijing.data.KnowledgeNode
import com.lanxin.zhijing.data.KnowledgeRelation

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
