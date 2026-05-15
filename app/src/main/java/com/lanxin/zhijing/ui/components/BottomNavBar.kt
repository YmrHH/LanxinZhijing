package com.lanxin.zhijing.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lanxin.zhijing.navigation.Routes
import com.lanxin.zhijing.ui.theme.AppColors

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private val items = listOf(
    BottomItem(Routes.HOME, "首页", Icons.Default.Home),
    BottomItem(Routes.KNOWLEDGE_TREE, "知识树", Icons.Default.AccountTree),
    BottomItem(Routes.REVIEW, "复习", Icons.Default.School),
    BottomItem(Routes.PROFILE, "我的", Icons.Default.Person)
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = AppColors.cardSurface
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AppColors.primary,
                    selectedTextColor = AppColors.primary,
                    indicatorColor = AppColors.background,
                    unselectedIconColor = AppColors.textSecondary,
                    unselectedTextColor = AppColors.textSecondary
                )
            )
        }
    }
}
