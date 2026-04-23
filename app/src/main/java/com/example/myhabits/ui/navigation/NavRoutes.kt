package com.example.myhabits.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Registration : Screen("registration")
    object MainHub : Screen("main_hub")
}

sealed class BottomBarScreen(val route: String, val title: String, val icon: String) {
    object Home : BottomBarScreen("home", "Inicio", "🏠")
    object Stats : BottomBarScreen("stats", "Estadísticas", "📊")
    object Profile : BottomBarScreen("profile", "Perfil", "👤")
}
