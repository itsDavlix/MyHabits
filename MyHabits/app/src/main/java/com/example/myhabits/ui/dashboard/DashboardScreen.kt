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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
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
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    statsViewModel: StatsViewModel? = null
) {
    val habits by viewModel.habitsForSelectedDate.collectAsState()
    val allHabits by viewModel.habits.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    val statsState = statsViewModel?.uiState?.collectAsState()?.value ?: StatsState()
    
    val sortedHabits = remember(habits) { habits.sortedByDescending { it.isFavorite } }
    val activeHabitsOnSelectedDate = allHabits.filter { (it.isActiveOn(selectedDate) && !it.isPaused) || it.isCompletedOn(selectedDate) }
    val progress = remember(activeHabitsOnSelectedDate, selectedDate) {
        if (activeHabitsOnSelectedDate.isEmpty()) 0f 
        else activeHabitsOnSelectedDate.count { it.isCompletedOn(selectedDate) }.toFloat() / activeHabitsOnSelectedDate.size
    }

    var habitToEdit by remember { mutableStateOf<Habit?>(null) }
    var habitToDelete by remember { mutableStateOf<Habit?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    
    // Estados para el overlay motivacional
    var showMotivation by remember { mutableStateOf(false) }
    var showDayComplete by remember { mutableStateOf(false) }
    var motivationalMessage by remember { mutableStateOf("") }
    val motivationalPhrases = listOf(
        "¡Buen trabajo! 🌿",
        "Otro paso hacia tu bienestar.",
        "¡Disciplina de acero!",
        "Racha imparable ⚡",
        "¡Estás creciendo!",
        "Compromiso real.",
        "Cada paso cuenta."
    )

    LaunchedEffect(showMotivation) {
        if (showMotivation) {
            delay(2000)
            showMotivation = false
        }
    }

    LaunchedEffect(showDayComplete) {
        if (showDayComplete) {
            delay(3500)
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

    Box(modifier = Modifier.fillMaxSize().background(BrandDark)) {
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
            
            SectionTitle("HÁBITOS DEL DÍA")

            FilterChipsSection(
                currentFilter = currentFilter,
                onFilterSelected = { viewModel.setFilter(it) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
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
                            onDelete = { habitToDelete = habit },
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
            containerColor = BrandGreen,
            contentColor = BrandDark,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(32.dp))
        }

        if (habitToDelete != null) {
            AlertDialog(
                onDismissRequest = { habitToDelete = null },
                title = {
                    Text(
                        text = "¿Eliminar este hábito?",
                        color = SoftWhite,
                        fontWeight = FontWeight.Bold
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            habitToDelete?.let { viewModel.deleteHabit(it.id) }
                            habitToDelete = null
                        }
                    ) {
                        Text("Eliminar", color = SoftRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { habitToDelete = null }) {
                        Text("Cancelar", color = SoftWhite.copy(alpha = 0.6f))
                    }
                },
                containerColor = CardGray,
                shape = RoundedCornerShape(24.dp)
            )
        }

        MotivationalOverlay(
            isVisible = showMotivation,
            message = motivationalMessage
        )

        DayCompleteOverlay(
            isVisible = showDayComplete
        )
    }
}

@Composable
fun FilterChipsSection(currentFilter: HabitFilter, onFilterSelected: (HabitFilter) -> Unit) {
    val filters = listOf(
        "Todos" to HabitFilter.ALL,
        "Favoritos" to HabitFilter.FAVORITES,
        "Pendientes" to HabitFilter.PENDING,
        "Completados" to HabitFilter.COMPLETED
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { (label, filter) ->
            val isSelected = currentFilter == filter
            Surface(
                onClick = { onFilterSelected(filter) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) BrandGreen else CardGray,
                border = BorderStroke(1.dp, if (isSelected) BrandGreen else SoftWhite.copy(alpha = 0.1f)),
                modifier = Modifier.weight(1f)
            ) {
                Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) BrandDark else SoftWhite.copy(alpha = 0.7f)
                    )
                }
            }
        }
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
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(CardGray, CircleShape)
                .border(1.dp, SoftWhite.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🌿", fontSize = 56.sp)
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "TU CAMINO EMPIEZA AQUÍ",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = SoftWhite,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Crea un hábito y cultiva tu progreso diario.",
            style = MaterialTheme.typography.bodyMedium,
            color = SoftWhite.copy(alpha = 0.6f),
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
                .background(BrandDark.copy(alpha = 0.9f))
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
                            colors = listOf(BrandGreen, BrandDarkGreen)
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
                    color = SoftWhite,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "¡TU DISCIPLINA ES TU PODER!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SoftWhite.copy(alpha = 0.8f),
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
                .background(BrandDark.copy(alpha = 0.9f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .background(
                        color = CardGray,
                        shape = RoundedCornerShape(32.dp)
                    )
                    .border(2.dp, BrandGreen, RoundedCornerShape(32.dp))
                    .padding(horizontal = 40.dp, vertical = 48.dp),
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
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = BrandGreen,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp,
                    lineHeight = 32.sp,
                    modifier = Modifier.fillMaxWidth()
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
        color = BrandBlue.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, BrandBlue.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(BrandBlue.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🔥", fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Black,
                    color = SoftWhite
                )
                Text(
                    text = motivation,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = BrandCyan
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
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 2.sp,
        color = SoftWhite.copy(alpha = 0.4f),
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
fun HeaderSection(userName: String) {
    val greetingInfo = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..12 -> "Buenos días" to "☀️"
            in 13..20 -> "Buenas tardes" to "🌿"
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
            Text(text = "${greetingInfo.first} ${greetingInfo.second},", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = BrandGreen)
            Text(text = firstName, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = SoftWhite)
        }
        Surface(modifier = Modifier.size(52.dp), shape = RoundedCornerShape(16.dp), color = CardGray, border = BorderStroke(1.dp, SoftWhite.copy(alpha = 0.1f))) {
            Box(contentAlignment = Alignment.Center) { Text(text = "🌱", fontSize = 24.sp) }
        }
    }
}

@Composable
fun WeeklyStatsSection(weeklyData: List<Float>, selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    val days = listOf("L", "M", "M", "J", "V", "S", "D")
    val today = LocalDate.now()
    val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)

    Column {
        Text(text = "PROGRESO SEMANAL", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = SoftWhite.copy(alpha = 0.4f))
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
                            .width(36.dp)
                            .height(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) BrandGreen.copy(alpha = 0.1f) else CardGray)
                            .border(
                                1.dp, 
                                if (isSelected) BrandGreen else SoftWhite.copy(alpha = 0.05f), 
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(progressValue.coerceIn(0.05f, 1f)).align(Alignment.BottomCenter).background(if (progressValue >= 0.8f) BrandGreen else BrandBlue))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = day, 
                        style = MaterialTheme.typography.labelSmall, 
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                        color = if (isSelected) BrandGreen else SoftWhite.copy(alpha = 0.4f)
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
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = CardGray,
        border = BorderStroke(1.dp, SoftWhite.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = "PROGRESO $dateText", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = BrandCyan)
                    Text(text = "$completed de $total completados", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SoftWhite)
                }
                Text(text = "${(progress * 100).toInt()}%", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, color = BrandGreen)
            }
            Spacer(modifier = Modifier.height(20.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape),
                color = BrandGreen,
                trackColor = BrandDark,
            )
        }
    }
}

