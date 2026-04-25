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
import com.example.myhabits.ui.theme.*

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
    var frequency by remember { mutableStateOf(habit?.frequency ?: "Diaria") }
    
    val daysOfWeek = listOf("Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom")
    var selectedDays by remember { 
        mutableStateOf(if (frequency.contains(",")) frequency.split(", ").toSet() else emptySet()) 
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f).padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = if (habit == null) "NUEVO HÁBITO" else "EDITAR HÁBITO", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = Color.White)

                HabitTextField("Nombre del hábito", name) { name = it }
                HabitTextField("Categoría", category) { category = it }
                HabitTextField("Meta (ej: 2L, 30 min)", goal) { goal = it }

                SectionHeader("FRECUENCIA")
                FrequencySelector(frequency) { 
                    frequency = it
                    if (it != "Días específicos") selectedDays = emptySet()
                }

                if (frequency == "Días específicos") {
                    DaysSelector(daysOfWeek, selectedDays) { selectedDays = it }
                }

                SectionHeader("COLOR")
                ColorPicker(selectedColor) { selectedColor = it }

                SectionHeader("ÍCONO")
                IconPicker(selectedIcon) { selectedIcon = it }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank() && goal.isNotBlank() && category.isNotBlank()) {
                            val finalFreq = if (frequency == "Días específicos") selectedDays.joinToString(", ") else frequency
                            onConfirm(name, goal, category, selectedColor, selectedIcon, finalFreq)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = EnergyLime, contentColor = Color.Black),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text(text = if (habit == null) "CREAR HÁBITO" else "GUARDAR CAMBIOS", fontWeight = FontWeight.Black)
                }

                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "CANCELAR", color = Color.White.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@Composable
private fun HabitTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EnergyLime,
            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
            focusedLabelColor = EnergyLime,
            unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
            cursorColor = EnergyLime,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
}

@Composable
private fun SectionHeader(title: String) {
    Text(text = title, style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.5f))
}

@Composable
private fun FrequencySelector(selected: String, onSelect: (String) -> Unit) {
    val frequencies = listOf("Diaria", "Semanal", "Días específicos")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        frequencies.forEach { freq ->
            val isSel = selected == freq || (selected.contains(",") && freq == "Días específicos")
            Surface(
                onClick = { onSelect(freq) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSel) EnergyLime else Color.White.copy(alpha = 0.05f),
                modifier = Modifier.weight(1f)
            ) {
                Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                    Text(text = freq, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.Black else Color.White)
                }
            }
        }
    }
}

@Composable
private fun DaysSelector(days: List<String>, selected: Set<String>, onUpdate: (Set<String>) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        days.forEach { day ->
            val isSel = selected.contains(day)
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape)
                    .background(if (isSel) EnergyLime else Color.White.copy(alpha = 0.05f))
                    .clickable { onUpdate(if (isSel) selected - day else selected + day) },
                contentAlignment = Alignment.Center
            ) {
                Text(text = day.take(1), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.Black else Color.White)
            }
        }
    }
}

@Composable
private fun ColorPicker(selected: Color, onSelect: (Color) -> Unit) {
    val colors = listOf(EnergyLime, HealthBlue, Color(0xFFFF3D00), Color(0xFFD500F9), Color(0xFF2979FF), Color(0xFF76FF03), Color(0xFFFFEA00), Color(0xFFC6FF00))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(colors) { color ->
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(color).clickable { onSelect(color) }, contentAlignment = Alignment.Center) {
                if (selected == color) Text("✓", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun IconPicker(selected: String, onSelect: (String) -> Unit) {
    val icons = listOf("✨", "🏋️", "🏃", "🍗", "🧘", "💧", "🌙", "🔥", "📚", "🍎")
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(icons) { icon ->
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp))
                    .background(if (selected == icon) EnergyLime else Color.White.copy(alpha = 0.05f))
                    .clickable { onSelect(icon) },
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 20.sp)
            }
        }
    }
}
