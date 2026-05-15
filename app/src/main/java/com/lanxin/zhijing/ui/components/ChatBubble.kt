package com.lanxin.zhijing.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lanxin.zhijing.data.ChatMessage
import com.lanxin.zhijing.ui.theme.AppColors

@Composable
fun ChatBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val isAi = message.role == "AI"
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isAi) 4.dp else 16.dp,
                bottomEnd = if (isAi) 16.dp else 4.dp
            ),
            color = if (isAi) {
                AppColors.cardSurface
            } else {
                AppColors.primary.copy(alpha = 0.12f)
            },
            tonalElevation = 1.dp,
            shadowElevation = 1.dp,
            border = BorderStroke(1.dp, AppColors.border)
        ) {
            Column(Modifier.padding(12.dp)) {
                Text(
                    text = if (isAi) "智能助手" else "我",
                    style = MaterialTheme.typography.labelLarge,
                    color = AppColors.textSecondary
                )
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.textPrimary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
