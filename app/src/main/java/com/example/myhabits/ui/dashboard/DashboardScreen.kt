package com.example.myhabits.ui.dashboard

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.text.style.TextDecoration
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
    
    val sortedHabits = remember(habits) { habits.sortedByDescending { it.isFavorite } }
    val activeHabits = habits.filter { !it.isPaused }
    val progress = remember(activeHabits) {
        if (activeHabits.isEmpty()) 0f else activeHabits.count { it.isCompleted }.toFloat() / activeHabits.size
    }

    var habitToEdit by remember { mutableStateOf<Habit?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        HabitDialog(
            habit = habitToEdit,
            onDismiss = { showDialog = false; habitToEdit = null },
            onConfirm = { name, goal, category, color, icon, frequency ->
                if (habitToEdit == null) {
                    viewModel.addHabit(name, goal, category, color, icon, frequency)
                } else {
                    viewModel.updateHabit(habitToEdit!!.copy(name = name, goal = goal, category = category, categoryColor = color, icon = icon, frequency = frequency))
                }
                showDialog = false
                habitToEdit = null
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(DeepBlack)) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            HeaderSection(userName)
            Spacer(modifier = Modifier.height(24.dp))
            WeeklyStatsSection()
            Spacer(modifier = Modifier.height(24.dp))
            HealthProgressCard(progress, activeHabits.count { it.isCompleted }, activeHabits.size)
            Spacer(modifier = Modifier.height(32.dp))
            
            SectionTitle("HÁBITOS DEL DÍA")
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(items = sortedHabits, key = { it.id }) { habit ->
                    HealthHabitItem(
                        habit = habit,
                        onToggle = { viewModel.toggleHabit(habit.id) },
                        onEdit = { habitToEdit = habit; showDialog = true },
                        onDelete = { viewModel.deleteHabit(habit.id) },
                        onFavorite = { viewModel.toggleFavorite(habit.id) },
                        onPause = { viewModel.togglePaused(habit.id) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { habitToEdit = null; showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = EnergyLime,
            contentColor = Color.Black,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp,
        color = Color.White.copy(alpha = 0.5f),
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
fun HeaderSection(userName: String) {
    val greetingInfo = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..12 -> "Buenos días" to "☀️"
            in 13..20 -> "Buenas tardes" to "🌤️"
            else -> "Buenas noches" to "🌙"
        }
    }
    
    val firstName = remember(userName) { userName.split(" ").firstOrNull() ?: "ATLETA" }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "${greetingInfo.first} ${greetingInfo.second},", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = EnergyLime)
            Text(text = firstName, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = Color.White)
        }
        Surface(modifier = Modifier.size(52.dp), shape = RoundedCornerShape(14.dp), color = DarkSurface, border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))) {
            Box(contentAlignment = Alignment.Center) { Text(text = "🔔", fontSize = 24.sp) }
        }
    }
}

@Composable
fun WeeklyStatsSection() {
    val days = listOf("LUN", "MAR", "MIE", "JUE", "VIE", "SAB", "DOM")
    val stats = listOf(0.7f, 0.9f, 0.4f, 0.0f, 0.0f, 0.0f, 0.0f)

    Column {
        Text(text = "ACTIVIDAD SEMANAL", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.4f))
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            days.forEachIndexed { index, day ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.width(32.dp).height(70.dp).clip(RoundedCornerShape(4.dp)).background(DarkSurface)) {
                        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(stats[index]).align(Alignment.BottomCenter).background(if (stats[index] >= 0.8f) EnergyLime else HealthBlue))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = day, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@Composable
fun HealthProgressCard(progress: Float, completed: Int, total: Int) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = DarkSurface, border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = "ESTADO DIARIO", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = EnergyLime)
                    Text(text = "$completed de $total completados", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f))
                }
                Text(text = "${(progress * 100).toInt()}%", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black, color = Color.White)
            }
            Spacer(modifier = Modifier.height(20.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape),
                color = EnergyLime,
                trackColor = Color.Black,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = if (progress < 1f) "Cerca de tu meta de hoy" else "¡Objetivos completados!", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = if (progress >= 1f) EnergyLime else Color.White)
        }
    }
}

