package com.lanxin.zhijing.data.ai

import com.lanxin.zhijing.BuildConfig

/**
 * 根据 [BuildConfig] 装配 AI 仓库：
 * 1. 若配置了 `ai.backend.baseUrl` → [BackendProxyAiLearningRepository]（可选 Fallback Mock）
 * 2. 否则若配置了 vivo AppID/AppKEY → [VivoLanxinAiRepository] 直连 AIGC（可选 Fallback）
 * 3. 否则 [MockAiLearningRepository]
 */
object AiRepositoryFactory {

    fun create(): AiLearningRepository {
        val mock = MockAiLearningRepository()
        val base = BuildConfig.AI_BACKEND_BASE_URL.trim()
        if (base.isNotEmpty()) {
            val remote = BackendProxyAiLearningRepository(baseUrl = base)
            return if (BuildConfig.AI_BACKEND_FALLBACK_TO_MOCK) {
                FallbackAiLearningRepository(remote, mock)
            } else {
                remote
            }
        }
        val vivoId = BuildConfig.VIVO_AIGC_APP_ID.trim()
        val vivoKey = BuildConfig.VIVO_AIGC_APP_KEY.trim()
        if (vivoId.isNotEmpty() && vivoKey.isNotEmpty()) {
            val vivo = VivoLanxinAiRepository()
            return if (BuildConfig.AI_BACKEND_FALLBACK_TO_MOCK) {
                FallbackAiLearningRepository(vivo, mock)
            } else {
                vivo
            }
        }
        return mock
    }
}
