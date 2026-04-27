package com.example.myhabits.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhabits.ui.theme.*

@Composable
fun StatsScreen(viewModel: StatsViewModel) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandDark)
            .padding(20.dp)
    ) {
        Text(
            text = "PROGRESO PERSONAL",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = SoftWhite
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (state.totalCompletions == 0) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "📊", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "TU PROGRESO SE MOSTRARÁ AQUÍ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SoftWhite.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            WeeklyActivityChart(state.weeklyData)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item { StatCard("Racha Actual", "${state.currentStreak}", "días", BrandGreen) }
                item { StatCard("Mejor Racha", "${state.bestStreak}", "récord", BrandBlue) }
                item { StatCard("Completados", "${state.totalCompletions}", "total", BrandCyan) }
                item { StatCard("Rendimiento", "${(state.weeklyCompletionRate * 100).toInt()}%", "semanal", BrandLightGreen) }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            CategoryHighlightCard(state.topCategory)
        }
    }
}

@Composable
fun WeeklyActivityChart(data: List<Float>) {
    val days = listOf("L", "M", "M", "J", "V", "S", "D")
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = CardGray,
        border = BorderStroke(1.dp, SoftWhite.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "ACTIVIDAD SEMANAL",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = SoftWhite.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(140.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                data.forEachIndexed { index, value ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .fillMaxHeight(value.coerceIn(0.1f, 1f))
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(BrandGreen, BrandBlue)
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = days[index],
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = SoftWhite.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, unit: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = CardGray,
        border = BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = SoftWhite.copy(alpha = 0.3f),
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = color
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftWhite.copy(alpha = 0.2f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryHighlightCard(category: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = CardGray,
        border = BorderStroke(1.dp, BrandGreen.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(BrandGreen, BrandCyan)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🏆", fontSize = 32.sp)
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = "CATEGORÍA DOMINANTE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = BrandGreen,
                    letterSpacing = 1.sp
                )
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = SoftWhite
                )
            }
        }
    }
}
