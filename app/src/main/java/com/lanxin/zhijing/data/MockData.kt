package com.lanxin.zhijing.data

object MockData {
    val learningItems: List<LearningItem> = listOf(
        LearningItem("derivative", "导数与单调性", 42, "weak"),
        LearningItem("economy", "经济基础与上层建筑", 68, "good"),
        LearningItem("cell", "细胞呼吸", 55, "medium"),
        LearningItem("industry", "工业革命", 73, "good")
    )

    val knowledgeNodes: List<KnowledgeNode> = listOf(
        KnowledgeNode("function", "函数基础", 80, "good"),
        KnowledgeNode("definition", "导数定义", 70, "good"),
        KnowledgeNode("geometry", "几何意义", 55, "medium"),
        KnowledgeNode("symbol", "导数符号", 45, "medium"),
        KnowledgeNode("monotonic", "单调区间", 42, "focus"),
        KnowledgeNode("extreme", "极值判断", 38, "weak"),
        KnowledgeNode("parameter", "参数讨论", 25, "weak")
    )

    val relations: List<KnowledgeRelation> = listOf(
        KnowledgeRelation("函数基础", "前置", "导数定义"),
        KnowledgeRelation("导数符号", "影响", "单调区间"),
        KnowledgeRelation("单调区间", "前置", "极值判断")
    )

    val initialChatMessages: List<ChatMessage> = listOf(
        ChatMessage(
            "AI",
            "你现在卡在“导数符号如何影响函数走势”。建议先理解：f'(x)>0 表示函数在该区间内整体上升。"
        ),
        ChatMessage("USER", "为什么 f'(x)>0 时，函数就是递增的？"),
        ChatMessage(
            "AI",
            "可以把导数理解成函数图像在某一点的倾斜方向。当 f'(x)>0 时，切线斜率为正，图像向右上方延伸。"
        )
    )

    const val mockAiFollowUpReply: String =
        "这个问题仍然和导数符号、单调区间有关，我建议先回到导数的几何意义理解。"

    val analysisTags: List<String> = listOf(
        "导数计算",
        "导数符号",
        "单调区间",
        "极值判断",
        "参数讨论"
    )

    val graphEdges: List<Pair<String, String>> = listOf(
        "function" to "definition",
        "definition" to "center",
        "symbol" to "monotonic",
        "monotonic" to "extreme",
        "center" to "parameter"
    )
}
