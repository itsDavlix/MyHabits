package com.example.myhabits

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myhabits.ui.login.LoginScreen
import com.example.myhabits.ui.login.RegistrationScreen
import com.example.myhabits.ui.dashboard.MainHubScreen
import com.example.myhabits.ui.navigation.Screen
import com.example.myhabits.ui.theme.MyHabitsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyHabitsTheme {
                MyHabitsApp()
            }
        }
    }
}

@Composable
fun MyHabitsApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.MainHub.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegistration = {
                    navController.navigate(Screen.Registration.route)
                }
            )
        }
        composable(Screen.Registration.route) {
            RegistrationScreen(
                onRegistrationSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Registration.route) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.MainHub.route) {
            MainHubScreen()
        }
    }
}