@Composable
fun HealthHabitItem(habit: Habit, onToggle: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit, onFavorite: () -> Unit, onPause: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    val bgByState by animateColorAsState(targetValue = when {
        habit.isPaused -> DarkSurface.copy(alpha = 0.5f)
        habit.isCompleted -> EnergyLime.copy(alpha = 0.12f)
        else -> DarkSurface
    }, label = "bg")

    Surface(
        onClick = { if (!habit.isPaused) onToggle() },
        modifier = Modifier.fillMaxWidth().alpha(if (habit.isPaused) 0.5f else 1f),
        shape = RoundedCornerShape(16.dp),
        color = bgByState,
        border = BorderStroke(1.dp, when {
            habit.isPaused -> Color.White.copy(alpha = 0.05f)
            habit.isCompleted -> EnergyLime.copy(alpha = 0.6f)
            else -> Color.White.copy(alpha = 0.08f)
        })
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            HabitIcon(habit)
            Spacer(modifier = Modifier.width(16.dp))
            HabitInfo(habit, Modifier.weight(1f))
            HabitActions(habit, showMenu, { showMenu = it }, onEdit, onFavorite, onPause, onDelete)
            Spacer(modifier = Modifier.width(8.dp))
            HabitCheckbox(habit.isCompleted)
        }
    }
}

@Composable
private fun HabitIcon(habit: Habit) {
    Box(
        modifier = Modifier.size(46.dp).clip(RoundedCornerShape(14.dp))
            .background(if (habit.isCompleted) EnergyLime.copy(alpha = 0.2f) else habit.categoryColor.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = habit.icon, fontSize = 22.sp)
    }
}

@Composable
private fun HabitInfo(habit: Habit, modifier: Modifier) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = habit.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold,
                color = if (habit.isCompleted) EnergyLime else Color.White,
                textDecoration = if (habit.isCompleted) TextDecoration.LineThrough else null
            )
            if (habit.isFavorite) {
                Icon(Icons.Default.Favorite, null, tint = EnergyLime, modifier = Modifier.padding(start = 8.dp).size(16.dp))
            }
        }
        Text(text = "${habit.category} | ${habit.goal}", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.4f))
    }
}

@Composable
private fun HabitActions(habit: Habit, expanded: Boolean, onExpand: (Boolean) -> Unit, onEdit: () -> Unit, onFavorite: () -> Unit, onPause: () -> Unit, onDelete: () -> Unit) {
    Box {
        IconButton(onClick = { onExpand(true) }) {
            Icon(Icons.Default.MoreVert, null, tint = Color.White.copy(alpha = 0.4f))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { onExpand(false) }, modifier = Modifier.background(DarkSurface)) {
            DropdownMenuItem(text = { Text("Editar", color = Color.White) }, onClick = { onExpand(false); onEdit() })
            DropdownMenuItem(
                text = { Text(if (habit.isFavorite) "Quitar favorito" else "Favorito", color = Color.White) },
                onClick = { onExpand(false); onFavorite() },
                leadingIcon = { Icon(if (habit.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null, tint = EnergyLime) }
            )
            DropdownMenuItem(
                text = { Text(if (habit.isPaused) "Reanudar" else "Pausar", color = Color.White) },
                onClick = { onExpand(false); onPause() },
                leadingIcon = { Icon(if (habit.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause, null, tint = HealthBlue) }
            )
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            DropdownMenuItem(text = { Text("Eliminar", color = Color.Red) }, onClick = { onExpand(false); onDelete() })
        }
    }
}

@Composable
private fun HabitCheckbox(isCompleted: Boolean) {
    Box(
        modifier = Modifier.size(26.dp).clip(CircleShape)
            .background(if (isCompleted) EnergyLime else Color.Transparent)
            .border(2.dp, if (isCompleted) EnergyLime else Color.White.copy(alpha = 0.3f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) Text("✓", fontWeight = FontWeight.Black, color = Color.Black, fontSize = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    MyHabitsTheme { DashboardScreen() }
}
