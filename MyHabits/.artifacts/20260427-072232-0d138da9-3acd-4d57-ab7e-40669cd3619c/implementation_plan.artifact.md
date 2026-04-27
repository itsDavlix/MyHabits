# Plan de Mejora en Seguimiento de Hábitos

Este plan detalla las mejoras para permitir la visualización de hábitos completados por día de la semana y la gestión de hábitos según su frecuencia.

## Cambios Propuestos

### Modelos de Datos

#### [Habit.kt](file:///C:/Users/itsDavlix/Documents/MyHabits/MyHabits/app/src/main/java/com/example/myhabits/data/Habit.kt)
- Añadir función `isActiveOn(date: LocalDate): Boolean` para verificar si un hábito debe realizarse en una fecha específica según su frecuencia.
- Añadir función `isCompletedOn(date: LocalDate): Boolean` para verificar si se completó en esa fecha.
- Actualizar `isCompletedToday` para usar `isCompletedOn(LocalDate.now())`.

---

### Lógica de Negocio (ViewModels)

#### [DashboardViewModel.kt](file:///C:/Users/itsDavlix/Documents/MyHabits/MyHabits/app/src/main/java/com/example/myhabits/ui/dashboard/DashboardViewModel.kt)
- Añadir `selectedDate: MutableStateFlow<LocalDate>` para rastrear el día seleccionado en la UI.
- Exponer `habitsForSelectedDate: StateFlow<List<Habit>>` que filtra los hábitos según `isActiveOn(selectedDate)`.
- Modificar `toggleHabit(habitId: Int)` para que use `selectedDate.value`.
- Añadir función `setSelectedDate(date: LocalDate)`.

---

### Interfaz de Usuario (Compose)

#### [DashboardScreen.kt](file:///C:/Users/itsDavlix/Documents/MyHabits/MyHabits/app/src/main/java/com/example/myhabits/ui/dashboard/DashboardScreen.kt)
- **WeeklyStatsSection**:
    - Hacer que los días sean clicables.
    - Resaltar el día seleccionado.
    - Mostrar las fechas reales de la semana actual.
- **Lista de Hábitos**:
    - Mostrar los hábitos correspondientes a `selectedDate`.
    - Actualizar el título de la sección según el día seleccionado (ej: "HÁBITOS DEL MARTES" o "HÁBITOS DE HOY").
- **HealthProgressCard**:
    - Calcular el progreso basándose en los hábitos activos del `selectedDate`.

#### [HabitDialog.kt](file:///C:/Users/itsDavlix/Documents/MyHabits/MyHabits/app/src/main/java/com/example/myhabits/ui/dashboard/HabitDialog.kt)
- **Selector de Categoría**:
    - Reemplazar el campo de texto de categoría con un `ExposedDropdownMenu`.
    - Incluir categorías predefinidas: "Poder", "Cardio", "Nutrición", "Flex", "Recuperación", "Descanso".
    - Añadir opción "Otro..." que habilita un campo de texto para escribir una categoría personalizada.
- **Selector de Recordatorio**:
    - Configurar el `TimePicker` para que use el formato de **24 horas** durante la selección.
    - Traducir la hora seleccionada a formato de **12 horas (AM/PM)** para la visualización en el botón del diálogo y en la lista de hábitos.

---

## Plan de Verificación

### Verificación Manual
1. Abrir la aplicación y verificar que se muestran los hábitos de hoy por defecto.
2. Hacer clic en diferentes días de la semana en la sección de estadísticas semanales.
3. Verificar que la lista de hábitos cambie para mostrar solo los hábitos activos en ese día (basado en su frecuencia).
4. Completar un hábito en un día pasado y verificar que se guarde correctamente (la barra de progreso de ese día debe subir).
5. Crear un nuevo hábito con frecuencia "Días específicos" (ej: Lunes y Miércoles) y verificar que solo aparezca al seleccionar esos días.

### Pruebas Automatizadas
- Ejecutar `StatsCalculationTest.kt` para asegurar que no hay regresiones en el cálculo de rachas.
- Añadir una prueba unitaria para la nueva lógica de `isActiveOn` en `Habit`.
