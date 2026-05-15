package com.lanxin.zhijing

import android.app.Application
import com.lanxin.zhijing.data.local.DatabaseProvider
import com.lanxin.zhijing.data.repository.LearningRepository
import kotlinx.coroutines.runBlocking

class LanxinZhijingApplication : Application() {

    val database by lazy { DatabaseProvider.get(this) }
    val learningRepository by lazy { LearningRepository(database) }

    override fun onCreate() {
        super.onCreate()
        runBlocking {
            learningRepository.initializeIfNeeded()
        }
    }
}
