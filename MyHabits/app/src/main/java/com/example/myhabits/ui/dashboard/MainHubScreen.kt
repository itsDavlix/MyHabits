package com.example.myhabits.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        stats.totalCompletions > 30 -> "ELITE 🏆"
        stats.totalCompletions > 15 -> "DISCIPLINADO 💪"
        stats.totalCompletions > 5 -> "CONSTANTE ⚡"
        else -> "PRINCIPIANTE 🌱"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(com.example.myhabits.ui.theme.DeepBlack)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        
        if (!isEditing) {
            // Profile Header
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = RoundedCornerShape(30.dp),
                    color = com.example.myhabits.ui.theme.DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(2.dp, com.example.myhabits.ui.theme.EnergyLime)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "👤", fontSize = 50.sp)
                    }
                }
                Surface(
                    color = com.example.myhabits.ui.theme.EnergyLime,
                    shape = CircleShape,
                    modifier = Modifier.size(28.dp).offset(x = 4.dp, y = 4.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("⚡", fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = currentUser?.name?.uppercase() ?: "ATLETA",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
            
            Surface(
                color = com.example.myhabits.ui.theme.EnergyLime.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.myhabits.ui.theme.EnergyLime.copy(alpha = 0.3f)),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = "NIVEL DISCIPLINA: $userLevel",
                    color = com.example.myhabits.ui.theme.EnergyLime,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Stats Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickStatCard(
                    title = "Racha Actual",
                    value = "${stats.currentStreak}",
                    unit = "días",
                    icon = "🔥",
                    modifier = Modifier.weight(1f)
                )
                QuickStatCard(
                    title = "Hábitos",
                    value = "${habits.size}",
                    unit = "totales",
                    icon = "📝",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickStatCard(
                    title = "Completados",
                    value = "${stats.totalCompletions}",
                    unit = "veces",
                    icon = "✅",
                    modifier = Modifier.weight(1f)
                )
                QuickStatCard(
                    title = "Favoritos",
                    value = "${habits.count { it.isFavorite }}",
                    unit = "habits",
                    icon = "⭐",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { isEditing = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = com.example.myhabits.ui.theme.DarkSurface, contentColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Icon(androidx.compose.material.icons.Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("EDITAR PERFIL", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = { 
                    com.example.myhabits.data.SessionManager.setCurrentUser(null)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f), contentColor = Color.Red),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.2f))
            ) {
                Text("CERRAR SESIÓN", fontWeight = FontWeight.Bold)
            }
        } else {
            // Edit Mode (kept mostly as is but styled better)
            Text(
                text = "EDITAR PERFIL",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = com.example.myhabits.ui.theme.EnergyLime,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedLabelColor = com.example.myhabits.ui.theme.EnergyLime,
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = com.example.myhabits.ui.theme.EnergyLime,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedLabelColor = com.example.myhabits.ui.theme.EnergyLime,
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Nueva Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = com.example.myhabits.ui.theme.EnergyLime,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedLabelColor = com.example.myhabits.ui.theme.EnergyLime,
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White
                )
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { 
                    val oldEmail = currentUser?.email ?: ""
                    val updatedUser = com.example.myhabits.data.User(name, email, password)
                    com.example.myhabits.data.UserDatabase.updateUser(oldEmail, updatedUser)
                    com.example.myhabits.data.SessionManager.setCurrentUser(updatedUser)
                    isEditing = false 
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = com.example.myhabits.ui.theme.EnergyLime, contentColor = Color.Black),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("GUARDAR CAMBIOS", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = { isEditing = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("CANCELAR", color = Color.White.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun QuickStatCard(title: String, value: String, unit: String, icon: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = com.example.myhabits.ui.theme.DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.padding(bottom = 4.dp)
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
        containerColor = Color(0xFF1A1A1A),
        contentColor = Color.White
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
                        fontSize = 20.sp,
                        color = if (selected) Color(0xFFD4FF00) else Color.White.copy(alpha = 0.5f)
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) Color(0xFFD4FF00) else Color.White.copy(alpha = 0.5f)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
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
