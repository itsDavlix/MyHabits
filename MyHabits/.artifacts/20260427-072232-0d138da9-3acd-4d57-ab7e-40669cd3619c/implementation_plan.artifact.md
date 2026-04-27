# Plan de Mejora: Perfil de Usuario y Modificación de Datos

Este plan detalla los cambios para permitir que los usuarios modifiquen su nombre, correo electrónico y contraseña desde la pantalla de perfil.

## Corrección del Reloj de Recordatorio

- **Cambio de Formato**: Cambiar el `TimePicker` de formato 24h a formato **12h** (`is24Hour = false`).
- **Selector AM/PM**: Al usar `is24Hour = false`, Material 3 añade automáticamente los botones de selección AM/PM al reloj, eliminando la confusión de los "otros números" (que eran las horas de la tarde en formato 24h).
- **Consistencia**: Mantener la visualización de la hora seleccionada en formato 12h con AM/PM.

## Perfil de Usuario y Modificación de Datos

#### [UserDatabase.kt](file:///C:/Users/itsDavlix/Documents/MyHabits/MyHabits/app/src/main/java/com/example/myhabits/data/UserDatabase.kt)
- Añadir función `updateUser(updatedUser: User)` para actualizar los datos del usuario en la base de datos simulada.

---

### Interfaz de Usuario (Compose)

#### [MainHubScreen.kt](file:///C:/Users/itsDavlix/Documents/MyHabits/MyHabits/app/src/main/java/com/example/myhabits/ui/dashboard/MainHubScreen.kt)
- Refactorizar `ProfileScreen` para incluir:
    - Un modo de visualización y un modo de edición.
    - Campos de texto (`OutlinedTextField`) para editar el nombre, email y contraseña.
    - Botones de "EDITAR PERFIL", "GUARDAR CAMBIOS" y "CANCELAR".
    - Lógica para actualizar tanto el `SessionManager` como el `UserDatabase` al guardar.
    - Estilos coherentes con el resto de la aplicación (uso de `EnergyLime`, `DarkSurface`, etc.).

---

## Plan de Verificación

### Verificación Manual
1. Iniciar sesión y navegar a la pestaña de **Perfil**.
2. Hacer clic en **EDITAR PERFIL** y verificar que los campos se vuelven editables.
3. Cambiar el nombre y el correo electrónico, luego hacer clic en **GUARDAR CAMBIOS**.
4. Verificar que el nombre se actualiza inmediatamente en el encabezado de la pantalla principal (Dashboard).
5. Cerrar sesión e intentar iniciar sesión con el nuevo correo electrónico para confirmar que los cambios persistieron en `UserDatabase`.
6. Verificar que el botón **CANCELAR** revierte los cambios pendientes y vuelve al modo de visualización.
