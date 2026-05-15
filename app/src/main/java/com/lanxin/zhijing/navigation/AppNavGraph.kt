package com.lanxin.zhijing.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lanxin.zhijing.data.ai.ImportSource
import com.lanxin.zhijing.data.local.LocalDbConstants
import com.lanxin.zhijing.ui.components.AppScaffold
import com.lanxin.zhijing.ui.components.BottomNavBar
import com.lanxin.zhijing.ui.screens.AnalysisScreen
import com.lanxin.zhijing.ui.screens.CameraCaptureScreen
import com.lanxin.zhijing.ui.screens.HomeScreen
import com.lanxin.zhijing.ui.screens.ImportPreviewScreen
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
fun AppNavGraph(
    learningViewModel: LearningViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute?.substringBefore("/") in tabRoutes ||
        currentRoute in tabRoutes
    val context = LocalContext.current
    val openImportPreview by learningViewModel.openImportPreview.collectAsStateWithLifecycle()

    LaunchedEffect(openImportPreview) {
        if (openImportPreview) {
            navController.navigate(Routes.IMPORT_PREVIEW) {
                launchSingleTop = true
            }
            learningViewModel.consumeOpenImportPreviewRequest()
        }
    }

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
                currentRoute = currentRoute?.substringBefore("/"),
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
                    viewModel = learningViewModel,
                    onOpenCamera = { navController.navigate(Routes.CAMERA_CAPTURE) },
                    onOpenKnowledgeTree = { navigateToTab(Routes.KNOWLEDGE_TREE) },
                    onOpenImportPreview = { navController.navigate(Routes.IMPORT_PREVIEW) }
                )
            }
            composable(Routes.CAMERA_CAPTURE) {
                CameraCaptureScreen(
                    onPhotoCaptured = { uri ->
                        learningViewModel.stageFromImageUri(
                            context = context,
                            uri = uri,
                            source = ImportSource.PHOTO_WRONG_QUESTION,
                            defaultTitle = "拍照错题"
                        ) { ok ->
                            if (ok) {
                                navController.popBackStack()
                                navController.navigate(Routes.IMPORT_PREVIEW)
                            } else {
                                context.showShortToast("无法处理照片")
                            }
                        }
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(Routes.IMPORT_PREVIEW) {
                ImportPreviewScreen(
                    viewModel = learningViewModel,
                    onConfirm = {
                        navController.navigate(Routes.ANALYSIS) {
                            popUpTo(Routes.HOME)
                        }
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(Routes.ANALYSIS) {
                AnalysisScreen(
                    viewModel = learningViewModel,
                    onViewKnowledgeTree = { navigateToTab(Routes.KNOWLEDGE_TREE) },
                    onGetStepHints = { navController.navigate(Routes.nodeFocus(LocalDbConstants.NODE_DERIVATIVE)) },
                    onAddToWrongBook = { context.showShortToast("已加入错题本") }
                )
            }
            composable(Routes.KNOWLEDGE_TREE) {
                KnowledgeTreeScreen(
                    viewModel = learningViewModel,
                    onOpenNodeFocus = { nodeId ->
                        navController.navigate(Routes.nodeFocus(nodeId))
                    }
                )
            }
            composable(
                route = Routes.NODE_FOCUS_PATTERN,
                arguments = listOf(
                    navArgument("nodeId") {
                        type = NavType.StringType
                        defaultValue = LocalDbConstants.NODE_DERIVATIVE
                    }
                )
            ) { entry ->
                val nodeId = entry.arguments?.getString("nodeId") ?: LocalDbConstants.NODE_DERIVATIVE
                LaunchedEffect(nodeId) {
                    learningViewModel.setFocusNodeId(nodeId)
                }
                NodeFocusScreen(
                    viewModel = learningViewModel,
                    onExplainToPeer = { navigateToTab(Routes.REVIEW) },
                    onRelatedWrong = { context.showShortToast("已展示相关错题") },
                    onAddReview = { context.showShortToast("已加入复习计划") }
                )
            }
            composable(Routes.REVIEW) {
                ReviewScreen(
                    viewModel = learningViewModel,
                    onNextPractice = { context.showShortToast("已生成 2 道同类练习题") }
                )
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    viewModel = learningViewModel
                )
            }
        }
    }
}
