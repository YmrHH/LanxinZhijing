package com.lanxin.zhijing.data.ai

import android.util.Base64
import com.lanxin.zhijing.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * 当 [BuildConfig.AI_BACKEND_BASE_URL] 已配置时，将 PDF 字节交给自有后端解析（PLAN §12.8 `parse-pdf`）。
 */
object BackendPdfParse {

    private const val PATH = "/api/lanxin/v1/parse-pdf"

    fun isConfigured(): Boolean = BuildConfig.AI_BACKEND_BASE_URL.trim().isNotEmpty()

    suspend fun parse(displayName: String, bytes: ByteArray): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            require(isConfigured()) { "未配置 ai.backend.baseUrl" }
            val base = BuildConfig.AI_BACKEND_BASE_URL.trim().trimEnd('/')
            val body = JSONObject().apply {
                put("fileName", displayName)
                put("contentBase64", Base64.encodeToString(bytes, Base64.NO_WRAP))
            }
            val url = URL(base + PATH)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                doOutput = true
                connectTimeout = 30_000
                readTimeout = 180_000
            }
            try {
                val payload = body.toString().toByteArray(StandardCharsets.UTF_8)
                conn.outputStream.use { it.write(payload) }
                val code = conn.responseCode
                val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                val text = stream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
                if (code !in 200..299) error("HTTP $code: $text")
                if (text.isBlank()) error("Empty response body")
                val root = JSONObject(text)
                when {
                    root.has("text") -> root.getString("text")
                    root.optJSONObject("data")?.has("text") == true ->
                        root.getJSONObject("data").getString("text")
                    else -> error("Missing text field in response")
                }.trim()
            } finally {
                conn.disconnect()
            }
        }
    }
}
