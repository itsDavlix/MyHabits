# Plan de Mejora: Mensajes Motivacionales Post-Hábito

Este plan detalla la implementación de una superposición estética y animada que muestra una frase motivacional cada vez que el usuario completa un hábito.

## Cambios Propuestos

### Interfaz de Usuario (Compose)

#### [DashboardScreen.kt](file:///C:/Users/itsDavlix/Documents/MyHabits/MyHabits/app/src/main/java/com/example/myhabits/ui/dashboard/DashboardScreen.kt)
- **Estado de Motivación**: Añadir estados `showMotivationalOverlay` y `currentMotivationalMessage`.
- **Nuevo Componente `MotivationalOverlay`**:
    - Una superposición a pantalla completa con fondo semi-transparente difuminado (glassmorphism).
    - Tipografía impactante en color `EnergyLime`.
    - Animaciones de entrada (fade-in + scale) y salida automática después de 2 segundos.
    - Frases aleatorias: "¡Buen trabajo!", "Otro paso más cerca de tu meta.", "Disciplina completada.", "Racha en progreso."
- **Integración**: Activar el overlay dentro de la lógica de `onToggle` en `DashboardScreen` solo cuando el hábito pasa a estado completado.

---

## Plan de Verificación

### Verificación Manual
1. Abrir la aplicación en el Dashboard.
2. Marcar un hábito como completado.
3. Verificar que aparece un mensaje motivacional a pantalla completa con una animación suave.
4. Confirmar que el mensaje desaparece solo después de un breve periodo (aprox. 2 segundos).
5. Completar otro hábito y verificar que el mensaje puede ser diferente (aleatorio).
6. Verificar que completar un hábito en un día pasado también active el mensaje.
