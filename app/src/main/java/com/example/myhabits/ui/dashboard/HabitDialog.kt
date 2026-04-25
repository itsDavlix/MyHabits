package com.example.myhabits.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.myhabits.data.Habit
import com.example.myhabits.ui.theme.DarkSurface
import com.example.myhabits.ui.theme.EnergyLime
import com.example.myhabits.ui.theme.HealthBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDialog(
    habit: Habit? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Color, String, String) -> Unit
) {
    var name by remember { mutableStateOf(habit?.name ?: "") }
    var category by remember { mutableStateOf(habit?.category ?: "") }
    var goal by remember { mutableStateOf(habit?.goal ?: "") }
    var selectedColor by remember { mutableStateOf(habit?.categoryColor ?: EnergyLime) }
    var selectedIcon by remember { mutableStateOf(habit?.icon ?: "✨") }
    var frequency by remember {
        mutableStateOf(
            if (habit?.frequency?.startsWith("Lun") == true || 
                habit?.frequency?.startsWith("Mar") == true ||
                habit?.frequency?.contains(",") == true) "Días específicos" 
            else habit?.frequency ?: "Diaria"
        )
    }
    
    val daysOfWeek = listOf("Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom")
    var selectedDays by remember { 
        mutableStateOf(
            if (frequency == "Días específicos") {
                habit?.frequency?.split(", ")?.toSet() ?: emptySet()
            } else emptySet()
        )
    }

    val colors = listOf(
        EnergyLime, HealthBlue, Color(0xFFFF3D00), Color(0xFFD500F9),
        Color(0xFF2979FF), Color(0xFF76FF03), Color(0xFFFFEA00), Color(0xFFC6FF00)
    )
    val icons = listOf("✨", "🏋️", "🏃", "🍗", "🧘", "💧", "🌙", "🔥", "📚", "🍎")
    val frequencies = listOf("Diaria", "Semanal", "Días específicos")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (habit == null) "NUEVO HÁBITO" else "EDITAR HÁBITO",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del hábito") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EnergyLime,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = EnergyLime,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = EnergyLime
                    )
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoría") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EnergyLime,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = EnergyLime,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = EnergyLime
                    )
                )

                OutlinedTextField(
                    value = goal,
                    onValueChange = { goal = it },
                    label = { Text("Meta (ej: 2L, 30 min, 5 km)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EnergyLime,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = EnergyLime,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = EnergyLime
                    )
                )

                Text(
                    text = "FRECUENCIA",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.5f)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    frequencies.forEach { freq ->
                        val selected = frequency == freq
                        Surface(
                            onClick = { frequency = freq },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) EnergyLime else Color.White.copy(alpha = 0.05f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = freq,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }

                if (frequency == "Días específicos") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        daysOfWeek.forEach { day ->
                            val isSelected = selectedDays.contains(day)
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) EnergyLime else Color.White.copy(alpha = 0.05f))
                                    .clickable {
                                        selectedDays = if (isSelected) {
                                            selectedDays - day
                                        } else {
                                            selectedDays + day
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.take(1),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "COLOR",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.5f)
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(colors) { color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { selectedColor = color }
                                .padding(4.dp)
                        ) {
                            if (selectedColor == color) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("✓", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Text(
                    text = "ÍCONO",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.5f)
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(icons) { icon ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedIcon == icon) EnergyLime else Color.White.copy(alpha = 0.05f))
                                .clickable { selectedIcon = icon },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = icon, fontSize = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank() && goal.isNotBlank() && category.isNotBlank()) {
                            val finalFrequency = if (frequency == "Días específicos") {
                                if (selectedDays.isEmpty()) "Días específicos" else selectedDays.joinToString(", ")
                            } else {
                                frequency
                            }
                            onConfirm(name, goal, category, selectedColor, selectedIcon, finalFrequency)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = EnergyLime, contentColor = Color.Black),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text(text = if (habit == null) "CREAR HÁBITO" else "GUARDAR CAMBIOS", fontWeight = FontWeight.Black)
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "CANCELAR", color = Color.White.copy(alpha = 0.6f))
                }
            }
        }
    }
}
