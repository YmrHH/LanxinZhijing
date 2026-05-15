package com.lanxin.zhijing.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lanxin.zhijing.BuildConfig
import com.lanxin.zhijing.ui.components.AppCard
import com.lanxin.zhijing.ui.components.PageHeader
import com.lanxin.zhijing.ui.theme.AppColors
import com.lanxin.zhijing.viewmodel.LearningViewModel

@Composable
fun ProfileScreen(
    viewModel: LearningViewModel,
    modifier: Modifier = Modifier
) {
    val knowledgeCount by viewModel.profileKnowledgeCount.collectAsStateWithLifecycle()
    val pending by viewModel.profileReviewPendingCount.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PageHeader(
            title = "我的",
            subtitle = "学习画像与个人设置"
        )
        AppCard {
            Text(
                text = "学习天数：7 天",
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.textPrimary
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "已沉淀知识点：$knowledgeCount 个",
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.textPrimary
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "待复习节点：$pending 个",
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.textPrimary
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = aiBackendChannelSummary(),
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.textSecondary
            )
        }
        Text(
            text = "个人学习画像将在后续版本完善。",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = AppColors.textSecondary,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun aiBackendChannelSummary(): String {
    val backend = BuildConfig.AI_BACKEND_BASE_URL.trim()
    if (backend.isNotEmpty()) {
        val tail = if (backend.length > 40) "…" else ""
        val short = backend.take(40) + tail
        return if (BuildConfig.AI_BACKEND_FALLBACK_TO_MOCK) {
            "AI 通道：自有后端（失败回退 Mock）\n$short"
        } else {
            "AI 通道：自有后端（不回退 Mock）\n$short"
        }
    }
    val vivoId = BuildConfig.VIVO_AIGC_APP_ID.trim()
    val vivoKey = BuildConfig.VIVO_AIGC_APP_KEY.trim()
    if (vivoId.isNotEmpty() && vivoKey.isNotEmpty()) {
        val model = BuildConfig.VIVO_AIGC_MODEL.trim().ifEmpty { "vivo-BlueLM-TB-Pro" }
        val fb = if (BuildConfig.AI_BACKEND_FALLBACK_TO_MOCK) "失败回退 Mock" else "不回退 Mock"
        return "AI 通道：蓝心直连（$fb，模型 $model，AppID ${vivoId.take(10)}…）"
    }
    return "AI 通道：本地 Mock（可配置 ai.backend.baseUrl 或 ai.vivo.appId/appKey）"
}

