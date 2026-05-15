package com.lanxin.zhijing.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lanxin.zhijing.LanxinZhijingApplication
import com.lanxin.zhijing.data.ChatMessage
import com.lanxin.zhijing.data.KnowledgeNode
import com.lanxin.zhijing.data.LearningItem
import com.lanxin.zhijing.data.ai.AiLearningRepository
import com.lanxin.zhijing.data.ai.FeynmanEvaluationRequest
import com.lanxin.zhijing.data.ai.ImportSource
import com.lanxin.zhijing.data.ai.LearningAnalysisResult
import com.lanxin.zhijing.data.ai.MockAiLearningRepository
import com.lanxin.zhijing.data.ai.builtInDefaultLearningAnalysis
import com.lanxin.zhijing.data.ai.demoNodeQuestionContext
import com.lanxin.zhijing.data.importutil.TextImportHelper
import com.lanxin.zhijing.data.local.LocalDbConstants
import com.lanxin.zhijing.data.repository.LearningRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class LearningViewModel(
    private val learningRepository: LearningRepository,
    private val aiRepository: AiLearningRepository = MockAiLearningRepository()
) : ViewModel() {

    private val focusNodeId = MutableStateFlow(LocalDbConstants.NODE_DERIVATIVE)

    val learningItems: StateFlow<List<LearningItem>> = learningRepository.learningItems
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val treeSatelliteNodes: StateFlow<List<KnowledgeNode>> = learningRepository.knowledgeNodesForTree
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val centerTreeNode: StateFlow<KnowledgeNode?> = learningRepository.centerTreeNode
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val knowledgeGraphEdges: StateFlow<List<Pair<String, String>>> = learningRepository.knowledgeGraphEdges
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val chatMessages: StateFlow<List<ChatMessage>> = focusNodeId
        .flatMapLatest { learningRepository.getChatMessages(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val focusNodeDisplay: StateFlow<KnowledgeNode?> = focusNodeId
        .flatMapLatest { learningRepository.getKnowledgeNode(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _stepHintsExpanded = MutableStateFlow(false)
    val stepHintsExpanded: StateFlow<Boolean> = _stepHintsExpanded.asStateFlow()

    val profileKnowledgeCount: StateFlow<Int> = learningRepository.profileKnowledgeCount
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val profileReviewPendingCount: StateFlow<Int> = learningRepository.profileReviewPendingCount
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val latestFeynmanReview = learningRepository.latestFeynmanForDerivative
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _analysisDisplay = MutableStateFlow(builtInDefaultLearningAnalysis())
    val analysisDisplay: StateFlow<LearningAnalysisResult> = _analysisDisplay.asStateFlow()

    fun refreshAnalysisForDisplay() {
        viewModelScope.launch {
            val latest = learningRepository.getLatestImportOnce()
            val text = latest?.rawText.orEmpty()
            val src = latest?.sourceType?.let { runCatching { ImportSource.valueOf(it) }.getOrNull() }
                ?: ImportSource.PASTE_TEXT
            _analysisDisplay.value = aiRepository.analyzeLearningContent(text, src).getOrElse {
                builtInDefaultLearningAnalysis()
            }
        }
    }

    fun importPastedText(title: String, body: String, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val trimmed = body.trim()
            if (trimmed.isEmpty()) {
                onDone(false)
                return@launch
            }
            learningRepository.importAndPersist(title, trimmed, ImportSource.PASTE_TEXT)
            _analysisDisplay.value = aiRepository.analyzeLearningContent(trimmed, ImportSource.PASTE_TEXT)
                .getOrElse { builtInDefaultLearningAnalysis() }
            onDone(true)
        }
    }

    fun importFromFile(context: Context, uri: Uri, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val text = TextImportHelper.readUri(context, uri)
            if (text.isNullOrBlank()) {
                onDone(false)
                return@launch
            }
            val name = TextImportHelper.queryDisplayName(context, uri) ?: "文件导入"
            learningRepository.importAndPersist(
                title = name,
                rawText = text,
                importSource = ImportSource.TEXTBOOK_OR_NOTES,
                fileUri = uri.toString()
            )
            _analysisDisplay.value = aiRepository.analyzeLearningContent(text, ImportSource.TEXTBOOK_OR_NOTES)
                .getOrElse { builtInDefaultLearningAnalysis() }
            onDone(true)
        }
    }

    fun setFocusNodeId(nodeId: String) {
        focusNodeId.value = nodeId
    }

    fun toggleStepHints() {
        _stepHintsExpanded.value = !_stepHintsExpanded.value
    }

    fun setStepHintsExpanded(expanded: Boolean) {
        _stepHintsExpanded.value = expanded
    }

    fun sendUserMessageAndMockReply(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        val nodeId = focusNodeId.value
        viewModelScope.launch {
            learningRepository.addChatMessage(nodeId, "USER", trimmed)
            val ctx = demoNodeQuestionContext()
            val reply = aiRepository.askNodeQuestion(ctx, trimmed).getOrElse {
                com.lanxin.zhijing.data.MockData.mockAiFollowUpReply
            }
            learningRepository.addChatMessage(nodeId, "AI", reply)
        }
    }

    fun ensureMockFeynmanPersisted() {
        viewModelScope.launch {
            val existing = learningRepository.latestFeynmanForDerivative.first()
            if (existing != null) return@launch
            val question = "请你不用公式，讲给同学听：为什么导数可以判断函数的增减？"
            val userAnswer =
                "因为导数表示函数变化的方向。导数大于 0 时，函数值会增加；导数小于 0 时，函数值会减少。"
            val request = FeynmanEvaluationRequest(
                nodeId = LocalDbConstants.NODE_DERIVATIVE,
                nodeTitle = "导数与单调性",
                question = question,
                userAnswer = userAnswer,
                masteryBefore = 42
            )
            val result = aiRepository.evaluateFeynmanAnswer(request).getOrNull() ?: return@launch
            learningRepository.saveFeynmanReview(
                nodeId = LocalDbConstants.NODE_DERIVATIVE,
                question = question,
                userAnswer = userAnswer,
                result = result
            )
        }
    }
}

class LearningViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == LearningViewModel::class.java)
        val app = application as LanxinZhijingApplication
        return LearningViewModel(app.learningRepository) as T
    }
}
