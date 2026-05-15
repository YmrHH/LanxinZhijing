package com.lanxin.zhijing.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lanxin.zhijing.data.local.entity.MasteryRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MasteryRecordDao {

    @Query("SELECT * FROM mastery_record WHERE nodeId = :nodeId ORDER BY createdAt DESC")
    fun getRecordsByNodeId(nodeId: String): Flow<List<MasteryRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(entity: MasteryRecordEntity)
}
