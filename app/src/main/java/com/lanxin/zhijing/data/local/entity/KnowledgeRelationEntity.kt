package com.lanxin.zhijing.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "knowledge_relation")
data class KnowledgeRelationEntity(
    @PrimaryKey val id: String,
    val contentId: String,
    val fromNodeId: String,
    val fromTitle: String,
    val toNodeId: String,
    val toTitle: String,
    val relationType: String
)
