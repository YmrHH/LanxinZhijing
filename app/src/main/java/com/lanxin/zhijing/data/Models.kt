package com.lanxin.zhijing.data

data class LearningItem(
    val id: String,
    val title: String,
    val progress: Int,
    val status: String
)

data class KnowledgeNode(
    val id: String,
    val label: String,
    val progress: Int,
    val type: String
)

data class KnowledgeRelation(
    val from: String,
    val relation: String,
    val to: String
)

data class ChatMessage(
    val role: String,
    val content: String
)
