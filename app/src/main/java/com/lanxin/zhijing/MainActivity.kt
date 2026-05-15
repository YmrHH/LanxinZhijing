package com.lanxin.zhijing

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
        setContent {
            LanxinZhijingTheme {
                AppNavGraph(learningViewModel = learningViewModel)
            }
        }
    }
}
