package com.lanxin.zhijing.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lanxin.zhijing.data.local.entity.KnowledgeRelationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KnowledgeRelationDao {

    @Query("SELECT * FROM knowledge_relation WHERE contentId = :contentId")
    fun getRelationsByContentId(contentId: String): Flow<List<KnowledgeRelationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRelations(entities: List<KnowledgeRelationEntity>)
}
