package com.lanxin.zhijing.data.ai

import com.lanxin.zhijing.data.MockData

/**
 * V0.1 演示用固定上下文，与 PLAN 中「导数与单调性」主线一致。
 */
fun demoNodeQuestionContext(): NodeQuestionContext {
    val related = MockData.knowledgeNodes.filter { it.id in setOf("symbol", "monotonic", "extreme") }
    return NodeQuestionContext(
        nodeId = "derivative",
        nodeTitle = "导数与单调性",
        nodeDescription = "围绕导数符号与单调区间的理解",
        mastery = 42,
        relatedNodes = related,
        recentMistakes = listOf("导数符号与单调区间混淆"),
        recentReviewFeedback = listOf("复述时能说出变化趋势，缺少切线斜率表述")
    )
}