@Composable
fun HealthHabitItem(habit: Habit, selectedDate: LocalDate, onToggle: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit, onFavorite: () -> Unit, onPause: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    val isCompletedOnDate = habit.isCompletedOn(selectedDate)
    
    val bgByState by animateColorAsState(
        targetValue = when {
            habit.isPaused -> CardGray.copy(alpha = 0.4f)
            isCompletedOnDate -> BrandGreen.copy(alpha = 0.08f)
            else -> CardGray
        },
        animationSpec = tween(durationMillis = 400),
        label = "bg"
    )
    
    val borderByState by animateColorAsState(
        targetValue = when {
            habit.isPaused -> SoftWhite.copy(alpha = 0.05f)
            isCompletedOnDate -> BrandGreen.copy(alpha = 0.4f)
            else -> SoftWhite.copy(alpha = 0.1f)
        },
        animationSpec = tween(durationMillis = 400),
        label = "border"
    )

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    Surface(
        onClick = { if (!habit.isPaused) onToggle() },
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .alpha(if (habit.isPaused) 0.5f else 1f)
            .pointerInput(habit.isPaused) {
                if (!habit.isPaused) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            when (event.type) {
                                PointerEventType.Press -> isPressed = true
                                PointerEventType.Release -> isPressed = false
                                PointerEventType.Exit -> isPressed = false
                            }
                        }
                    }
                }
            },
        shape = RoundedCornerShape(20.dp),
        color = bgByState,
        border = BorderStroke(1.dp, borderByState)
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
        modifier = Modifier.size(52.dp).clip(RoundedCornerShape(16.dp))
            .background(if (isCompletedOnDate) BrandGreen.copy(alpha = 0.15f) else habit.categoryColor.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = habit.icon, fontSize = 26.sp)
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
                color = if (isCompletedOnDate) BrandGreen else SoftWhite,
                textDecoration = if (isCompletedOnDate) TextDecoration.LineThrough else null
            )
            if (habit.isFavorite) {
                Icon(Icons.Default.Favorite, null, tint = BrandGreen, modifier = Modifier.padding(start = 8.dp).size(14.dp))
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
            Surface(
                color = BrandCyan.copy(alpha = 0.1f),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, BrandCyan.copy(alpha = 0.1f))
            ) {
                Text(
                    text = habit.category.uppercase(), 
                    style = MaterialTheme.typography.labelSmall, 
                    fontWeight = FontWeight.Bold,
                    color = BrandCyan,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = habit.goal, style = MaterialTheme.typography.labelMedium, color = SoftWhite.copy(alpha = 0.4f), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun HabitActions(habit: Habit, expanded: Boolean, onExpand: (Boolean) -> Unit, onEdit: () -> Unit, onFavorite: () -> Unit, onPause: () -> Unit, onDelete: () -> Unit) {
    Box {
        IconButton(onClick = { onExpand(true) }) {
            Icon(Icons.Default.MoreVert, null, tint = SoftWhite.copy(alpha = 0.3f))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { onExpand(false) }, modifier = Modifier.background(CardGray)) {
            DropdownMenuItem(
                text = { Text("Editar", color = SoftWhite) }, 
                onClick = { onExpand(false); onEdit() },
                leadingIcon = { Icon(Icons.Default.Edit, null, tint = BrandBlue) }
            )
            DropdownMenuItem(
                text = { Text(if (habit.isFavorite) "Quitar favorito" else "Favorito", color = SoftWhite) },
                onClick = { onExpand(false); onFavorite() },
                leadingIcon = { Icon(if (habit.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null, tint = BrandGreen) }
            )
            DropdownMenuItem(
                text = { Text(if (habit.isPaused) "Reanudar" else "Pausar", color = SoftWhite) },
                onClick = { onExpand(false); onPause() },
                leadingIcon = { Icon(if (habit.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause, null, tint = BrandCyan) }
            )
            HorizontalDivider(color = SoftWhite.copy(alpha = 0.1f))
            DropdownMenuItem(
                text = { Text("Eliminar", color = SoftRed) }, 
                onClick = { onExpand(false); onDelete() },
                leadingIcon = { Icon(Icons.Default.Delete, null, tint = SoftRed) }
            )
        }
    }
}

@Composable
private fun HabitCheckbox(isCompleted: Boolean) {
    Box(
        modifier = Modifier.size(28.dp).clip(CircleShape)
            .background(if (isCompleted) BrandGreen else Color.Transparent)
            .border(2.dp, if (isCompleted) BrandGreen else SoftWhite.copy(alpha = 0.2f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) Text("✓", fontWeight = FontWeight.Black, color = BrandDark, fontSize = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    MyHabitsTheme { DashboardScreen() }
}
