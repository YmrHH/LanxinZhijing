package com.lanxin.zhijing.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lanxin.zhijing.data.local.entity.ImportRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImportRecordDao {

    @Query("SELECT * FROM import_record ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestImportOnce(): ImportRecordEntity?

    @Query("SELECT * FROM import_record ORDER BY createdAt DESC")
    fun getAllImportRecords(): Flow<List<ImportRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(entity: ImportRecordEntity)
}
