package com.lanxin.zhijing.data.ai

import com.lanxin.zhijing.data.KnowledgeNode
import com.lanxin.zhijing.data.KnowledgeRelation
import org.json.JSONArray
import org.json.JSONObject

/**
 * 与 PLAN §12.8 一致的后端 JSON 组装与解析（严格字段名）。
 */
internal object AiBackendWireFormat {

    const val PATH_ANALYZE = "/api/lanxin/v1/analyze"
    const val PATH_NODE_ASK = "/api/lanxin/v1/node-ask"
    const val PATH_STEP_HINTS = "/api/lanxin/v1/step-hints"
    const val PATH_FEYNMAN = "/api/lanxin/v1/feynman-evaluate"

    fun analyzeRequestBody(text: String, importSource: ImportSource): JSONObject =
        JSONObject().apply {
            put("text", text)
            put("importSource", importSource.name)
        }

    fun parseAnalyzeResponse(json: String): LearningAnalysisResult {
        val o = JSONObject(json)
        val tags = o.requireStringArray("relatedKnowledgePoints")
        val path = o.requireStringArray("suggestedPath")
        val nodes = o.optJSONArray("nodes")?.toKnowledgeNodes().orEmpty()
        val rels = o.optJSONArray("relations")?.toKnowledgeRelations().orEmpty()
        return LearningAnalysisResult(
            contentType = o.getString("contentType"),
            coreTopic = o.getString("coreTopic"),
            relatedKnowledgePoints = tags,
            possibleWeakness = o.getString("possibleWeakness"),
            suggestedPath = path,
            nodes = nodes,
            relations = rels
        )
    }

    fun nodeContextToJson(ctx: NodeQuestionContext): JSONObject =
        JSONObject().apply {
            put("nodeId", ctx.nodeId)
            put("nodeTitle", ctx.nodeTitle)
            put("nodeDescription", ctx.nodeDescription)
            put("mastery", ctx.mastery)
            put("relatedNodes", JSONArray(ctx.relatedNodes.map { it.toWireJson() }))
            put("recentMistakes", JSONArray(ctx.recentMistakes))
            put("recentReviewFeedback", JSONArray(ctx.recentReviewFeedback))
        }

    fun nodeAskRequestBody(ctx: NodeQuestionContext, question: String): JSONObject =
        JSONObject().apply {
            put("nodeContext", nodeContextToJson(ctx))
            put("question", question)
        }

    fun parseNodeAskResponse(json: String): String =
        JSONObject(json).getString("answer")

    fun parseStepHintsResponse(json: String): List<String> =
        JSONObject(json).requireStringArray("hints")

    fun feynmanRequestBody(request: FeynmanEvaluationRequest): JSONObject =
        JSONObject().apply {
            put("nodeId", request.nodeId)
            put("nodeTitle", request.nodeTitle)
            put("question", request.question)
            put("userAnswer", request.userAnswer)
            put("masteryBefore", request.masteryBefore)
        }

    fun parseFeynmanResponse(json: String): FeynmanEvaluationResult {
        val o = JSONObject(json)
        return FeynmanEvaluationResult(
            score = o.getInt("score"),
            level = o.getString("level"),
            strengths = o.requireStringArray("strengths"),
            weaknesses = o.requireStringArray("weaknesses"),
            suggestions = o.requireStringArray("suggestions"),
            masteryBefore = o.getInt("masteryBefore"),
            masteryAfter = o.getInt("masteryAfter"),
            nextTasks = o.requireStringArray("nextTasks")
        )
    }

    private fun KnowledgeNode.toWireJson(): JSONObject =
        JSONObject().apply {
            put("id", id)
            put("label", label)
            put("progress", progress)
            put("type", type)
        }

    private fun JSONArray.toKnowledgeNodes(): List<KnowledgeNode> = buildList {
        for (i in 0 until length()) {
            val n = getJSONObject(i)
            add(
                KnowledgeNode(
                    id = n.getString("id"),
                    label = n.getString("label"),
                    progress = n.optInt("progress", 0),
                    type = n.optString("type", "concept")
                )
            )
        }
    }

    private fun JSONArray.toKnowledgeRelations(): List<KnowledgeRelation> = buildList {
        for (i in 0 until length()) {
            val r = getJSONObject(i)
            add(
                KnowledgeRelation(
                    from = r.getString("from"),
                    relation = r.getString("relation"),
                    to = r.getString("to")
                )
            )
        }
    }

    private fun JSONObject.requireStringArray(key: String): List<String> {
        val arr = getJSONArray(key)
        return buildList {
            for (i in 0 until arr.length()) {
                add(arr.getString(i))
            }
        }
    }
}
