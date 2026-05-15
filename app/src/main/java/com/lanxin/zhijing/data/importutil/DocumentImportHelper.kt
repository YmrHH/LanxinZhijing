package com.lanxin.zhijing.data.importutil

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import kotlin.text.Charsets
import kotlin.text.charset
import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream

/**
 * 从用户选择的文件中抽取可分析正文。
 *
 * - 图片：返回 [ExtractResult.Image]，由调用方在 IO 协程中配合 [ImageOcrHelper] 识别。
 * - **PDF**：不本地全文抽取（见 PLAN：延后至 V0.5 AI/后端），返回可读占位说明。
 */
object DocumentImportHelper {

    const val MAX_BYTES = 10 * 1024 * 1024

    private val PLAIN_TEXT_EXTENSIONS = setOf(
        "txt", "md", "markdown", "csv", "tsv", "json", "xml", "html", "htm",
        "log", "yaml", "yml", "ini", "properties", "tex", "css", "js", "ts",
        "kt", "kts", "java", "py", "c", "cpp", "h", "hpp", "sql", "rb", "go",
        "rs", "sh", "bat", "ps1", "swift", "scala", "php", "lua", "r", "m"
    )

    sealed class ExtractResult {
        data class Text(val content: String) : ExtractResult()
        data class Image(val displayName: String) : ExtractResult()
        data class Failed(val message: String) : ExtractResult()
    }

    fun extract(context: Context, uri: Uri): ExtractResult {
        val name = TextImportHelper.queryDisplayName(context, uri) ?: "导入文件"
        val mime = context.contentResolver.getType(uri).orEmpty()
        val ext = name.substringAfterLast('.', "").lowercase()

        if (mime.startsWith("image/") || ext in IMAGE_EXTENSIONS) {
            return ExtractResult.Image(name)
        }

        val bytes = readBytes(context, uri)
            ?: return ExtractResult.Failed("无法读取文件，请检查权限或重试。")
        if (bytes.size > MAX_BYTES) {
            return ExtractResult.Failed("文件超过 10MB 上限，请拆分或导出较小版本后再导入。")
        }

        return when (resolveFormat(ext, mime)) {
            DocumentFormat.PLAIN_TEXT -> extractPlain(bytes)
            DocumentFormat.PDF -> ExtractResult.Text(pdfDeferredToAiBody(name))
            DocumentFormat.DOCX -> extractDocx(bytes)
            DocumentFormat.EPUB -> extractEpub(bytes)
            DocumentFormat.ODT -> extractOdt(bytes)
            DocumentFormat.RTF -> extractRtf(bytes)
            DocumentFormat.DOC_LEGACY -> ExtractResult.Failed(
                "暂不支持旧版 .doc，请在 Word/WPS 中另存为 .docx 或 .pdf 后再导入。"
            )
            DocumentFormat.UNKNOWN -> extractUnknown(bytes, name)
        }.let { result ->
            when (result) {
                is ExtractResult.Text -> {
                    val trimmed = result.content.trim()
                    if (trimmed.isEmpty()) {
                        ExtractResult.Failed("未能从文件中提取到文字，请在预览页手动输入内容。")
                    } else {
                        ExtractResult.Text(trimmed.take(200_000))
                    }
                }
                else -> result
            }
        }
    }

    private enum class DocumentFormat {
        PLAIN_TEXT, PDF, DOCX, EPUB, ODT, RTF, DOC_LEGACY, UNKNOWN
    }

    private val IMAGE_EXTENSIONS = setOf(
        "jpg", "jpeg", "png", "gif", "webp", "bmp", "heic", "heif"
    )

    private fun resolveFormat(ext: String, mime: String): DocumentFormat = when {
        ext == "pdf" || mime == "application/pdf" -> DocumentFormat.PDF
        ext == "docx" || mime.contains("wordprocessingml") -> DocumentFormat.DOCX
        ext == "doc" || mime == "application/msword" -> DocumentFormat.DOC_LEGACY
        ext == "epub" || mime == "application/epub+zip" -> DocumentFormat.EPUB
        ext == "odt" || mime.contains("opendocument.text") -> DocumentFormat.ODT
        ext == "rtf" || mime == "application/rtf" || mime == "text/rtf" -> DocumentFormat.RTF
        ext in PLAIN_TEXT_EXTENSIONS || mime.startsWith("text/") -> DocumentFormat.PLAIN_TEXT
        else -> DocumentFormat.UNKNOWN
    }

    private fun readBytes(context: Context, uri: Uri): ByteArray? =
        try {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } catch (_: Exception) {
            null
        }

    private fun extractPlain(bytes: ByteArray): ExtractResult =
        ExtractResult.Text(decodeText(bytes))

