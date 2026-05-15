package com.lanxin.zhijing.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "import_record")
data class ImportRecordEntity(
    @PrimaryKey val id: String,
    val sourceType: String,
    val title: String,
    val rawText: String,
    val fileUri: String?,
    val imageUri: String?,
    val createdAt: Long
)
