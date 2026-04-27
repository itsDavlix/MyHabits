package com.example.myhabits.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myhabits.data.Habit
import com.example.myhabits.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Calendar
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    statsViewModel: StatsViewModel? = null
) {
    val habits by viewModel.habitsForSelectedDate.collectAsState()
    val allHabits by viewModel.habits.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val statsState = statsViewModel?.uiState?.collectAsState()?.value ?: StatsState()
    
    val sortedHabits = remember(habits) { habits.sortedByDescending { it.isFavorite } }
    val activeHabitsOnSelectedDate = allHabits.filter { it.isActiveOn(selectedDate) || it.isCompletedOn(selectedDate) }
    val progress = remember(activeHabitsOnSelectedDate, selectedDate) {
        if (activeHabitsOnSelectedDate.isEmpty()) 0f 
        else activeHabitsOnSelectedDate.count { it.isCompletedOn(selectedDate) }.toFloat() / activeHabitsOnSelectedDate.size
    }

    var habitToEdit by remember { mutableStateOf<Habit?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    
    // Estados para el overlay motivacional
    var showMotivation by remember { mutableStateOf(false) }
    var showDayComplete by remember { mutableStateOf(false) }
    var motivationalMessage by remember { mutableStateOf("") }
    val motivationalPhrases = listOf(
        "¡Buen trabajo!",
        "Otro paso más cerca de tu meta.",
        "Disciplina completada.",
        "Racha en progreso.",
        "¡Estás imparable!",
        "Compromiso demostrado.",
        "Cada pequeño paso cuenta."
    )

    LaunchedEffect(showMotivation) {
        if (showMotivation) {
            delay(2000)
            showMotivation = false
        }
    }

    LaunchedEffect(showDayComplete) {
        if (showDayComplete) {
            delay(3500) // Un poco más largo para que se vea el confeti
            showDayComplete = false
        }
    }

    if (showDialog) {
        HabitDialog(
            habit = habitToEdit,
            onDismiss = { showDialog = false; habitToEdit = null },
            onConfirm = { name, goal, category, color, icon, frequency, reminderTime ->
                if (habitToEdit == null) {
                    viewModel.addHabit(name, goal, category, color, icon, frequency, reminderTime)
                } else {
                    viewModel.updateHabit(habitToEdit!!.copy(
                        name = name, 
                        goal = goal, 
                        category = category, 
                        categoryColor = color, 
                        icon = icon, 
                        frequency = frequency,
                        reminderTime = reminderTime
                    ))
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
            
            if (statsState.currentStreak > 0) {
                StreakBanner(statsState.streakMessage, statsState.motivationMessage)
                Spacer(modifier = Modifier.height(24.dp))
            }

            WeeklyStatsSection(statsState.weeklyData, selectedDate, onDateSelected = { viewModel.setSelectedDate(it) })
            Spacer(modifier = Modifier.height(24.dp))
            HealthProgressCard(progress, activeHabitsOnSelectedDate.count { it.isCompletedOn(selectedDate) }, activeHabitsOnSelectedDate.size, selectedDate)
            Spacer(modifier = Modifier.height(32.dp))
            
            val sectionTitle = when (selectedDate) {
                LocalDate.now() -> "HÁBITOS DE HOY"
                LocalDate.now().minusDays(1) -> "HÁBITOS DE AYER"
                LocalDate.now().plusDays(1) -> "HÁBITOS DE MAÑANA"
                else -> {
                    val locale = Locale("es", "ES")
                    "HÁBITOS DEL ${selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, locale).uppercase()}"
                }
            }
            SectionTitle(sectionTitle)
            
            if (sortedHabits.isEmpty()) {
                EmptyHabitsState()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(items = sortedHabits, key = { it.id }) { habit ->
                        HealthHabitItem(
                            habit = habit,
                            selectedDate = selectedDate,
                            onToggle = { 
                                val wasCompleted = habit.isCompletedOn(selectedDate)
                                viewModel.toggleHabit(habit.id) 
                                
                                if (!wasCompleted) {
                                    // Verificar si después de este toggle todo el día está completo
                                    val willBeAllCompleted = activeHabitsOnSelectedDate.all { 
                                        if (it.id == habit.id) true else it.isCompletedOn(selectedDate) 
                                    }
                                    
                                    if (willBeAllCompleted && activeHabitsOnSelectedDate.isNotEmpty()) {
                                        showDayComplete = true
                                    } else {
                                        motivationalMessage = motivationalPhrases.random()
                                        showMotivation = true
                                    }
                                }
                            },
                            onEdit = { habitToEdit = habit; showDialog = true },
                            onDelete = { viewModel.deleteHabit(habit.id) },
                            onFavorite = { viewModel.toggleFavorite(habit.id) },
                            onPause = { viewModel.togglePaused(habit.id) }
                        )
                    }
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

        // Overlay Motivacional
        MotivationalOverlay(
            isVisible = showMotivation,
            message = motivationalMessage
        )

        // Overlay de Día Completado
        DayCompleteOverlay(
            isVisible = showDayComplete
        )
    }
}

@Composable
fun EmptyHabitsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = DarkSurface,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "📝", fontSize = 48.sp)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Aún no tienes hábitos para este día.",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Crea tu primer hábito con el botón +",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun DayCompleteOverlay(isVisible: Boolean) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(500)) + scaleIn(
            initialScale = 0.8f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        ),
        exit = fadeOut(animationSpec = tween(500)) + scaleOut(targetScale = 1.1f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            EmojiConfeti()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(EnergyLime, EnergyLime.copy(alpha = 0.8f))
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(horizontal = 40.dp, vertical = 48.dp)
            ) {
                Text(
                    text = "🌟",
                    fontSize = 48.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "DÍA COMPLETADO",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "¡ERES IMPARABLE!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun EmojiConfeti() {
    val emojis = listOf("🎉", "🔥", "💪", "🏆", "⚡", "✨")

    Box(modifier = Modifier.fillMaxSize()) {
        repeat(25) { index ->
            val startX = remember { (0..100).random().toFloat() / 100f }
            val startY = remember { (0..100).random().toFloat() / 100f }
            val emoji = remember { emojis.random() }
            
            val infiniteTransition = rememberInfiniteTransition(label = "emoji_anim_$index")
            
            val yOffset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 40f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "y_offset"
            )
            
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = LinearOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = yOffset.dp),
                contentAlignment = androidx.compose.ui.BiasAlignment(startX * 2 - 1, startY * 2 - 1)
            ) {
                Text(
                    text = emoji,
                    fontSize = 28.sp,
                    modifier = Modifier
                        .scale(scale)
                        .alpha(0.7f)
                )
            }
        }
    }
}

@Composable
fun MotivationalOverlay(isVisible: Boolean, message: String) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + scaleIn(initialScale = 0.8f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
        exit = fadeOut() + scaleOut(targetScale = 1.1f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f)) // Fondo oscuro sólido para máximo contraste
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .background(
                        color = DarkSurface,
                        shape = RoundedCornerShape(28.dp)
                    )
                    .border(2.dp, EnergyLime, RoundedCornerShape(28.dp))
                    .padding(horizontal = 32.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "✨",
                    fontSize = 56.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = message.uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = EnergyLime,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp,
                    lineHeight = 36.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .height(4.dp)
                        .width(80.dp)
                        .clip(CircleShape)
                        .background(EnergyLime)
                )
            }
        }
    }
}

