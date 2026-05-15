package com.lanxin.zhijing.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lanxin.zhijing.ui.components.AppCard
import com.lanxin.zhijing.ui.components.PageHeader
import com.lanxin.zhijing.ui.theme.AppColors
import com.lanxin.zhijing.viewmodel.LearningViewModel

@Composable
fun ImportPreviewScreen(
    viewModel: LearningViewModel,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pending by viewModel.pendingImport.collectAsStateWithLifecycle()

    if (pending == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("没有待导入的内容", color = AppColors.textSecondary)
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
                Text("返回")
            }
        }
        return
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PageHeader(
            title = "导入预览",
            subtitle = "确认或修正内容后再进行分析"
        )
        AppCard {
            Text(
                text = "来源：${pending!!.source.name}",
                style = MaterialTheme.typography.labelLarge,
                color = AppColors.textSecondary
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = pending!!.title,
                onValueChange = viewModel::updatePendingTitle,
                label = { Text("标题") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = pending!!.body,
                onValueChange = viewModel::updatePendingBody,
                label = { Text("学习内容") },
                minLines = 8,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = {
                    viewModel.confirmPendingImport { ok ->
                        if (ok) onConfirm()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary)
            ) {
                Text("确认并分析")
            }
            OutlinedButton(
                onClick = {
                    viewModel.clearPendingImport()
                    onCancel()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("取消")
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
