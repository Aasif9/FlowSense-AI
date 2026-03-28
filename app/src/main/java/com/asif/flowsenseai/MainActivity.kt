package com.asif.flowsenseai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.asif.flowsenseai.navigation.AppNavGraph
import com.asif.flowsenseai.ui.theme.FlowSenseAITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlowSenseAITheme {
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }
    }
}