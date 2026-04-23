package com.example.myhabits.ui.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.myhabits.data.Habit
import com.example.myhabits.ui.theme.MyHabitsTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Paleta Deportiva Profesional
val DeepBlack = Color(0xFF0A0A0A)
val DarkSurface = Color(0xFF1E1E1E)
val EnergyLime = Color(0xFFD4FF00)
val HealthBlue = Color(0xFF00D2FF)

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = DashboardViewModel()) {
    val habits by viewModel.habits.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val completedCount = habits.count { it.isCompleted }
    val totalCount = habits.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

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

            HealthProgressCard(progress)

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
                items(habits) { habit ->
                    HealthHabitItem(habit) { viewModel.toggleHabit(habit.id) }
                }
            }
        }

        FloatingActionButton(
            onClick = { viewModel.addRandomHabit() },
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "TU PROGRESO",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = EnergyLime,
                letterSpacing = 1.sp
            )
            Text(
                text = "¡HOLA, $userName!",
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
fun HealthProgressCard(progress: Float) {
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
                        text = "Objetivos cumplidos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineLarge,
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
fun HealthHabitItem(habit: Habit, onToggle: () -> Unit) {
    val bgByState by animateColorAsState(
        targetValue = if (habit.isCompleted) EnergyLime.copy(alpha = 0.08f) else DarkSurface,
        label = "bg"
    )

    Surface(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = bgByState,
        border = BorderStroke(
            1.dp, 
            if (habit.isCompleted) EnergyLime.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.05f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(habit.categoryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = habit.icon, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${habit.category} • ${habit.goal}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            // Checkbox Minimalista
            Surface(
                modifier = Modifier.size(24.dp),
                shape = CircleShape,
                color = if (habit.isCompleted) EnergyLime else Color.Transparent,
                border = BorderStroke(2.dp, if (habit.isCompleted) EnergyLime else Color.White.copy(alpha = 0.2f))
            ) {
                if (habit.isCompleted) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("✓", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                    }
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
