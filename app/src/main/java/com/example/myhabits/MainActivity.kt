package com.example.myhabits

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.myhabits.ui.dashboard.DashboardScreen
import com.example.myhabits.ui.login.LoginScreen
import com.example.myhabits.ui.login.RegistrationScreen
import com.example.myhabits.ui.theme.MyHabitsTheme

enum class Screen {
    LOGIN, REGISTRATION, DASHBOARD
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyHabitsTheme {
                var currentScreen by remember { mutableStateOf(Screen.LOGIN) }

                Crossfade(targetState = currentScreen, label = "main_nav") { screen ->
                    when (screen) {
                        Screen.LOGIN -> {
                            LoginScreen(
                                onLoginSuccess = { currentScreen = Screen.DASHBOARD },
                                onNavigateToRegistration = { currentScreen = Screen.REGISTRATION }
                            )
                        }
                        Screen.REGISTRATION -> {
                            RegistrationScreen(
                                onRegistrationSuccess = { currentScreen = Screen.DASHBOARD },
                                onBackToLogin = { currentScreen = Screen.LOGIN }
                            )
                        }
                        Screen.DASHBOARD -> {
                            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                                Box(modifier = Modifier.padding(innerPadding)) {
                                    DashboardScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
