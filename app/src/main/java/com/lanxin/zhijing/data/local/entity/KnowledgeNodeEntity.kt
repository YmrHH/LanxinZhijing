package com.lanxin.zhijing.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "knowledge_node")
data class KnowledgeNodeEntity(
    @PrimaryKey val id: String,
    val contentId: String,
    val title: String,
    val description: String,
    val mastery: Int,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long
)
