package com.lanxin.zhijing.data.ai

import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * vivo AIGC 网关 HTTP 客户端（参考开源实现：POST `https://api-ai.vivo.com.cn/vivogpt/completions` + 网关头签名）。
 */
internal class VivoAigcHttpClient(
    private val appId: String,
    private val appKey: String,
    private val model: String
) {

    private val secureRandom = SecureRandom()

    suspend fun completionsEasyChat(
        sessionId: String,
        prompt: String,
        systemPrompt: String = ""
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val body = JSONObject().apply {
                put("prompt", prompt)
                if (systemPrompt.isNotEmpty()) put("systemPrompt", systemPrompt)
                put("model", model)
                put("sessionId", sessionId)
            }
            postCompletions(body)
        }
    }

    private fun postCompletions(body: JSONObject): String {
        val requestId = UUID.randomUUID().toString()
        val path = "/vivogpt/completions"
        val queryString = "requestId=$requestId"
        val timestampSec = System.currentTimeMillis() / 1000
        val timestampStr = timestampSec.toString()
        val nonce = randomNonce(8)
        val signingString = buildString {
            append("POST\n")
            append(path).append('\n')
            append(queryString).append('\n')
            append(appId).append('\n')
            append(timestampStr).append('\n')
            append("x-ai-gateway-app-id:").append(appId).append('\n')
            append("x-ai-gateway-timestamp:").append(timestampStr).append('\n')
            append("x-ai-gateway-nonce:").append(nonce)
        }
        val signature = hmacSha256Base64(signingString, appKey)
        val url = URL("https://api-ai.vivo.com.cn$path?$queryString")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            setRequestProperty("X-AI-GATEWAY-APP-ID", appId)
            setRequestProperty("X-AI-GATEWAY-TIMESTAMP", timestampStr)
            setRequestProperty("X-AI-GATEWAY-NONCE", nonce)
            setRequestProperty(
                "X-AI-GATEWAY-SIGNED-HEADERS",
                "x-ai-gateway-app-id;x-ai-gateway-timestamp;x-ai-gateway-nonce"
            )
            setRequestProperty("X-AI-GATEWAY-SIGNATURE", signature)
            doOutput = true
            connectTimeout = 30_000
            readTimeout = 120_000
        }
        try {
            val bytes = body.toString().toByteArray(StandardCharsets.UTF_8)
            conn.outputStream.use { it.write(bytes) }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = stream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
            if (code !in 200..299) {
                error("HTTP $code: $text")
            }
            val root = JSONObject(text)
            val apiCode = root.optInt("code", -1)
            if (apiCode != 0) {
                error(root.optString("msg", root.optString("message", "code=$apiCode")))
            }
            return root.getJSONObject("data").getString("content")
        } finally {
            conn.disconnect()
        }
    }

    private fun randomNonce(length: Int): String {
        val letters = "abcdefghijklmnopqrstuvwxyz0123456789"
        return buildString(length) {
            repeat(length) {
                append(letters[secureRandom.nextInt(letters.length)])
            }
        }
    }

    private fun hmacSha256Base64(signingString: String, key: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), "HmacSHA256"))
        val raw = mac.doFinal(signingString.toByteArray(StandardCharsets.UTF_8))
        return Base64.encodeToString(raw, Base64.NO_WRAP)
    }
}
