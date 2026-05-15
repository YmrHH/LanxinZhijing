package com.lanxin.zhijing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lanxin.zhijing.navigation.AppNavGraph
import com.lanxin.zhijing.ui.theme.LanxinZhijingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LanxinZhijingTheme {
                AppNavGraph()
            }
        }
    }
}
