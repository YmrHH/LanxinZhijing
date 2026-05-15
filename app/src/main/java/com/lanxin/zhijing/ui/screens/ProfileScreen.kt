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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lanxin.zhijing.ui.components.AppCard
import com.lanxin.zhijing.ui.components.PageHeader
import com.lanxin.zhijing.ui.theme.AppColors

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
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
                text = "已沉淀知识点：26 个",
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.textPrimary
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "待复习节点：5 个",
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.textPrimary
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
