package com.lanxin.zhijing.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.lanxin.zhijing.ui.theme.AppColors.background
import com.lanxin.zhijing.ui.theme.AppColors.border
import com.lanxin.zhijing.ui.theme.AppColors.primary
import com.lanxin.zhijing.ui.theme.AppColors.textPrimary
import com.lanxin.zhijing.ui.theme.AppColors.textSecondary

private val LightColorScheme = lightColorScheme(
    primary = primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8E4FF),
    onPrimaryContainer = textPrimary,
    secondary = AppColors.green,
    onSecondary = Color.White,
    tertiary = AppColors.yellow,
    background = background,
    onBackground = textPrimary,
    surface = AppColors.cardSurface,
    onSurface = textPrimary,
    surfaceVariant = Color(0xFFF0F5FA),
    onSurfaceVariant = textSecondary,
    outline = border
)

@Composable
fun LanxinZhijingTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content
    )
}
