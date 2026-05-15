package com.lanxin.zhijing.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lanxin.zhijing.ui.components.AppCard
import com.lanxin.zhijing.ui.components.PageHeader
import com.lanxin.zhijing.ui.components.TagChip
import com.lanxin.zhijing.ui.theme.AppColors
import com.lanxin.zhijing.viewmodel.LearningViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnalysisScreen(
    viewModel: LearningViewModel,
    onViewKnowledgeTree: () -> Unit,
    onGetStepHints: () -> Unit,
    onAddToWrongBook: () -> Unit,
    modifier: Modifier = Modifier
) {
    val analysis by viewModel.analysisDisplay.collectAsStateWithLifecycle()
    val analysisLoading by viewModel.analysisLoading.collectAsStateWithLifecycle()
    val analysisError by viewModel.analysisError.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refreshAnalysisForDisplay()
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PageHeader(
            title = "AI 学习分析",
            subtitle = "已识别你的学习内容"
        )
        if (analysisLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = AppColors.primary)
            Text(
                text = "正在分析学习内容，请稍候…",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.textSecondary
            )
        }
        analysisError?.let { err ->
            AppCard {
                Text(
                    text = "分析未完全成功",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = AppColors.textPrimary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = err,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.textSecondary
                )
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { viewModel.retryAnalysisForDisplay() }) {
                    Text("重新分析")
                }
            }
        }
        if (!analysisLoading) {
            AppCard {
                Text(
                    text = "识别结果",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = AppColors.textPrimary
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "内容类型：${analysis.contentType}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.textPrimary
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "核心考点：${analysis.coreTopic}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.textPrimary
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "关联知识点",
                    style = MaterialTheme.typography.labelLarge,
                    color = AppColors.textSecondary
                )
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    analysis.relatedKnowledgePoints.forEach { tag ->
                        TagChip(label = tag)
                    }
                }
            }
            AppCard {
                Text(
                    text = "可能卡点",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = AppColors.textPrimary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = analysis.possibleWeakness,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.textPrimary
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "建议学习路径",
                    style = MaterialTheme.typography.labelLarge,
                    color = AppColors.textSecondary
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = analysis.suggestedPath.joinToString(" -> "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.textPrimary
                )
            }
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.tipGreenBackground
                ),
                border = BorderStroke(1.dp, AppColors.border),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                    Text(
                        text = "比普通 AI 多做一步",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = AppColors.green
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "不是直接给答案，而是先判断：考什么、卡在哪、先补哪条知识链。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.textPrimary
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onViewKnowledgeTree,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary)
                ) {
                    Text("查看知识树")
                }
                Button(
                    onClick = onGetStepHints,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary)
                ) {
                    Text("获取分步提示")
                }
                OutlinedButton(
                    onClick = onAddToWrongBook,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("加入错题本")
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
