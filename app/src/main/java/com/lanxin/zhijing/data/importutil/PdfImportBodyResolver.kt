package com.lanxin.zhijing.data.importutil

import com.lanxin.zhijing.data.ai.BackendPdfParse

/**
 * PDF 导入预览正文：已配置后端则请求解析；否则占位；失败时占位 + 简要原因。
 */
object PdfImportBodyResolver {

    suspend fun resolve(displayName: String, bytes: ByteArray): String {
        if (!BackendPdfParse.isConfigured()) {
            return DocumentImportHelper.pdfPlaceholderBody(displayName)
        }
        val r = BackendPdfParse.parse(displayName, bytes)
        val text = r.getOrNull()?.trim().orEmpty()
        return when {
            r.isSuccess && text.isNotEmpty() -> text.take(200_000)
            r.isFailure ->
                DocumentImportHelper.pdfPlaceholderBody(displayName) +
                    "\n\n【PDF 后端解析失败】" +
                    (r.exceptionOrNull()?.message?.take(500) ?: "")
            else ->
                DocumentImportHelper.pdfPlaceholderBody(displayName) +
                    "\n\n【PDF 后端返回空正文】"
        }
    }
}