    private fun extractUnknown(bytes: ByteArray, name: String): ExtractResult {
        val ext = name.substringAfterLast('.', "").lowercase()
        if (isZipMagic(bytes)) {
            val docx = extractDocx(bytes)
            if (docx is ExtractResult.Text) return docx
            val epub = extractEpub(bytes)
            if (epub is ExtractResult.Text) return epub
            val odt = extractOdt(bytes)
            if (odt is ExtractResult.Text) return odt
        }
        val asText = decodeText(bytes)
        if (asText.isNotBlank() && isMostlyPrintable(asText)) {
            return ExtractResult.Text(asText)
        }
        val hint = if (ext.isNotEmpty()) ".$ext" else "该类型"
        return ExtractResult.Failed(
            "暂不支持自动解析 $hint 文件。支持：txt、md、docx、epub、odt、rtf、常见文本；pdf 接入 AI（V0.5）后解析；图片可使用相册 OCR。"
        )
    }

    private fun pdfDeferredToAiBody(fileName: String): String =
        buildString {
            appendLine("[PDF · 待接入 AI 解析]")
            appendLine("文件：$fileName")
            appendLine()
            append("本地不再抽取 PDF 正文。接入蓝心或自有后端（V0.5）后，可由模型解析 PDF（含扫描版）。")
            appendLine()
            append("请在此粘贴正文，或先用 Word/WPS 导出为 .docx / .txt 再导入。")
        }

    private fun extractDocx(bytes: ByteArray): ExtractResult {
        val xml = readZipEntry(bytes, "word/document.xml")
            ?: return ExtractResult.Failed("不是有效的 Word .docx 文件。")
        return ExtractResult.Text(stripOfficeXml(xml))
    }

    private fun extractEpub(bytes: ByteArray): ExtractResult {
        val sb = StringBuilder()
        ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                val n = entry.name.lowercase()
                if (n.endsWith(".html") || n.endsWith(".xhtml") || n.endsWith(".htm")) {
                    val html = zip.readBytes().decodeToString()
                    sb.appendLine(stripHtml(html))
                    sb.appendLine()
                }
                entry = zip.nextEntry
            }
        }
        return ExtractResult.Text(sb.toString())
    }

    private fun extractOdt(bytes: ByteArray): ExtractResult {
        val xml = readZipEntry(bytes, "content.xml")
            ?: return ExtractResult.Failed("不是有效的 OpenDocument .odt 文件。")
        return ExtractResult.Text(stripOfficeXml(xml))
    }

    private fun extractRtf(bytes: ByteArray): ExtractResult {
        val raw = decodeText(bytes)
        var text = raw.replace(Regex("\\\\par\\s*"), "\n")
        text = text.replace(Regex("\\{[^{}]*}"), "")
        text = text.replace(Regex("\\\\[a-z]+-?\\d*\\s?"), "")
        return ExtractResult.Text(text)
    }

    private fun readZipEntry(bytes: ByteArray, path: String): String? {
        ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                if (entry.name == path) {
                    return zip.readBytes().decodeToString()
                }
                entry = zip.nextEntry
            }
        }
        return null
    }

    private fun stripOfficeXml(xml: String): String =
        xml.replace(Regex("</w:p>|</text:p>"), "\n")
            .replace(Regex("<w:br[^>]*/>"), "\n")
            .replace(Regex("<[^>]+>"), "")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace(Regex("\n{3,}"), "\n\n")
            .trim()

    private fun stripHtml(html: String): String =
        html.replace(Regex("<(script|style)[^>]*>[\\s\\S]*?</\\1>", RegexOption.IGNORE_CASE), "")
            .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
            .replace(Regex("</p>", RegexOption.IGNORE_CASE), "\n")
            .replace(Regex("<[^>]+>"), "")
            .replace(Regex("&nbsp;", RegexOption.IGNORE_CASE), " ")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&amp;", "&")
            .trim()

    private fun decodeText(bytes: ByteArray): String {
        if (bytes.size >= 3 &&
            bytes[0] == 0xEF.toByte() &&
            bytes[1] == 0xBB.toByte() &&
            bytes[2] == 0xBF.toByte()
        ) {
            return String(bytes, Charsets.UTF_8)
        }
        val utf8 = String(bytes, Charsets.UTF_8)
        if (!utf8.contains('\uFFFD')) return utf8
        return runCatching { String(bytes, charset("GBK")) }.getOrElse { utf8 }
    }

    private fun isZipMagic(bytes: ByteArray): Boolean =
        bytes.size >= 4 &&
            bytes[0] == 0x50.toByte() &&
            bytes[1] == 0x4B.toByte()

    private fun isMostlyPrintable(text: String): Boolean {
        if (text.isEmpty()) return false
        val printable = text.count { ch -> ch.isWhitespace() || ch.code in 32..126 || ch.code > 126 }
        return printable.toFloat() / text.length >= 0.85f
    }

    fun unsupportedFileBody(fileName: String, reason: String): String =
        buildString {
            appendLine("[未能自动提取正文]")
            appendLine("文件：$fileName")
            appendLine("原因：$reason")
            appendLine()
            append("请在此手动粘贴或输入内容后提交分析。")
        }

    fun guessMimeFromName(fileName: String): String? =
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            fileName.substringAfterLast('.', "").lowercase()
        )
}
