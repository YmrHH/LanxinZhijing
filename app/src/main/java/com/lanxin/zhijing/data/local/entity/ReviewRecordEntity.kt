package com.lanxin.zhijing.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "review_record")
data class ReviewRecordEntity(
    @PrimaryKey val id: String,
    val nodeId: String,
    val question: String,
    val userAnswer: String,
    val score: Int,
    val level: String,
    val strengths: String,
    val weaknesses: String,
    val suggestions: String,
    val masteryBefore: Int,
    val masteryAfter: Int,
    val createdAt: Long
)
