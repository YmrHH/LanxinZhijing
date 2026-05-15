package com.lanxin.zhijing.navigation

object Routes {
    const val HOME = "home"
    const val KNOWLEDGE_TREE = "knowledge_tree"
    const val REVIEW = "review"
    const val PROFILE = "profile"
    const val ANALYSIS = "analysis"
    const val IMPORT_PREVIEW = "import_preview"
    const val CAMERA_CAPTURE = "camera_capture"
    const val NODE_FOCUS_PATTERN = "node_focus/{nodeId}"

    fun nodeFocus(nodeId: String): String = "node_focus/$nodeId"
}
