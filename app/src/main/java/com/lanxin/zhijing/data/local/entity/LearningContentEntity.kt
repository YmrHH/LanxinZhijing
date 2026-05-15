package com.lanxin.zhijing.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "learning_content")
data class LearningContentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val sourceType: String,
    val rawText: String,
    val createdAt: Long,
    val updatedAt: Long
)
