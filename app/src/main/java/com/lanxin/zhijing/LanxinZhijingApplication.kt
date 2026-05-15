package com.lanxin.zhijing

import android.app.Application
import com.lanxin.zhijing.data.ai.AiLearningRepository
import com.lanxin.zhijing.data.ai.AiRepositoryFactory
import com.lanxin.zhijing.data.local.DatabaseProvider
import com.lanxin.zhijing.data.repository.LearningRepository

class LanxinZhijingApplication : Application() {

    val database by lazy { DatabaseProvider.get(this) }
    val learningRepository by lazy { LearningRepository(database) }
    val aiLearningRepository: AiLearningRepository by lazy { AiRepositoryFactory.create() }
}
