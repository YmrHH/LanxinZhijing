package com.lanxin.zhijing.data.ai

/**
 * V0.5：直连 vivo 客户端 SDK/HTTP **不推荐**（AppKEY 须在服务端）。
 * 客户端请使用 [BackendProxyAiLearningRepository] + [AiRepositoryFactory]（`local.properties` 配置 `ai.backend.baseUrl`），
 * 由自有后端按官方文档调用蓝心。
 *
 * 文档入口：https://aigc.vivo.com.cn/#/document/index?id=1746
 */
class VivoLanxinAiRepository : AiLearningRepository {

    private val notImplemented =
        IllegalStateException(
            "V0.5 起按 vivo 官方文档接入蓝心大模型；请求地址、鉴权与 Body 以文档为准，勿在客户端硬编码 AppKEY。"
        )

    override suspend fun analyzeLearningContent(
        text: String,
        sourceType: ImportSource
    ): Result<LearningAnalysisResult> = Result.failure(notImplemented)

    override suspend fun askNodeQuestion(
        nodeContext: NodeQuestionContext,
        question: String
    ): Result<String> = Result.failure(notImplemented)

    override suspend fun generateStepHints(
        nodeContext: NodeQuestionContext
    ): Result<List<String>> = Result.failure(notImplemented)

    override suspend fun evaluateFeynmanAnswer(
        request: FeynmanEvaluationRequest
    ): Result<FeynmanEvaluationResult> = Result.failure(notImplemented)
}