@Composable
fun StreakBanner(message: String, motivation: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = EnergyLime,
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🔥", fontSize = 28.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
                Text(
                    text = motivation,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }
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
fun WeeklyStatsSection(weeklyData: List<Float>, selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    val days = listOf("LUN", "MAR", "MIE", "JUE", "VIE", "SAB", "DOM")
    val today = LocalDate.now()
    val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)

    Column {
        Text(text = "ACTIVIDAD SEMANAL", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.4f))
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            days.forEachIndexed { index, day ->
                val date = startOfWeek.plusDays(index.toLong())
                val isSelected = date == selectedDate
                val progressValue = weeklyData.getOrElse(index) { 0f }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onDateSelected(date) }
                ) {
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(70.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) EnergyLime.copy(alpha = 0.2f) else DarkSurface)
                            .border(
                                if (isSelected) 1.dp else 0.dp, 
                                if (isSelected) EnergyLime else Color.Transparent, 
                                RoundedCornerShape(4.dp)
                            )
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(progressValue.coerceIn(0f, 1f)).align(Alignment.BottomCenter).background(if (progressValue >= 0.8f) EnergyLime else HealthBlue))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = day, 
                        style = MaterialTheme.typography.labelSmall, 
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                        color = if (isSelected) EnergyLime else Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun HealthProgressCard(progress: Float, completed: Int, total: Int, selectedDate: LocalDate) {
    val dateText = when (selectedDate) {
        LocalDate.now() -> "HOY"
        else -> selectedDate.format(DateTimeFormatter.ofPattern("dd MMM"))
    }
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = DarkSurface, border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = "ESTADO ($dateText)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = EnergyLime)
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
            Text(text = if (progress < 1f) "Cerca de tu meta" else "¡Objetivos completados!", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = if (progress >= 1f) EnergyLime else Color.White)
        }
    }
}

