package com.lanxin.zhijing.data.ai

import com.lanxin.zhijing.data.MockData

/**
 * V0.1：返回与产品主线一致的固定数据，不发起网络请求。
 */
class MockAiLearningRepository : AiLearningRepository {

    override suspend fun analyzeLearningContent(
        text: String,
        sourceType: ImportSource
    ): Result<LearningAnalysisResult> {
        return Result.success(
            LearningAnalysisResult(
                contentType = "数学错题",
                coreTopic = "导数与函数单调性",
                relatedKnowledgePoints = MockData.analysisTags,
                possibleWeakness = "你可能不是不会求导，而是不熟悉「导数符号变化」和「函数增减性」的关系。",
                suggestedPath = listOf("导数定义", "几何意义", "导数符号", "单调性", "极值判断"),
                nodes = MockData.knowledgeNodes,
                relations = MockData.relations
            )
        )
    }

    override suspend fun askNodeQuestion(
        nodeContext: NodeQuestionContext,
        question: String
    ): Result<String> {
        return Result.success(MockData.mockAiFollowUpReply)
    }

    override suspend fun generateStepHints(
        nodeContext: NodeQuestionContext
    ): Result<List<String>> {
        return Result.success(
            listOf(
                "提示 1：先判断导数符号",
                "提示 2：再看该符号在区间内是否稳定",
                "提示 3：如果 f'(x)>0，函数在该区间递增；如果 f'(x)<0，函数在该区间递减。"
            )
        )
    }

    override suspend fun evaluateFeynmanAnswer(
        request: FeynmanEvaluationRequest
    ): Result<FeynmanEvaluationResult> {
        return Result.success(
            FeynmanEvaluationResult(
                score = 78,
                level = "基本理解",
                strengths = listOf("说出了导数和函数变化趋势有关。"),
                weaknesses = listOf(
                    "可以补充「切线斜率」的解释",
                    "判断单调性时，要看区间内导数符号是否稳定",
                    "还没有说明导数符号变化与极值点的关系"
                ),
                suggestions = listOf("补充导数几何意义", "完成 2 道同类题"),
                masteryBefore = request.masteryBefore,
                masteryAfter = 68,
                nextTasks = listOf("完成 2 道同类题", "复习导数符号与单调区间")
            )
        )
    }
}
