package com.lanxin.zhijing.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lanxin.zhijing.ui.components.AppCard
import com.lanxin.zhijing.ui.components.AppProgressBar
import com.lanxin.zhijing.ui.components.ChatBubble
import com.lanxin.zhijing.ui.components.PageHeader
import com.lanxin.zhijing.ui.components.TagChip
import com.lanxin.zhijing.ui.theme.AppColors
import com.lanxin.zhijing.viewmodel.LearningViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NodeFocusScreen(
    viewModel: LearningViewModel,
    onExplainToPeer: () -> Unit,
    onRelatedWrong: () -> Unit,
    onAddReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val hintsExpanded by viewModel.stepHintsExpanded.collectAsStateWithLifecycle()
    val focusNode by viewModel.focusNodeDisplay.collectAsStateWithLifecycle()
    var input by remember { mutableStateOf("") }

    val title = focusNode?.label ?: "导数与单调性"
    val mastery = focusNode?.progress ?: 42

    LazyColumn(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            PageHeader(
                title = "节点聚焦",
                subtitle = "$title · 掌握度 $mastery%"
            )
        }
        item {
            AppCard {
                Text(
                    text = "当前节点：$title",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = AppColors.textPrimary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "掌握度：$mastery%",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.textSecondary
                )
                Spacer(Modifier.height(10.dp))
                AppProgressBar(progress = mastery)
            }
        }
        item {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("导数符号", "单调区间", "极值判断").forEach { label ->
                    TagChip(label = label)
                }
            }
        }
        items(messages, key = { it.id }) { msg ->
            ChatBubble(message = msg)
        }
        item {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = { viewModel.toggleStepHints() }) {
                    Text("分步提示")
                }
                Button(
                    onClick = onExplainToPeer,
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary)
                ) {
                    Text("讲给同学听")
                }
                OutlinedButton(onClick = onRelatedWrong) {
                    Text("相关错题")
                }
                OutlinedButton(onClick = onAddReview) {
                    Text("加入复习")
                }
            }
        }
        if (hintsExpanded) {
            item {
                AppCard {
                    Text(
                        text = "分步提示",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = AppColors.textPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "提示 1：先判断导数符号",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.textPrimary
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "提示 2：再看该符号在区间内是否稳定",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.textPrimary
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "提示 3：如果 f'(x)>0，函数在该区间递增；如果 f'(x)<0，函数在该区间递减。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.textPrimary
                    )
                }
            }
        }
        item {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("继续追问这个知识点...") },
                singleLine = false,
                minLines = 2
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    viewModel.sendUserMessageAndMockReply(input)
                    input = ""
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary)
            ) {
                Text("发送")
            }
        }
    }
}
