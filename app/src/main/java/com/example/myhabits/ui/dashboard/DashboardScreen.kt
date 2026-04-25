package com.example.myhabits.ui.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myhabits.data.Habit
import com.example.myhabits.ui.theme.*
import java.util.Calendar

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel()) {
    val habits by viewModel.habits.collectAsState()
    val userName by viewModel.userName.collectAsState()
    
    val sortedHabits = remember(habits) {
        habits.sortedByDescending { it.isFavorite }
    }
    
    val activeHabits = habits.filter { !it.isPaused }
    val completedCount = activeHabits.count { it.isCompleted }
    val totalCount = activeHabits.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    var showHabitDialog by remember { mutableStateOf(false) }
    var habitToEdit by remember { mutableStateOf<Habit?>(null) }

    if (showHabitDialog) {
        HabitDialog(
            habit = habitToEdit,
            onDismiss = { 
                showHabitDialog = false
                habitToEdit = null
            },
            onConfirm = { name, goal, category, color, icon, frequency ->
                if (habitToEdit == null) {
                    viewModel.addHabit(name, goal, category, color, icon, frequency)
                } else {
                    viewModel.updateHabit(habitToEdit!!.copy(
                        name = name,
                        goal = goal,
                        category = category,
                        categoryColor = color,
                        icon = icon,
                        frequency = frequency
                    ))
                }
                showHabitDialog = false
                habitToEdit = null
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            HeaderSection(userName)

            Spacer(modifier = Modifier.height(24.dp))

            WeeklyStatsSection()

            Spacer(modifier = Modifier.height(24.dp))

            HealthProgressCard(progress, completedCount, totalCount)

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "HÁBITOS DEL DÍA",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = Color.White.copy(alpha = 0.5f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(
                    items = sortedHabits,
                    key = { it.id }
                ) { habit ->
                    HealthHabitItem(
                        habit = habit,
                        onToggle = { viewModel.toggleHabit(habit.id) },
                        onEdit = {
                            habitToEdit = habit
                            showHabitDialog = true
                        },
                        onDelete = { viewModel.deleteHabit(habit.id) },
                        onFavorite = { viewModel.toggleFavorite(habit.id) },
                        onPause = { viewModel.togglePaused(habit.id) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { 
                habitToEdit = null
                showHabitDialog = true 
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = EnergyLime,
            contentColor = Color.Black,
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(text = "+", fontWeight = FontWeight.Bold, fontSize = 28.sp)
        }
    }
}

@Composable
fun HeaderSection(userName: String) {
    val (greeting, emoji) = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..12 -> "Buenos días" to "☀️"
            in 13..20 -> "Buenas tardes" to "🌤️"
            else -> "Buenas noches" to "🌙"
        }
    }
    
    val firstName = remember(userName) {
        userName.split(" ").firstOrNull() ?: "ATLETA"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "$greeting $emoji,",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = EnergyLime,
                letterSpacing = 1.sp
            )
            Text(
                text = firstName,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
        
        Surface(
            modifier = Modifier.size(52.dp),
            shape = RoundedCornerShape(14.dp),
            color = DarkSurface,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "🔔", fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun WeeklyStatsSection() {
    val days = listOf("LUN", "MAR", "MIE", "JUE", "VIE", "SAB", "DOM")
    val stats = listOf(0.7f, 0.9f, 0.4f, 0.0f, 0.0f, 0.0f, 0.0f)

    Column {
        Text(
            text = "ACTIVIDAD SEMANAL",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEachIndexed { index, day ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(70.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkSurface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(stats[index])
                                .align(Alignment.BottomCenter)
                                .background(if (stats[index] >= 0.8f) EnergyLime else HealthBlue)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun HealthProgressCard(progress: Float, completed: Int, total: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ESTADO DIARIO",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = EnergyLime
                    )
                    Text(
                        text = "$completed de $total completados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
            ) {
                val animatedProgress by animateFloatAsState(targetValue = progress, label = "p")
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(listOf(HealthBlue, EnergyLime))
                        )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (progress < 1f) "Cerca de tu meta de hoy" else "¡Objetivos completados!",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (progress >= 1f) EnergyLime else Color.White
            )
        }
    }
}

@Composable
fun HealthHabitItem(
    habit: Habit, 
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onFavorite: () -> Unit,
    onPause: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    val bgByState by animateColorAsState(
        targetValue = when {
            habit.isPaused -> DarkSurface.copy(alpha = 0.5f)
            habit.isCompleted -> EnergyLime.copy(alpha = 0.12f)
            else -> DarkSurface
        },
        label = "bg"
    )

    Surface(
        onClick = if (!habit.isPaused) onToggle else ({}),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp)
            .alpha(if (habit.isPaused) 0.5f else 1f),
        shape = RoundedCornerShape(16.dp),
        color = bgByState,
        tonalElevation = if (habit.isCompleted) 0.dp else 4.dp,
        border = BorderStroke(
            1.dp, 
            when {
                habit.isPaused -> Color.White.copy(alpha = 0.05f)
                habit.isCompleted -> EnergyLime.copy(alpha = 0.6f)
                else -> Color.White.copy(alpha = 0.08f)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (habit.isCompleted) EnergyLime.copy(alpha = 0.2f) 
                        else habit.categoryColor.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = habit.icon, 
                    fontSize = 22.sp,
                    modifier = Modifier.padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (habit.isCompleted) EnergyLime else Color.White,
                        textDecoration = if (habit.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                    )
                    if (habit.isFavorite) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Favorite, 
                            contentDescription = null, 
                            tint = EnergyLime, 
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = "${habit.category} | ${habit.goal}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Medium
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = Color.White.copy(alpha = 0.4f))
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(DarkSurface)
                ) {
                    DropdownMenuItem(
                        text = { Text("Editar", color = Color.White) },
                        onClick = {
                            showMenu = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (habit.isFavorite) "Quitar favorito" else "Favorito", color = Color.White) },
                        onClick = {
                            showMenu = false
                            onFavorite()
                        },
                        leadingIcon = {
                            Icon(
                                if (habit.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = EnergyLime
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (habit.isPaused) "Reanudar" else "Pausar", color = Color.White) },
                        onClick = {
                            showMenu = false
                            onPause()
                        },
                        leadingIcon = {
                            Icon(
                                if (habit.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = null,
                                tint = HealthBlue
                            )
                        }
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    DropdownMenuItem(
                        text = { Text("Eliminar", color = Color.Red) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Checkbox
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(if (habit.isCompleted) EnergyLime else Color.Transparent)
                    .border(2.dp, if (habit.isCompleted) EnergyLime else Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (habit.isCompleted) {
                    Text("✓", fontWeight = FontWeight.Black, color = Color.Black, fontSize = 16.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    MyHabitsTheme {
        DashboardScreen()
    }
}
