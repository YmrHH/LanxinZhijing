package com.lanxin.zhijing.data.importutil

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import com.lanxin.zhijing.data.ai.ImportSource

object ImportIntentParser {

    suspend fun parse(context: Context, intent: Intent?): PendingImport? {
        if (intent == null) return null
        return when (intent.action) {
            Intent.ACTION_SEND -> parseSend(context, intent)
            Intent.ACTION_PROCESS_TEXT -> parseProcessText(intent)
            else -> null
        }
    }

    private fun parseProcessText(intent: Intent): PendingImport? {
        val text = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT)?.trim().orEmpty()
        if (text.isEmpty()) return null
        val title = text.lineSequence().firstOrNull { it.isNotBlank() }?.take(32) ?: "划词分析"
        return PendingImport(
            title = title,
            body = text,
            source = ImportSource.PASTE_TEXT
        )
    }

    private suspend fun parseSend(context: Context, intent: Intent): PendingImport? {
        val type = intent.type.orEmpty()
        if (type.startsWith("text/")) {
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)?.trim().orEmpty()
            if (text.isEmpty()) return null
            val title = text.lineSequence().firstOrNull { it.isNotBlank() }?.take(32) ?: "分享文本"
            return PendingImport(title = title, body = text, source = ImportSource.PASTE_TEXT)
        }
        val stream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(Intent.EXTRA_STREAM)
        }
        if (stream != null) {
            when (val extracted = DocumentImportHelper.extract(context, stream)) {
                is DocumentImportHelper.ExtractResult.Text -> {
                    val name = TextImportHelper.queryDisplayName(context, stream) ?: "分享文件"
                    return PendingImport(
                        title = name,
                        body = extracted.content,
                        source = ImportSource.TEXTBOOK_OR_NOTES,
                        fileUri = stream.toString()
                    )
                }
                is DocumentImportHelper.ExtractResult.Image -> {
                    return buildImagePendingWithOcr(context, stream, ImportSource.SCREENSHOT, "分享图片")
                }
                is DocumentImportHelper.ExtractResult.Failed -> {
                    val name = TextImportHelper.queryDisplayName(context, stream) ?: "分享文件"
                    return PendingImport(
                        title = name,
                        body = DocumentImportHelper.unsupportedFileBody(name, extracted.message),
                        source = ImportSource.TEXTBOOK_OR_NOTES,
                        fileUri = stream.toString()
                    )
                }
            }
        }
        return null
    }

    private suspend fun buildImagePendingWithOcr(
        context: Context,
        uri: Uri,
        source: ImportSource,
        defaultTitle: String
    ): PendingImport {
        val name = TextImportHelper.queryDisplayName(context, uri) ?: defaultTitle
        val ocr = ImageOcrHelper.recognizeText(context, uri)
        val body = ocr ?: TextImportHelper.imagePlaceholderBody(name, source)
        return PendingImport(
            title = name,
            body = body,
            source = source,
            imageUri = uri.toString()
        )
    }
}