@Composable
fun HealthHabitItem(habit: Habit, selectedDate: LocalDate, onToggle: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit, onFavorite: () -> Unit, onPause: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    val isCompletedOnDate = habit.isCompletedOn(selectedDate)
    
    val bgByState by animateColorAsState(targetValue = when {
        habit.isPaused -> DarkSurface.copy(alpha = 0.5f)
        isCompletedOnDate -> EnergyLime.copy(alpha = 0.12f)
        else -> DarkSurface
    }, label = "bg")

    Surface(
        onClick = { if (!habit.isPaused) onToggle() },
        modifier = Modifier.fillMaxWidth().alpha(if (habit.isPaused) 0.5f else 1f),
        shape = RoundedCornerShape(16.dp),
        color = bgByState,
        border = BorderStroke(1.dp, when {
            habit.isPaused -> Color.White.copy(alpha = 0.05f)
            isCompletedOnDate -> EnergyLime.copy(alpha = 0.6f)
            else -> Color.White.copy(alpha = 0.08f)
        })
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            HabitIcon(habit, isCompletedOnDate)
            Spacer(modifier = Modifier.width(16.dp))
            HabitInfo(habit, isCompletedOnDate, Modifier.weight(1f))
            HabitActions(habit, showMenu, { showMenu = it }, onEdit, onFavorite, onPause, onDelete)
            Spacer(modifier = Modifier.width(8.dp))
            HabitCheckbox(isCompletedOnDate)
        }
    }
}

@Composable
private fun HabitIcon(habit: Habit, isCompletedOnDate: Boolean) {
    Box(
        modifier = Modifier.size(46.dp).clip(RoundedCornerShape(14.dp))
            .background(if (isCompletedOnDate) EnergyLime.copy(alpha = 0.2f) else habit.categoryColor.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = habit.icon, fontSize = 22.sp)
    }
}

@Composable
private fun HabitInfo(habit: Habit, isCompletedOnDate: Boolean, modifier: Modifier) {
    val reminderFormatter = remember { DateTimeFormatter.ofPattern("hh:mm a") }
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = habit.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold,
                color = if (isCompletedOnDate) EnergyLime else Color.White,
                textDecoration = if (isCompletedOnDate) TextDecoration.LineThrough else null
            )
            if (habit.isFavorite) {
                Icon(Icons.Default.Favorite, null, tint = EnergyLime, modifier = Modifier.padding(start = 8.dp).size(16.dp))
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "${habit.category} | ${habit.goal}", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.4f))
            habit.reminderTime?.let { time ->
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "⏰ ${time.format(reminderFormatter)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = EnergyLime.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
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
