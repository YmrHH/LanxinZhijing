package com.lanxin.zhijing.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lanxin.zhijing.ui.components.AppCard
import com.lanxin.zhijing.ui.components.AppProgressBar
import com.lanxin.zhijing.ui.components.LearningItemCard
import com.lanxin.zhijing.ui.components.PageHeader
import com.lanxin.zhijing.ui.theme.AppColors
import com.lanxin.zhijing.ui.util.showShortToast
import com.lanxin.zhijing.viewmodel.LearningViewModel

@Composable
fun HomeScreen(
    viewModel: LearningViewModel,
    onOpenAnalysis: () -> Unit,
    onOpenKnowledgeTree: () -> Unit,
    modifier: Modifier = Modifier
) {
    val learningItems by viewModel.learningItems.collectAsStateWithLifecycle()
    val centerNode by viewModel.centerTreeNode.collectAsStateWithLifecycle()
    val suggestionProgress = centerNode?.progress ?: 42
    val context = LocalContext.current

    var showPasteDialog by remember { mutableStateOf(false) }
    var pasteTitle by remember { mutableStateOf("") }
    var pasteBody by remember { mutableStateOf("") }

    val pickFile = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        viewModel.importFromFile(context, uri) { ok ->
            if (ok) onOpenAnalysis()
            else context.showShortToast("无法读取文件（过大或非 UTF-8 文本）")
        }
    }

    if (showPasteDialog) {
        AlertDialog(
            onDismissRequest = { showPasteDialog = false },
            title = { Text("粘贴文档内容") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pasteTitle,
                        onValueChange = { pasteTitle = it },
                        label = { Text("标题（可选）") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = pasteBody,
                        onValueChange = { pasteBody = it },
                        label = { Text("正文") },
                        minLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.importPastedText(pasteTitle, pasteBody) { ok ->
                            if (ok) {
                                showPasteDialog = false
                                pasteBody = ""
                                onOpenAnalysis()
                            } else {
                                context.showShortToast("请输入要导入的内容")
                            }
                        }
                    }
                ) {
                    Text("导入并分析")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

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
                        text = "掌握度：$suggestionProgress%",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.primary
                    )
                }
                Spacer(Modifier.height(8.dp))
                AppProgressBar(progress = suggestionProgress)
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
                        onClick = { pickFile.launch("*/*") }
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
                        onClick = { showPasteDialog = true }
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
        if (learningItems.isEmpty()) {
            item {
                CircularProgressIndicator(color = AppColors.primary)
            }
        } else {
            items(learningItems, key = { it.id }) { item ->
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
