# Plan de Mejora: Filtros de Hábitos en el Dashboard

Este plan detalla la implementación de chips de filtrado para organizar la lista de hábitos por estado (Todos, Favoritos, Pendientes, Completados).

## Cambios Propuestos

### Lógica de Negocio (ViewModel)

#### [DashboardViewModel.kt](file:///C:/Users/itsDavlix/Documents/MyHabits/MyHabits/app/src/main/java/com/example/myhabits/ui/dashboard/DashboardViewModel.kt)
- Añadir un enum `HabitFilter` con los valores: `ALL`, `FAVORITES`, `PENDING`, `COMPLETED`.
- Añadir un estado `_currentFilter: MutableStateFlow<HabitFilter>`.
- Modificar `habitsForSelectedDate` para que combine el flujo de hábitos, la fecha seleccionada y el filtro actual.

### Interfaz de Usuario (Compose)

#### [DashboardScreen.kt](file:///C:/Users/itsDavlix/Documents/MyHabits/MyHabits/app/src/main/java/com/example/myhabits/ui/dashboard/DashboardScreen.kt)
- Crear un componente `FilterChipsSection` que muestre los chips de filtrado.
- Los chips tendrán el estilo visual de la app (uso de `EnergyLime` para el estado seleccionado).
- La sección se ubicará justo debajo del título "HÁBITOS DEL DÍA" y arriba de la lista.

---

## Plan de Verificación

### Verificación Manual
1. Abrir la aplicación.
2. Hacer clic en el chip **Favoritos** y verificar que solo aparecen los hábitos marcados con el corazón.
3. Hacer clic en **Pendientes** y verificar que solo aparecen los que aún no han sido completados en el día seleccionado.
4. Hacer clic en **Completados** y verificar que solo aparecen los ya realizados.
5. Volver a **Todos** para confirmar que la lista completa se restaura.
6. Cambiar de día y verificar que los filtros siguen funcionando correctamente para la nueva fecha.
