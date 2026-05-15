package com.lanxin.zhijing.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lanxin.zhijing.data.local.entity.LearningContentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningContentDao {

    @Query("SELECT COUNT(*) FROM learning_content")
    suspend fun countContents(): Int

    @Query("SELECT * FROM learning_content")
    fun getAllContents(): Flow<List<LearningContentEntity>>

    @Query("SELECT * FROM learning_content WHERE id = :id")
    fun getContentById(id: String): Flow<LearningContentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertContent(entity: LearningContentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertContents(entities: List<LearningContentEntity>)

    @Query("DELETE FROM learning_content WHERE id = :id")
    suspend fun deleteContentById(id: String)
}
