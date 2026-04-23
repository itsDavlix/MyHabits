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

// Paleta Deportiva
val SportsBlack = Color(0xFF000000)
val SportsGray = Color(0xFF1A1A1A)
val NeonVolt = Color(0xFFCCFF00)
val ElectricBlue = Color(0xFF00E5FF)

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = DashboardViewModel()) {
    val habits by viewModel.habits.collectAsState()
    val completedCount = habits.count { it.isCompleted }
    val totalCount = habits.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SportsBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            HeaderSection()

            Spacer(modifier = Modifier.height(24.dp))

            WeeklyPerformanceSection()

            Spacer(modifier = Modifier.height(24.dp))

            PowerLevelCard(progress, completedCount, totalCount)

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "MISIONES DIARIAS",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(habits) { habit ->
                    SportHabitItem(habit) { viewModel.toggleHabit(habit.id) }
                }
            }
        }

        // Action Button: Start Workout Style
        FloatingActionButton(
            onClick = { viewModel.addRandomHabit() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = NeonVolt,
            contentColor = Color.Black,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "IR", fontWeight = FontWeight.Black, fontSize = 20.sp)
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "JUGADOR 1",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = NeonVolt,
                letterSpacing = 1.sp
            )
            Text(
                text = "ANA CORE",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                color = Color.White
            )
        }
        
        Surface(
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(12.dp),
            color = SportsGray,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "🔔", fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun WeeklyPerformanceSection() {
    val days = listOf("LUN", "MAR", "MIE", "JUE", "VIE", "SAB", "DOM")
    val stats = listOf(0.8f, 1.0f, 0.4f, 0.0f, 0.0f, 0.0f, 0.0f)

    Column {
        Text(
            text = "RENDIMIENTO SEMANAL",
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
                            .width(36.dp)
                            .height(80.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(SportsGray)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(stats[index])
                                .align(Alignment.BottomCenter)
                                .background(if (stats[index] >= 1f) NeonVolt else ElectricBlue)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun PowerLevelCard(progress: Float, completed: Int, total: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        color = SportsGray,
        border = BorderStroke(2.dp, NeonVolt.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "ENERGÍA DIARIA",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    color = Color.White
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    color = NeonVolt
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de progreso
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .background(Color.Black)
                    .padding(2.dp)
            ) {
                val animatedProgress by animateFloatAsState(targetValue = progress, label = "p")
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(listOf(ElectricBlue, NeonVolt))
                        )
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "ESTADO: ${if (progress < 0.5f) "SIGUE ENTRENANDO" else "NIVEL ELITE"}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = NeonVolt
            )
        }
    }
}

@Composable
fun SportHabitItem(habit: Habit, onToggle: () -> Unit) {
    val bgByState by animateColorAsState(
        targetValue = if (habit.isCompleted) NeonVolt.copy(alpha = 0.1f) else SportsGray,
        label = "bg"
    )

    Surface(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = bgByState,
        border = BorderStroke(
            1.dp, 
            if (habit.isCompleted) NeonVolt else Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(habit.categoryColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = habit.icon, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name.uppercase(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Black,
                    color = if (habit.isCompleted) NeonVolt else Color.White
                )
                Text(
                    text = "${habit.category} • ${habit.goal}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            // Custom Sporty Checkbox
            Surface(
                modifier = Modifier.size(28.dp),
                shape = RoundedCornerShape(4.dp),
                color = if (habit.isCompleted) NeonVolt else Color.Transparent,
                border = BorderStroke(2.dp, if (habit.isCompleted) NeonVolt else Color.White.copy(alpha = 0.3f))
            ) {
                if (habit.isCompleted) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("X", fontWeight = FontWeight.Black, color = Color.Black, fontSize = 14.sp)
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
