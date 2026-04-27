# Mejora de la Pantalla de Perfil

Se mejorará la pantalla de perfil para incluir estadísticas rápidas del usuario y un sistema de niveles basado en la disciplina, haciendo que la aplicación se sienta más gratificante y profesional.

## Cambios Propuestos

### [Dashboard Component]

#### [MainHubScreen.kt](file:///C:/Users/itsDavlix/Documents/MyHabits/MyHabits/app/src/main/java/com/example/myhabits/ui/dashboard/MainHubScreen.kt)

- Se modificará `ProfileScreen` para recibir `DashboardViewModel` y `StatsViewModel` como parámetros.
- Se implementará una nueva interfaz para el perfil que incluya:
    - Encabezado con nombre y nivel de disciplina (Novato, Constante, Guerrero, Pro, Maestro).
    - Tarjetas de estadísticas rápidas (Racha actual, Hábitos completados, Favoritos).
    - El diseño existente de edición de perfil se mantendrá pero integrado en este nuevo flujo.

#### [DashboardViewModel.kt](file:///C:/Users/itsDavlix/Documents/MyHabits/MyHabits/app/src/main/java/com/example/myhabits/ui/dashboard/DashboardViewModel.kt)

- Se asegurará de que las estadísticas necesarias estén disponibles a través de los ViewModels existentes.

---

## Plan de Verificación

### Verificación Manual
- Se navegará a la pestaña de perfil para confirmar que se muestran las nuevas estadísticas.
- Se verificará que el nivel cambia según el número de hábitos completados (lógica simulada basada en `totalCompletions`).
- Se comprobará que el botón "Editar Perfil" sigue funcionando correctamente.
