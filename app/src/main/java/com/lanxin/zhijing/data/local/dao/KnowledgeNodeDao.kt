package com.lanxin.zhijing.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lanxin.zhijing.data.local.entity.KnowledgeNodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KnowledgeNodeDao {

    @Query("SELECT * FROM knowledge_node WHERE contentId = :contentId ORDER BY id")
    fun getNodesByContentId(contentId: String): Flow<List<KnowledgeNodeEntity>>

    @Query("SELECT * FROM knowledge_node ORDER BY contentId, id")
    fun getAllNodes(): Flow<List<KnowledgeNodeEntity>>

    @Query("SELECT * FROM knowledge_node WHERE id = :id")
    fun getNodeById(id: String): Flow<KnowledgeNodeEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertNode(entity: KnowledgeNodeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertNodes(entities: List<KnowledgeNodeEntity>)

    @Query(
        "UPDATE knowledge_node SET mastery = :mastery, status = :status, updatedAt = :updatedAt WHERE id = :nodeId"
    )
    suspend fun updateMastery(nodeId: String, mastery: Int, status: String, updatedAt: Long)
}
