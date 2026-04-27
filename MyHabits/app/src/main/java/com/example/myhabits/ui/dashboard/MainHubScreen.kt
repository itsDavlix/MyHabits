package com.example.myhabits.ui.dashboard

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myhabits.data.HabitRepository
import androidx.lifecycle.ViewModelProvider
import com.example.myhabits.ui.navigation.BottomBarScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myhabits.ui.theme.*

@Composable
fun MainHubScreen() {
    val navController = rememberNavController()
    val repository = remember { HabitRepository() }
    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
                val application = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!
                return DashboardViewModel(application, repository) as T
            }
        }
    )
    val statsViewModel: StatsViewModel = remember(dashboardViewModel) {
        StatsViewModel(dashboardViewModel)
    }
    
    Scaffold(
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = BottomBarScreen.Home.route
            ) {
                composable(BottomBarScreen.Home.route) {
                    DashboardScreen(dashboardViewModel, statsViewModel)
                }
                composable(BottomBarScreen.Stats.route) {
                    StatsScreen(statsViewModel)
                }
                composable(BottomBarScreen.Profile.route) {
                    ProfileScreen(dashboardViewModel, statsViewModel)
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(dashboardViewModel: DashboardViewModel, statsViewModel: StatsViewModel) {
    val currentUser by com.example.myhabits.data.SessionManager.currentUser.collectAsState()
    val habits by dashboardViewModel.habits.collectAsState()
    val stats by statsViewModel.uiState.collectAsState()
    
    var isEditing by remember { mutableStateOf(false) }
    
    var name by remember(currentUser) { mutableStateOf(currentUser?.name ?: "") }
    var email by remember(currentUser) { mutableStateOf(currentUser?.email ?: "") }
    var password by remember(currentUser) { mutableStateOf(currentUser?.password ?: "") }

    val userLevel = when {
        stats.totalCompletions > 50 -> "ELITE 🏆"
        stats.totalCompletions > 25 -> "DISCIPLINADO ⚡"
        stats.totalCompletions > 10 -> "CONSTANTE 🌿"
        else -> "PRINCIPIANTE 🌱"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandDark)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        
        if (!isEditing) {
            // Profile Header
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(110.dp),
                    shape = RoundedCornerShape(32.dp),
                    color = CardGray,
                    border = androidx.compose.foundation.BorderStroke(2.dp, BrandGreen)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "👤", fontSize = 60.sp)
                    }
                }
                Surface(
                    color = BrandGreen,
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp).offset(x = 6.dp, y = 6.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("⚡", fontSize = 16.sp, color = BrandDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = currentUser?.name?.uppercase() ?: "ATLETA",
                color = SoftWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            
            Surface(
                color = BrandGreen.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandGreen.copy(alpha = 0.2f)),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = userLevel,
                    color = BrandGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Stats Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                QuickStatCard(
                    title = "Racha",
                    value = "${stats.currentStreak}",
                    unit = "días",
                    icon = "🔥",
                    modifier = Modifier.weight(1f)
                )
                QuickStatCard(
                    title = "Hábitos",
                    value = "${habits.size}",
                    unit = "total",
                    icon = "🌿",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                QuickStatCard(
                    title = "Logros",
                    value = "${stats.totalCompletions}",
                    unit = "votos",
                    icon = "✅",
                    modifier = Modifier.weight(1f)
                )
                QuickStatCard(
                    title = "Favoritos",
                    value = "${habits.count { it.isFavorite }}",
                    unit = "items",
                    icon = "⭐",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { isEditing = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CardGray, contentColor = SoftWhite),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftWhite.copy(alpha = 0.1f))
            ) {
                Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp), tint = BrandBlue)
                Spacer(modifier = Modifier.width(12.dp))
                Text("CONFIGURACIÓN DE PERFIL", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { 
                    com.example.myhabits.data.SessionManager.setCurrentUser(null)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SoftRed.copy(alpha = 0.1f), contentColor = SoftRed),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftRed.copy(alpha = 0.2f))
            ) {
                Text("CERRAR SESIÓN", fontWeight = FontWeight.Bold)
            }
        } else {
            // Edit Mode
            Text(
                text = "EDITAR PERFIL",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = SoftWhite,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandGreen,
                    unfocusedBorderColor = SoftWhite.copy(alpha = 0.1f),
                    focusedLabelColor = BrandGreen,
                    focusedTextColor = SoftWhite,
                    unfocusedTextColor = SoftWhite
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandGreen,
                    unfocusedBorderColor = SoftWhite.copy(alpha = 0.1f),
                    focusedLabelColor = BrandGreen,
                    focusedTextColor = SoftWhite,
                    unfocusedTextColor = SoftWhite
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Nueva Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandGreen,
                    unfocusedBorderColor = SoftWhite.copy(alpha = 0.1f),
                    focusedLabelColor = BrandGreen,
                    focusedTextColor = SoftWhite,
                    unfocusedTextColor = SoftWhite
                )
            )
            
            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { 
                    val oldEmail = currentUser?.email ?: ""
                    val updatedUser = com.example.myhabits.data.User(name, email, password)
                    com.example.myhabits.data.UserDatabase.updateUser(oldEmail, updatedUser)
                    com.example.myhabits.data.SessionManager.setCurrentUser(updatedUser)
                    isEditing = false 
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = BrandDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("GUARDAR CAMBIOS", fontWeight = FontWeight.Black)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = { isEditing = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("CANCELAR", color = SoftWhite.copy(alpha = 0.4f))
            }
        }
    }
}

@Composable
fun QuickStatCard(title: String, value: String, unit: String, icon: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = CardGray,
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftWhite.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftWhite.copy(alpha = 0.3f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = SoftWhite
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = unit,
                    fontSize = 12.sp,
                    color = SoftWhite.copy(alpha = 0.2f),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }
    }
}

@Composable
fun BottomBar(navController: androidx.navigation.NavHostController) {
    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Stats,
        BottomBarScreen.Profile
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = CardGray,
        contentColor = SoftWhite,
        tonalElevation = 8.dp
    ) {
        screens.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Text(
                        text = screen.icon,
                        fontSize = 22.sp,
                        color = if (selected) BrandGreen else SoftWhite.copy(alpha = 0.4f)
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
                        color = if (selected) BrandGreen else SoftWhite.copy(alpha = 0.4f),
                        fontSize = 10.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = BrandGreen.copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
