package com.asif.flowsenseai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.asif.flowsenseai.ui.screen.MainScreen
import com.asif.flowsenseai.ui.screen.NotificationPermissionScreen
import com.asif.flowsenseai.ui.theme.FlowSenseAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlowSenseAITheme {
                // Set up navigation controller
                val navController = rememberNavController()
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "main_screen",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Main screen (expense list)
                        composable("main_screen") {
                            MainScreen(
                                onNavigateToPermission = {
                                    navController.navigate("permission_screen")
                                }
                            )
                        }
                        
                        // Notification permission screen
                        composable("permission_screen") {
                            NotificationPermissionScreen(
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    FlowSenseAITheme {
        MainScreen()
    }
}