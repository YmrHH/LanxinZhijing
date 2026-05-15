package com.lanxin.zhijing.data.ai

/**
 * 先走 [primary]；失败时走 [fallback]（通常为 Mock），满足 PLAN §12.7 可选降级。
 */
class FallbackAiLearningRepository(
    private val primary: AiLearningRepository,
    private val fallback: AiLearningRepository
) : AiLearningRepository {

    override suspend fun analyzeLearningContent(
        text: String,
        sourceType: ImportSource
    ): Result<LearningAnalysisResult> {
        val r = primary.analyzeLearningContent(text, sourceType)
        return if (r.isSuccess) r else fallback.analyzeLearningContent(text, sourceType)
    }

    override suspend fun askNodeQuestion(
        nodeContext: NodeQuestionContext,
        question: String
    ): Result<String> {
        val r = primary.askNodeQuestion(nodeContext, question)
        return if (r.isSuccess) r else fallback.askNodeQuestion(nodeContext, question)
    }

    override suspend fun generateStepHints(
        nodeContext: NodeQuestionContext
    ): Result<List<String>> {
        val r = primary.generateStepHints(nodeContext)
        return if (r.isSuccess) r else fallback.generateStepHints(nodeContext)
    }

    override suspend fun evaluateFeynmanAnswer(
        request: FeynmanEvaluationRequest
    ): Result<FeynmanEvaluationResult> {
        val r = primary.evaluateFeynmanAnswer(request)
        return if (r.isSuccess) r else fallback.evaluateFeynmanAnswer(request)
    }
}
