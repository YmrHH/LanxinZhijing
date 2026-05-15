package com.lanxin.zhijing.data.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * 调用自有后端（PLAN §12.8）；不包含 AppKEY；超时与错误以 [Result.failure] 返回。
 */
class BackendProxyAiLearningRepository(
    baseUrl: String
) : AiLearningRepository {

    private val base = baseUrl.trim().trimEnd('/')

    override suspend fun analyzeLearningContent(
        text: String,
        sourceType: ImportSource
    ): Result<LearningAnalysisResult> = withContext(Dispatchers.IO) {
        runCatching {
            val body = AiBackendWireFormat.analyzeRequestBody(text, sourceType)
            val json = postJson(path = AiBackendWireFormat.PATH_ANALYZE, body = body)
            AiBackendWireFormat.parseAnalyzeResponse(json)
        }
    }

    override suspend fun askNodeQuestion(
        nodeContext: NodeQuestionContext,
        question: String
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val body = AiBackendWireFormat.nodeAskRequestBody(nodeContext, question)
            val json = postJson(path = AiBackendWireFormat.PATH_NODE_ASK, body = body)
            AiBackendWireFormat.parseNodeAskResponse(json)
        }
    }

    override suspend fun generateStepHints(
        nodeContext: NodeQuestionContext
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        runCatching {
            val body = JSONObject().put("nodeContext", AiBackendWireFormat.nodeContextToJson(nodeContext))
            val json = postJson(path = AiBackendWireFormat.PATH_STEP_HINTS, body = body)
            AiBackendWireFormat.parseStepHintsResponse(json)
        }
    }

    override suspend fun evaluateFeynmanAnswer(
        request: FeynmanEvaluationRequest
    ): Result<FeynmanEvaluationResult> = withContext(Dispatchers.IO) {
        runCatching {
            val body = AiBackendWireFormat.feynmanRequestBody(request)
            val json = postJson(path = AiBackendWireFormat.PATH_FEYNMAN, body = body)
            AiBackendWireFormat.parseFeynmanResponse(json)
        }
    }

    private fun postJson(path: String, body: JSONObject): String {
        val url = URL(base + path)
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            doOutput = true
            connectTimeout = 30_000
            readTimeout = 120_000
        }
        try {
            val payload = body.toString()
            OutputStreamWriter(conn.outputStream, StandardCharsets.UTF_8).use { w ->
                w.write(payload)
            }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = stream?.bufferedReader(StandardCharsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (code !in 200..299) {
                error("HTTP $code: $text")
            }
            if (text.isBlank()) error("Empty response body")
            return text
        } finally {
            conn.disconnect()
        }
    }
}
