package com.lanxin.zhijing.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mastery_record")
data class MasteryRecordEntity(
    @PrimaryKey val id: String,
    val nodeId: String,
    val masteryBefore: Int,
    val masteryAfter: Int,
    val reason: String,
    val createdAt: Long
)
