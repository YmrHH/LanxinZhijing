package com.lanxin.zhijing.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lanxin.zhijing.data.local.dao.ChatMessageDao
import com.lanxin.zhijing.data.local.dao.ImportRecordDao
import com.lanxin.zhijing.data.local.dao.KnowledgeNodeDao
import com.lanxin.zhijing.data.local.dao.KnowledgeRelationDao
import com.lanxin.zhijing.data.local.dao.LearningContentDao
import com.lanxin.zhijing.data.local.dao.MasteryRecordDao
import com.lanxin.zhijing.data.local.dao.ReviewRecordDao
import com.lanxin.zhijing.data.local.entity.ChatMessageEntity
import com.lanxin.zhijing.data.local.entity.ImportRecordEntity
import com.lanxin.zhijing.data.local.entity.KnowledgeNodeEntity
import com.lanxin.zhijing.data.local.entity.KnowledgeRelationEntity
import com.lanxin.zhijing.data.local.entity.LearningContentEntity
import com.lanxin.zhijing.data.local.entity.MasteryRecordEntity
import com.lanxin.zhijing.data.local.entity.ReviewRecordEntity

@Database(
    entities = [
        LearningContentEntity::class,
        KnowledgeNodeEntity::class,
        KnowledgeRelationEntity::class,
        ChatMessageEntity::class,
        ReviewRecordEntity::class,
        MasteryRecordEntity::class,
        ImportRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun learningContentDao(): LearningContentDao
    abstract fun knowledgeNodeDao(): KnowledgeNodeDao
    abstract fun knowledgeRelationDao(): KnowledgeRelationDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun reviewRecordDao(): ReviewRecordDao
    abstract fun masteryRecordDao(): MasteryRecordDao
    abstract fun importRecordDao(): ImportRecordDao
}

object DatabaseProvider {
    private const val DB_NAME = "lanxin_zhijing.db"

    @Volatile
    private var instance: AppDatabase? = null

    fun get(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME
            ).build().also { instance = it }
        }
    }
}
