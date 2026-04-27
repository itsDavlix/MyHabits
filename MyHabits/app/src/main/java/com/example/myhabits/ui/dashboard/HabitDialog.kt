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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.myhabits.data.Habit
import com.example.myhabits.ui.theme.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDialog(
    habit: Habit? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Color, String, String, LocalTime?) -> Unit
) {
    val categories = listOf("Poder", "Cardio", "Nutrición", "Flex", "Recuperación", "Descanso", "Otro...")
    
    var name by remember { mutableStateOf(habit?.name ?: "") }
    var customCategory by remember { mutableStateOf("") }
    var selectedCategory by remember { 
        mutableStateOf(if (habit != null && habit.category !in categories.dropLast(1)) "Otro..." else habit?.category ?: "") 
    }
    
    var goal by remember { mutableStateOf(habit?.goal ?: "") }
    var selectedColor by remember { mutableStateOf(habit?.categoryColor ?: EnergyLime) }
    var selectedIcon by remember { mutableStateOf(habit?.icon ?: "✨") }
    var frequency by remember { mutableStateOf(habit?.frequency ?: "Diaria") }
    var reminderTime by remember { mutableStateOf(habit?.reminderTime) }
    
    var categoryExpanded by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = if (reminderTime != null) {
            if (reminderTime!!.hour == 0) 12 
            else if (reminderTime!!.hour > 12) reminderTime!!.hour - 12 
            else reminderTime!!.hour
        } else 10,
        initialMinute = reminderTime?.minute ?: 0,
        is24Hour = false
    )
    var amPmSelection by remember { mutableStateOf(if ((reminderTime?.hour ?: 10) < 12) "AM" else "PM") }
    
    val daysOfWeek = listOf("Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom")
    var selectedDays by remember { 
        mutableStateOf(if (frequency.contains(",")) frequency.split(", ").toSet() else emptySet()) 
    }
    
    val focusManager = LocalFocusManager.current
    
    val onHabitConfirm = {
        val finalCategory = if (selectedCategory == "Otro...") customCategory else selectedCategory
        if (name.isNotBlank() && goal.isNotBlank() && finalCategory.isNotBlank()) {
            val finalFreq = if (frequency == "Días específicos") selectedDays.joinToString(", ") else frequency
            onConfirm(name, goal, finalCategory, selectedColor, selectedIcon, finalFreq, reminderTime)
        }
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

                HabitTextField(
                    label = "Nombre del hábito",
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.onPreviewKeyEvent {
                        if ((it.key == Key.Tab || it.key == Key.Enter) && it.type == KeyEventType.KeyDown) {
                            focusManager.moveFocus(FocusDirection.Down)
                            true
                        } else false
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoría") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EnergyLime,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedLabelColor = EnergyLime,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                            modifier = Modifier.background(DarkSurface)
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat, color = Color.White) },
                                    onClick = {
                                        selectedCategory = cat
                                        categoryExpanded = false
                                        if (cat != "Otro...") customCategory = ""
                                    }
                                )
                            }
                        }
                    }
                }

                if (selectedCategory == "Otro...") {
                    HabitTextField(
                        label = "Nombre de la categoría personalizada",
                        value = if (customCategory.isEmpty() && habit != null && habit.category !in categories) habit.category.also { customCategory = it } else customCategory,
                        onValueChange = { customCategory = it },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )
                }

                HabitTextField(
                    label = "Meta (ej: 2L, 30 min)",
                    value = goal,
                    onValueChange = { goal = it },
                    modifier = Modifier.onPreviewKeyEvent {
                        if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                            onHabitConfirm()
                            true
                        } else false
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onHabitConfirm() })
                )

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

                SectionHeader("RECORDATORIO")
                ReminderSelector(reminderTime, { showTimePicker = true }, { reminderTime = null })

                if (showTimePicker) {
                    TimePickerDialog(
                        onDismissRequest = { showTimePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                var finalHour = timePickerState.hour
                                // El TimePicker con is24Hour=false ya maneja AM/PM internamente, 
                                // pero mantenemos los botones externos para cumplir con la petición de "tercer selector"
                                // y asegurar que el usuario tenga el control visual que pidió.
                                
                                // Si el usuario usó los botones externos de AM/PM, forzamos ese periodo:
                                if (amPmSelection == "AM") {
                                    if (finalHour >= 12) finalHour -= 12
                                } else { // PM
                                    if (finalHour < 12) finalHour += 12
                                }

                                reminderTime = LocalTime.of(finalHour, timePickerState.minute)
                                showTimePicker = false
                            }) { Text("OK", color = EnergyLime) }
                        },
                        dismissButton = {
                            TextButton(onClick = { showTimePicker = false }) { Text("CANCELAR", color = Color.White.copy(alpha = 0.6f)) }
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            TimePicker(state = timePickerState)
                            
                            // Nuevo selector manual de AM/PM
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                listOf("AM", "PM").forEach { period ->
                                    val isSel = amPmSelection == period
                                    Surface(
                                        onClick = { amPmSelection = period },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSel) EnergyLime else Color.White.copy(alpha = 0.1f),
                                        modifier = Modifier.width(80.dp).padding(horizontal = 4.dp)
                                    ) {
                                        Box(modifier = Modifier.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                            Text(
                                                text = period,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSel) Color.Black else Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onHabitConfirm,
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
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        text = content,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun HabitTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
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

@Composable
private fun ReminderSelector(time: LocalTime?, onSet: () -> Unit, onClear: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    Surface(
        onClick = onSet,
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.05f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "⏰", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = time?.format(formatter) ?: "Sin recordatorio",
                    color = if (time != null) EnergyLime else Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold
                )
            }
            if (time != null) {
                IconButton(onClick = { onClear() }, modifier = Modifier.size(24.dp)) {
                    Text(text = "✕", color = Color.Red, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
