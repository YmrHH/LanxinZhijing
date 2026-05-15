package com.lanxin.zhijing.data.ai

/**
 * V0.5：按 vivo AIGC 官方文档实现真实调用；当前仅占位，不发起网络、不包含任何密钥。
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
