package com.lanxin.zhijing.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lanxin.zhijing.ui.components.AppScaffold
import com.lanxin.zhijing.ui.components.BottomNavBar
import com.lanxin.zhijing.ui.screens.AnalysisScreen
import com.lanxin.zhijing.ui.screens.HomeScreen
import com.lanxin.zhijing.ui.screens.KnowledgeTreeScreen
import com.lanxin.zhijing.ui.screens.NodeFocusScreen
import com.lanxin.zhijing.ui.screens.ProfileScreen
import com.lanxin.zhijing.ui.screens.ReviewScreen
import com.lanxin.zhijing.ui.util.showShortToast
import com.lanxin.zhijing.viewmodel.LearningViewModel

private val tabRoutes = setOf(
    Routes.HOME,
    Routes.KNOWLEDGE_TREE,
    Routes.REVIEW,
    Routes.PROFILE
)

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in tabRoutes
    val context = LocalContext.current

    fun navigateToTab(route: String) {
        navController.navigate(route) {
            popUpTo(Routes.HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    AppScaffold(
        showBottomBar = showBottomBar,
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route -> navigateToTab(route) }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onOpenAnalysis = { navController.navigate(Routes.ANALYSIS) },
                    onOpenKnowledgeTree = { navigateToTab(Routes.KNOWLEDGE_TREE) }
                )
            }
            composable(Routes.ANALYSIS) {
                AnalysisScreen(
                    onViewKnowledgeTree = { navigateToTab(Routes.KNOWLEDGE_TREE) },
                    onGetStepHints = { navController.navigate(Routes.NODE_FOCUS) },
                    onAddToWrongBook = { context.showShortToast("已加入错题本") }
                )
            }
            composable(Routes.KNOWLEDGE_TREE) {
                KnowledgeTreeScreen(
                    onOpenNodeFocus = { navController.navigate(Routes.NODE_FOCUS) }
                )
            }
            composable(Routes.NODE_FOCUS) {
                val vm: LearningViewModel = viewModel()
                NodeFocusScreen(
                    viewModel = vm,
                    onExplainToPeer = { navigateToTab(Routes.REVIEW) },
                    onRelatedWrong = { context.showShortToast("已展示相关错题") },
                    onAddReview = { context.showShortToast("已加入复习计划") }
                )
            }
            composable(Routes.REVIEW) {
                ReviewScreen(
                    onNextPractice = { context.showShortToast("已生成 2 道同类练习题") }
                )
            }
            composable(Routes.PROFILE) {
                ProfileScreen()
            }
        }
    }
}
