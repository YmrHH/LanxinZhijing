package com.lanxin.zhijing.data.importutil

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.lanxin.zhijing.data.ai.ImportSource
import kotlin.text.Charsets

object TextImportHelper {
    private const val MAX_BYTES = DocumentImportHelper.MAX_BYTES

    fun readUri(context: Context, uri: Uri): String? =
        when (val result = DocumentImportHelper.extract(context, uri)) {
            is DocumentImportHelper.ExtractResult.Text -> result.content
            else -> null
        }

    fun queryDisplayName(context: Context, uri: Uri): String? =
        try {
            context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { c ->
                if (c.moveToFirst()) c.getString(0) else null
            }
        } catch (_: Exception) {
            null
        }

    fun imagePlaceholderBody(displayName: String, source: ImportSource): String {
        val sourceLabel = when (source) {
            ImportSource.PHOTO_WRONG_QUESTION -> "拍照错题"
            ImportSource.SCREENSHOT -> "截图/相册图片"
            ImportSource.PASTE_TEXT -> "文本"
            ImportSource.TEXTBOOK_OR_NOTES -> "文件/图片"
        }
        return buildString {
            appendLine("[图片内容待识别]")
            appendLine("来源：$sourceLabel")
            appendLine("文件：$displayName")
            appendLine()
            append("若未自动识别出文字，请在此补充；正式 OCR 能力随 V0.4 持续增强。")
        }
    }
}
