package com.lanxin.zhijing.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lanxin.zhijing.ui.theme.AppColors

@Composable
fun AppProgressBar(
    progress: Int,
    modifier: Modifier = Modifier,
    progressColor: Color = AppColors.primary,
    trackColor: Color = AppColors.border.copy(alpha = 0.45f)
) {
    val fraction = (progress.coerceIn(0, 100)) / 100f
    LinearProgressIndicator(
        progress = { fraction },
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(8.dp)),
        color = progressColor,
        trackColor = trackColor
    )
}
