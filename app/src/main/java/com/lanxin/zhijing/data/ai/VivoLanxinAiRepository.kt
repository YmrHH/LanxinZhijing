package com.lanxin.zhijing.data.ai

import com.lanxin.zhijing.BuildConfig
import java.util.UUID

/**
 * 直连 vivo AIGC「蓝心」HTTP 接口（[VivoAigcHttpClient]）。
 * AppID/AppKEY 由 [BuildConfig] 注入（见 `app/build.gradle.kts` / `local.properties`）。
 *
 * 文档入口：https://aigc.vivo.com.cn/#/document/index?id=1746
 */
class VivoLanxinAiRepository : AiLearningRepository {

    private val client by lazy {
        VivoAigcHttpClient(
            appId = BuildConfig.VIVO_AIGC_APP_ID.trim(),
            appKey = BuildConfig.VIVO_AIGC_APP_KEY.trim(),
            model = BuildConfig.VIVO_AIGC_MODEL.trim().ifEmpty { "vivo-BlueLM-TB-Pro" }
        )
    }

    override suspend fun analyzeLearningContent(
        text: String,
        sourceType: ImportSource
    ): Result<LearningAnalysisResult> {
        val sessionId = UUID.randomUUID().toString()
        val trimmed = text.trim().ifBlank { "（空文本）" }.take(12_000)
        val userBlock = "importSource=${sourceType.name}\n\n学习材料：\n$trimmed"
        return client.completionsEasyChat(
            sessionId = sessionId,
            prompt = userBlock,
            systemPrompt = ANALYZE_SYSTEM_PROMPT
        ).mapCatching { raw ->
            val json = extractJsonObject(raw)
            AiBackendWireFormat.parseAnalyzeResponse(json)
        }
    }

    override suspend fun askNodeQuestion(
        nodeContext: NodeQuestionContext,
        question: String
    ): Result<String> {
        val sessionId = UUID.randomUUID().toString()
        val user = buildString {
            append("节点标题：").append(nodeContext.nodeTitle).append('\n')
            append("掌握度：").append(nodeContext.mastery).append("%\n")
            append("节点说明：").append(nodeContext.nodeDescription).append('\n')
            append("用户问题：").append(question.trim())
        }
        return client.completionsEasyChat(
            sessionId = sessionId,
            prompt = user,
            systemPrompt = NODE_ASK_SYSTEM
        )
    }

    override suspend fun generateStepHints(
        nodeContext: NodeQuestionContext
    ): Result<List<String>> {
        val sessionId = UUID.randomUUID().toString()
        val user = "节点：${nodeContext.nodeTitle}，掌握度 ${nodeContext.mastery}%。请输出分步提示。"
        return client.completionsEasyChat(
            sessionId = sessionId,
            prompt = user,
            systemPrompt = STEP_HINTS_SYSTEM
        ).mapCatching { raw ->
            val json = extractJsonObject(raw)
            AiBackendWireFormat.parseStepHintsResponse(json)
        }
    }

    override suspend fun evaluateFeynmanAnswer(
        request: FeynmanEvaluationRequest
    ): Result<FeynmanEvaluationResult> {
        val sessionId = UUID.randomUUID().toString()
        val user = buildString {
            append("节点：").append(request.nodeTitle).append('\n')
            append("问题：").append(request.question).append('\n')
            append("学生回答：").append(request.userAnswer).append('\n')
            append("作答前掌握度：").append(request.masteryBefore)
        }
        return client.completionsEasyChat(
            sessionId = sessionId,
            prompt = user,
            systemPrompt = FEYNMAN_SYSTEM
        ).mapCatching { raw ->
            val json = extractJsonObject(raw)
            AiBackendWireFormat.parseFeynmanResponse(json)
        }
    }

    private companion object {
        const val ANALYZE_SYSTEM_PROMPT =
            "你是「蓝心知径」学习分析助手。只输出一个 JSON 对象，禁止 Markdown、禁止代码围栏、禁止多余说明。" +
                "字段与类型必须为：contentType(string), coreTopic(string), " +
                "relatedKnowledgePoints(array of string), possibleWeakness(string), suggestedPath(array of string), " +
                "nodes(array of object，每项含 id,label,progress,type 均为 string 或 number 按字段语义), " +
                "relations(array of object，每项含 from,relation,to 均为 string)。JSON 必须可被严格解析。"

        const val NODE_ASK_SYSTEM =
            "你是数学/理科学习助手。围绕用户给出的知识节点回答，使用简洁中文，不要输出 JSON，不要编造未给出的公式。"

        const val STEP_HINTS_SYSTEM =
            "只输出一个 JSON 对象，字段 hints 为 3 到 5 条中文短句的数组。禁止 Markdown 与代码围栏。"

        const val FEYNMAN_SYSTEM =
            "你是费曼学习法评估助手。只输出一个 JSON 对象，字段：" +
                "score(int 0-100), level(string), strengths(array of string), weaknesses(array of string), " +
                "suggestions(array of string), masteryBefore(int), masteryAfter(int), nextTasks(array of string)。" +
                "禁止 Markdown 与代码围栏。"
    }
}
