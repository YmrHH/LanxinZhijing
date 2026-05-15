package com.lanxin.zhijing.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lanxin.zhijing.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    @Query("SELECT * FROM chat_message WHERE nodeId = :nodeId ORDER BY createdAt ASC")
    fun getMessagesByNodeId(nodeId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(entity: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(entities: List<ChatMessageEntity>)

    @Query("DELETE FROM chat_message WHERE nodeId = :nodeId")
    suspend fun clearMessagesByNodeId(nodeId: String)
}
