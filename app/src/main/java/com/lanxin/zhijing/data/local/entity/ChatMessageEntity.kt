package com.lanxin.zhijing.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_message")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val nodeId: String,
    val role: String,
    val content: String,
    val createdAt: Long
)
