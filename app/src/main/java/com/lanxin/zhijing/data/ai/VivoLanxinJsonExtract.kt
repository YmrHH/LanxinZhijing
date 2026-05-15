package com.lanxin.zhijing.data.ai

/** 从模型回复中取出 JSON 对象子串（去掉 ```json 围栏等）。 */
internal fun extractJsonObject(raw: String): String {
    var s = raw.trim()
    if (s.startsWith("```")) {
        val firstNl = s.indexOf('\n')
        if (firstNl >= 0) s = s.substring(firstNl + 1)
        val fence = s.lastIndexOf("```")
        if (fence >= 0) s = s.substring(0, fence).trim()
    }
    val start = s.indexOf('{')
    val end = s.lastIndexOf('}')
    return if (start >= 0 && end > start) s.substring(start, end + 1) else s
}
