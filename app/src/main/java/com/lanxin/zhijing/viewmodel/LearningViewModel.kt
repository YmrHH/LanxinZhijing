package com.lanxin.zhijing.viewmodel

import androidx.lifecycle.ViewModel
import com.lanxin.zhijing.data.ChatMessage
import com.lanxin.zhijing.data.MockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LearningViewModel : ViewModel() {

    private val _chatMessages = MutableStateFlow(MockData.initialChatMessages)
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _stepHintsExpanded = MutableStateFlow(false)
    val stepHintsExpanded: StateFlow<Boolean> = _stepHintsExpanded.asStateFlow()

    fun toggleStepHints() {
        _stepHintsExpanded.update { !it }
    }

    fun setStepHintsExpanded(expanded: Boolean) {
        _stepHintsExpanded.value = expanded
    }

    fun sendUserMessageAndMockReply(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        _chatMessages.update { current ->
            current +
                ChatMessage("USER", trimmed) +
                ChatMessage("AI", MockData.mockAiFollowUpReply)
        }
    }
}
