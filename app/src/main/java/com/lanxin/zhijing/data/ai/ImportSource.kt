package com.lanxin.zhijing.data.ai

/**
 * 用户导入学习内容的来源类型（供 AI 分析入参）。
 */
enum class ImportSource {
    PHOTO_WRONG_QUESTION,
    SCREENSHOT,
    PASTE_TEXT,
    TEXTBOOK_OR_NOTES
}
