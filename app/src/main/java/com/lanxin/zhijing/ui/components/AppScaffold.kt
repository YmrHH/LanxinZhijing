package com.lanxin.zhijing.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lanxin.zhijing.ui.theme.AppColors

@Composable
fun AppScaffold(
    showBottomBar: Boolean,
    bottomBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppColors.background,
        bottomBar = {
            if (showBottomBar) bottomBar()
        },
        content = content
    )
}
