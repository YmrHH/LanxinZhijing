package com.lanxin.zhijing

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.lanxin.zhijing.navigation.AppNavGraph
import com.lanxin.zhijing.ui.theme.LanxinZhijingTheme
import com.lanxin.zhijing.viewmodel.LearningViewModel
import com.lanxin.zhijing.viewmodel.LearningViewModelFactory

class MainActivity : ComponentActivity() {

    private val learningViewModel: LearningViewModel by viewModels {
        LearningViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleImportIntent(intent)
        setContent {
            LanxinZhijingTheme {
                AppNavGraph(learningViewModel = learningViewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleImportIntent(intent)
    }

    private fun handleImportIntent(intent: Intent?) {
        val action = intent?.action ?: return
        if (action != Intent.ACTION_SEND && action != Intent.ACTION_PROCESS_TEXT) return
        learningViewModel.handleIncomingIntent(this, intent)
    }
}
