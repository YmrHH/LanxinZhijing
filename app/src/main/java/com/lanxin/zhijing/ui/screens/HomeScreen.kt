package com.lanxin.zhijing.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lanxin.zhijing.data.MockData
import com.lanxin.zhijing.ui.components.AppCard
import com.lanxin.zhijing.ui.components.AppProgressBar
import com.lanxin.zhijing.ui.components.LearningItemCard
import com.lanxin.zhijing.ui.components.PageHeader
import com.lanxin.zhijing.ui.theme.AppColors

@Composable
fun HomeScreen(
    onOpenAnalysis: () -> Unit,
    onOpenKnowledgeTree: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            PageHeader(
                title = "蓝心知径",
                subtitle = "把题目、教材和文档变成你的个人知识树"
            )
        }
        item {
            AppCard {
                Text(
                    text = "今日学习建议",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = AppColors.textPrimary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "先补「导数符号与单调区间」",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.textPrimary
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "根据最近错题与复述表现生成",
                    style = MaterialTheme.typography.labelLarge,
                    color = AppColors.textSecondary
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "掌握度：42%",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.primary
                    )
                }
                Spacer(Modifier.height(8.dp))
                AppProgressBar(progress = 42)
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeEntryCard(
                        title = "拍一道错题",
                        subtitle = "错因诊断",
                        modifier = Modifier.weight(1f),
                        onClick = onOpenAnalysis
                    )
                    HomeEntryCard(
                        title = "导入教材/笔记",
                        subtitle = "生成知识树",
                        modifier = Modifier.weight(1f),
                        onClick = null
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeEntryCard(
                        title = "粘贴文档内容",
                        subtitle = "拆解概念",
                        modifier = Modifier.weight(1f),
                        onClick = null
                    )
                    HomeEntryCard(
                        title = "截图识别",
                        subtitle = "系统级入口",
                        modifier = Modifier.weight(1f),
                        onClick = onOpenAnalysis
                    )
                }
            }
        }
        item {
            Text(
                text = "最近学习",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = AppColors.textPrimary
            )
        }
        items(MockData.learningItems, key = { it.id }) { item ->
            val isDerivative = item.id == "derivative"
            LearningItemCard(
                item = item,
                onClick = if (isDerivative) {
                    { onOpenKnowledgeTree() }
                } else {
                    null
                }
            )
        }
    }
}

@Composable
private fun HomeEntryCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)?
) {
    val mod = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }
    AppCard(modifier = mod) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = AppColors.textPrimary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelLarge,
            color = AppColors.textSecondary
        )
    }
}
