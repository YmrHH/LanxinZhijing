package com.lanxin.zhijing.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lanxin.zhijing.ui.components.AppCard
import com.lanxin.zhijing.ui.components.AppProgressBar
import com.lanxin.zhijing.ui.components.PageHeader
import com.lanxin.zhijing.ui.theme.AppColors
import com.lanxin.zhijing.viewmodel.LearningViewModel

@Composable
fun ReviewScreen(
    viewModel: LearningViewModel,
    onNextPractice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val latest by viewModel.latestFeynmanReview.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.ensureMockFeynmanPersisted()
    }

    val question = "请你不用公式，讲给同学听：为什么导数可以判断函数的增减？"
    val userAnswer =
        "因为导数表示函数变化的方向。导数大于 0 时，函数值会增加；导数小于 0 时，函数值会减少。"
    val score = latest?.score ?: 78
    val level = latest?.level ?: "基本理解"
    val strengthsText = latest?.strengths ?: "说出了导数和函数变化趋势有关。"
    val weaknessLines = latest?.weaknesses?.lines()?.filter { it.isNotBlank() } ?: listOf(
        "可以补充「切线斜率」的解释",
        "判断单调性时，要看区间内导数符号是否稳定",
        "还没有说明导数符号变化与极值点的关系"
    )
    val masteryAfter = latest?.masteryAfter ?: 68
    val masteryBefore = latest?.masteryBefore ?: 42

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PageHeader(
            title = "讲给同学听",
            subtitle = "费曼复述 · 掌握度更新"
        )
        AppCard {
            Text(
                text = "AI 提问",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = AppColors.textPrimary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = latest?.question ?: question,
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.textPrimary
            )
        }
        AppCard {
            Text(
                text = "用户回答",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = AppColors.textPrimary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = latest?.userAnswer ?: userAnswer,
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.textPrimary
            )
        }
        AppCard {
            Text(
                text = "AI 反馈",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = AppColors.textPrimary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "得分：$score / 100",
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.primary
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "等级标签：$level",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.textSecondary
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "做得好的地方：",
                style = MaterialTheme.typography.labelLarge,
                color = AppColors.textSecondary
            )
            Text(
                text = strengthsText,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.textPrimary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "还需要补充：",
                style = MaterialTheme.typography.labelLarge,
                color = AppColors.textSecondary
            )
            weaknessLines.forEachIndexed { index, line ->
                Text(
                    text = "${index + 1}. $line",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.textPrimary
                )
            }
        }
        AppCard {
            Text(
                text = "掌握度变化",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = AppColors.textPrimary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "导数与单调性",
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.textPrimary
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "变化：$masteryBefore% -> $masteryAfter%",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.textSecondary
            )
            Spacer(Modifier.height(10.dp))
            AppProgressBar(progress = masteryAfter, progressColor = AppColors.green)
        }
        Button(
            onClick = onNextPractice,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary)
        ) {
            Text("下一步：完成 2 道同类题")
        }
        Spacer(Modifier.height(24.dp))
    }
}
