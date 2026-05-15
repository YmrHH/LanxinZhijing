package com.lanxin.zhijing.data.ai

import com.lanxin.zhijing.BuildConfig

/**
 * 根据 [BuildConfig] 装配 AI 仓库：未配置后端 URL 时仅 Mock；配置后走 [BackendProxyAiLearningRepository]，
 * 且默认包一层 [FallbackAiLearningRepository] 以便联调失败时仍可用本地逻辑。
 */
object AiRepositoryFactory {

    fun create(): AiLearningRepository {
        val base = BuildConfig.AI_BACKEND_BASE_URL.trim()
        val mock = MockAiLearningRepository()
        if (base.isEmpty()) return mock
        val remote = BackendProxyAiLearningRepository(baseUrl = base)
        return if (BuildConfig.AI_BACKEND_FALLBACK_TO_MOCK) {
            FallbackAiLearningRepository(remote, mock)
        } else {
            remote
        }
    }
}
