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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
    val categories = listOf("Crecimiento", "Salud", "Deporte", "Mente", "Social", "Finanzas", "Otro...")
    
    var name by remember { mutableStateOf(habit?.name ?: "") }
    var customCategory by remember { mutableStateOf("") }
    var selectedCategory by remember { 
        mutableStateOf(if (habit != null && habit.category !in categories.dropLast(1)) "Otro..." else habit?.category ?: "") 
    }
    
    var goal by remember { mutableStateOf(habit?.goal ?: "") }
    var selectedColor by remember { mutableStateOf(habit?.categoryColor ?: BrandGreen) }
    var selectedIcon by remember { mutableStateOf(habit?.icon ?: "✨") }
    var frequency by remember { mutableStateOf(habit?.frequency ?: "Diaria") }
    var reminderTime by remember { mutableStateOf(habit?.reminderTime) }
    
    var categoryExpanded by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    val timePickerState = rememberTimePickerState(
        initialHour = reminderTime?.hour ?: 10,
        initialMinute = reminderTime?.minute ?: 0,
        is24Hour = false
    )
    
    val focusManager = LocalFocusManager.current
    
    val onHabitConfirm = {
        val finalCategory = if (selectedCategory == "Otro...") customCategory else selectedCategory
        if (name.isNotBlank() && goal.isNotBlank() && finalCategory.isNotBlank()) {
            onConfirm(name, goal, finalCategory, selectedColor, selectedIcon, frequency, reminderTime)
        }
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f).padding(16.dp),
            shape = RoundedCornerShape(32.dp),
            color = CardGray,
            border = androidx.compose.foundation.BorderStroke(1.dp, SoftWhite.copy(alpha = 0.05f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = if (habit == null) "NUEVO HÁBITO" else "EDITAR HÁBITO", 
                    style = MaterialTheme.typography.headlineSmall, 
                    fontWeight = FontWeight.Black, 
                    color = SoftWhite
                )

                HabitTextField(
                    label = "Nombre del hábito",
                    value = name,
                    onValueChange = { name = it },
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
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandGreen,
                                unfocusedBorderColor = SoftWhite.copy(alpha = 0.1f),
                                focusedLabelColor = BrandGreen,
                                unfocusedLabelColor = SoftWhite.copy(alpha = 0.5f),
                                focusedTextColor = SoftWhite,
                                unfocusedTextColor = SoftWhite
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                            modifier = Modifier.background(CardGray)
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat, color = SoftWhite) },
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
                        label = "Categoría personalizada",
                        value = customCategory,
                        onValueChange = { customCategory = it },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )
                }

                HabitTextField(
                    label = "Meta diaria (ej: 2L, 30 min, 10 páginas)",
                    value = goal,
                    onValueChange = { goal = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onHabitConfirm() })
                )

                SectionHeader("FRECUENCIA")
                FrequencySelector(frequency) { frequency = it }

                SectionHeader("COLOR Y ESTILO")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ColorPicker(selectedColor, Modifier.weight(1f)) { selectedColor = it }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconPicker(selectedIcon) { selectedIcon = it }
                }

                SectionHeader("RECORDATORIO")
                ReminderSelector(reminderTime, { showTimePicker = true }, { reminderTime = null })

                if (showTimePicker) {
                    TimePickerDialog(
                        onDismissRequest = { showTimePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                reminderTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                                showTimePicker = false
                            }) { Text("OK", color = BrandGreen, fontWeight = FontWeight.Bold) }
                        },
                        dismissButton = {
                            TextButton(onClick = { showTimePicker = false }) { Text("CANCELAR", color = SoftWhite.copy(alpha = 0.6f)) }
                        }
                    ) {
                        TimePicker(state = timePickerState)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onHabitConfirm,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = BrandDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = if (habit == null) "CREAR HÁBITO" else "GUARDAR CAMBIOS", fontWeight = FontWeight.Black)
                }

                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "CANCELAR", color = SoftWhite.copy(alpha = 0.4f))
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
        containerColor = CardGray,
        shape = RoundedCornerShape(28.dp)
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
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandGreen,
            unfocusedBorderColor = SoftWhite.copy(alpha = 0.1f),
            focusedLabelColor = BrandGreen,
            unfocusedLabelColor = SoftWhite.copy(alpha = 0.5f),
            cursorColor = BrandGreen,
            focusedTextColor = SoftWhite,
            unfocusedTextColor = SoftWhite
        )
    )
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title, 
        style = MaterialTheme.typography.labelLarge, 
        fontWeight = FontWeight.Bold, 
        color = SoftWhite.copy(alpha = 0.4f),
        letterSpacing = 1.sp
    )
}

@Composable
private fun FrequencySelector(selected: String, onSelect: (String) -> Unit) {
    val frequencies = listOf("Diaria", "Semanal")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        frequencies.forEach { freq ->
            val isSel = selected == freq
            Surface(
                onClick = { onSelect(freq) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSel) BrandGreen else SoftWhite.copy(alpha = 0.05f),
                modifier = Modifier.weight(1f)
            ) {
                Box(modifier = Modifier.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                    Text(text = freq, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isSel) BrandDark else SoftWhite)
                }
            }
        }
    }
}

@Composable
private fun ColorPicker(selected: Color, modifier: Modifier = Modifier, onSelect: (Color) -> Unit) {
    val colors = listOf(BrandGreen, BrandBlue, BrandCyan, BrandLightGreen, Color(0xFFFF8A65), Color(0xFFCE93D8))
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(colors) { color ->
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { onSelect(color) }, 
                contentAlignment = Alignment.Center
            ) {
                if (selected == color) Icon(Icons.Default.Check, null, tint = BrandDark, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun IconPicker(selected: String, onSelect: (String) -> Unit) {
    val icons = listOf("✨", "🌿", "💧", "🧘", "🏃", "🍎")
    var expanded by remember { mutableStateOf(false) }
    
    Box {
        Surface(
            onClick = { expanded = true },
            shape = RoundedCornerShape(12.dp),
            color = SoftWhite.copy(alpha = 0.05f),
            border = androidx.compose.foundation.BorderStroke(1.dp, SoftWhite.copy(alpha = 0.1f))
        ) {
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Text(text = selected, fontSize = 24.sp)
            }
        }
        
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(CardGray)) {
            Row(modifier = Modifier.padding(8.dp)) {
                icons.forEach { icon ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelect(icon); expanded = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = icon, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderSelector(time: LocalTime?, onSet: () -> Unit, onClear: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    Surface(
        onClick = onSet,
        shape = RoundedCornerShape(16.dp),
        color = SoftWhite.copy(alpha = 0.05f),
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
                    color = if (time != null) BrandGreen else SoftWhite.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold
                )
            }
            if (time != null) {
                IconButton(onClick = onClear, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, null, tint = SoftRed, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
