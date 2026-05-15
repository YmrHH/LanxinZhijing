package com.lanxin.zhijing.data.ai

interface AiLearningRepository {

    suspend fun analyzeLearningContent(
        text: String,
        sourceType: ImportSource
    ): Result<LearningAnalysisResult>

    suspend fun askNodeQuestion(
        nodeContext: NodeQuestionContext,
        question: String
    ): Result<String>

    suspend fun generateStepHints(
        nodeContext: NodeQuestionContext
    ): Result<List<String>>

    suspend fun evaluateFeynmanAnswer(
        request: FeynmanEvaluationRequest
    ): Result<FeynmanEvaluationResult>
}
