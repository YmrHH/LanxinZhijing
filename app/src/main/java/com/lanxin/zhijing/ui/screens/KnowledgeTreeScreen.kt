package com.lanxin.zhijing.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lanxin.zhijing.ui.components.AppCard
import com.lanxin.zhijing.ui.components.KnowledgeGraph
import com.lanxin.zhijing.ui.components.PageHeader
import com.lanxin.zhijing.ui.theme.AppColors
import com.lanxin.zhijing.viewmodel.LearningViewModel

@Composable
fun KnowledgeTreeScreen(
    viewModel: LearningViewModel,
    onOpenNodeFocus: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val satellites by viewModel.treeSatelliteNodes.collectAsStateWithLifecycle()
    val center by viewModel.centerTreeNode.collectAsStateWithLifecycle()
    val edges by viewModel.knowledgeGraphEdges.collectAsStateWithLifecycle()
    val clickable = setOf("symbol", "monotonic")
    val centerTitle = center?.label ?: "导数与单调性"
    val centerProgress = center?.progress ?: 42

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PageHeader(
            title = "我的知识树",
            subtitle = "中心节点：$centerTitle $centerProgress%"
        )
        if (satellites.isEmpty() || edges.isEmpty()) {
            CircularProgressIndicator(color = AppColors.primary)
        } else {
            KnowledgeGraph(
                nodes = satellites,
                graphEdges = edges,
                centerTitle = centerTitle,
                centerProgress = centerProgress,
                onCenterClick = { onOpenNodeFocus("derivative") },
                onSatelliteClick = { node ->
                    if (node.id in clickable) onOpenNodeFocus(node.id)
                },
                clickableSatelliteIds = clickable,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
            )
        }
        AppCard {
            Text(
                text = "关系示例",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = AppColors.textPrimary
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "绿色圆点：函数基础 -> 前置 -> 导数定义",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.textPrimary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = "蓝色圆点：导数符号 -> 影响 -> 单调区间",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.textPrimary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = "红色圆点：单调区间 -> 前置 -> 极值判断",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.textPrimary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        Text(
            text = "系统建议：先复习“导数符号与单调区间”",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = AppColors.primary
        )
        Spacer(Modifier.height(8.dp))
    }
}
