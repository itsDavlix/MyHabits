<p align="center">
  <img src="./MyHabits%20Logo.jpeg" alt="MyHabits Logo" width="220"/>
</p>

# 🌱 MyHabits - Seguimiento personal de hábitos saludables

Aplicación móvil Android para crear, organizar y dar seguimiento a hábitos personales mediante progreso diario, recordatorios, estadísticas básicas y una interfaz moderna enfocada en bienestar, energía y constancia.

Desarrollada en **Kotlin**, **Jetpack Compose** y **Material 3**, usando **Android Studio** como entorno principal de desarrollo.

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-Android-7F52FF" alt="Kotlin Android"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4" alt="Jetpack Compose"/>
  <img src="https://img.shields.io/badge/Material%203-Dise%C3%B1o-00C853" alt="Material 3"/>
  <img src="https://img.shields.io/badge/WorkManager-Recordatorios-009688" alt="WorkManager"/>
  <img src="https://img.shields.io/badge/API%20min-26-blue" alt="API mínima 26"/>
  <img src="https://img.shields.io/badge/Estado-Prototipo%20funcional-success" alt="Estado Prototipo Funcional"/>
  <img src="https://img.shields.io/badge/Proyecto-Presentaci%C3%B3n%20Acad%C3%A9mica-00bcd4" alt="Proyecto de Presentación Académica"/>
</p>

---

## 📝 Sobre el Proyecto

**MyHabits** nace como una solución sencilla para ayudar al usuario a organizar sus hábitos diarios y visualizar su progreso de forma clara, motivadora y fácil de usar.

El proyecto está pensado como una **aplicación de presentación**, ideal para demostrar conocimientos de desarrollo móvil Android sin convertirse en un sistema complejo o de producción. Su enfoque principal es mostrar una experiencia funcional para un usuario, con navegación fluida, formularios, recordatorios, estadísticas y una identidad visual atractiva.

La app permite:

- Registrar e iniciar sesión con un usuario.
- Crear hábitos personalizados con categoría, meta, color, ícono y frecuencia.
- Marcar hábitos como completados según el día seleccionado.
- Consultar progreso diario y estadísticas semanales.
- Configurar recordatorios locales mediante notificaciones.
- Editar información básica del perfil.

> **Importante:** MyHabits es un prototipo funcional. No está diseñado para manejar usuarios reales en producción, sincronización en la nube ni almacenamiento permanente avanzado.

---

## ✨ Funcionalidades Clave

### 1. Autenticación básica

- Registro de usuario con:
  - Nombre.
  - Correo electrónico.
  - Contraseña.
- Inicio de sesión con correo y contraseña.
- Validación de campos obligatorios.
- Validación de formato de correo.
- Mensajes de error para datos incorrectos o duplicados.
- Sesión activa durante el uso de la aplicación.

La autenticación se maneja como una base simulada para fines de demostración, suficiente para presentar el flujo completo de entrada a la app.

### 2. Gestión de hábitos

- Creación de nuevos hábitos.
- Edición de hábitos existentes.
- Eliminación de hábitos.
- Pausa y reactivación de hábitos.
- Marcado y desmarcado de hábitos completados.
- Selección de hábitos favoritos para priorizarlos visualmente.
- Personalización de cada hábito mediante:
  - Nombre.
  - Meta u objetivo.
  - Categoría.
  - Color.
  - Ícono.
  - Frecuencia.
  - Recordatorio opcional.

### 3. Frecuencias de seguimiento

Cada hábito puede configurarse con diferentes tipos de frecuencia:

- **Diaria:** el hábito aparece todos los días.
- **Semanal:** el hábito se trabaja de forma recurrente por semana.
- **Días específicos:** el usuario selecciona qué días desea cumplirlo.

Esto permite adaptar la app a rutinas simples como ejercicio, hidratación, lectura, descanso o nutrición.

### 4. Panel principal

La pantalla principal centraliza la experiencia del usuario:

- Saludo personalizado con el nombre del usuario.
- Vista de hábitos del día seleccionado.
- Selector de fechas dentro de la semana.
- Barra de progreso diario.
- Tarjetas visuales para cada hábito.
- Actividad semanal resumida.
- Orden visual que prioriza hábitos favoritos.

El panel está diseñado para que el usuario entienda rápidamente qué debe completar y cuánto ha avanzado durante el día.

### 5. Recordatorios locales

- Configuración opcional de hora para cada hábito.
- Selector de hora en formato de 12 horas con AM/PM.
- Programación de recordatorios con **WorkManager**.
- Notificaciones locales para recordar hábitos pendientes.
- Cancelación de recordatorios cuando un hábito se elimina o se pausa.

Esta funcionalidad aporta valor práctico sin requerir backend ni servicios externos.

### 6. Estadísticas

La app incluye una sección de estadísticas para visualizar el rendimiento del usuario:

- Porcentaje de cumplimiento semanal.
- Racha actual.
- Mejor racha registrada.
- Total de hábitos completados.
- Categoría con más actividad.
- Gráfico simple de actividad semanal.
- Mensajes motivacionales según el avance.

Estas métricas son sencillas, pero suficientes para una presentación visual y funcional del proyecto.

### 7. Perfil de usuario

- Visualización del nombre y correo del usuario.
- Edición de datos personales:
  - Nombre.
  - Correo.
  - Contraseña.
- Actualización del saludo en el panel principal al modificar el nombre.
- Opción de cerrar sesión.

---

## 🔄 Flujo básico de uso

1. El usuario crea una cuenta desde la pantalla de registro.
2. Inicia sesión con su correo y contraseña.
3. Ingresa al panel principal de hábitos.
4. Crea hábitos indicando categoría, meta, color, ícono, frecuencia y recordatorio opcional.
5. Marca hábitos como completados durante el día.
6. Consulta el progreso diario y las estadísticas semanales.
7. Edita su perfil o cierra sesión cuando lo necesite.

---

## 🏗️ Arquitectura y Recursos

### Arquitectura general

MyHabits sigue una estructura simple basada en capas, adecuada para un proyecto académico de Android:

1. **Capa de Presentación (UI)**  
   - Construida con **Jetpack Compose**.
   - Pantallas para login, registro, dashboard, estadísticas y perfil.
   - Componentes visuales modernos con **Material 3**.

2. **Capa de Estado y Lógica de Interfaz**  
   - Uso de **ViewModel** para manejar la lógica de las pantallas.
   - Uso de **StateFlow** para actualizar la interfaz de forma reactiva.
   - Cálculo de progreso, rachas y estadísticas.

3. **Capa de Datos Simulada**  
   - Modelos para usuarios, hábitos y completados.
   - Repositorios simples para manejar la información durante la ejecución.
   - Base de usuarios simulada para el flujo de autenticación.

4. **Capa de Recordatorios**  
   - Uso de **WorkManager** para programar tareas.
   - Worker encargado de disparar recordatorios.
   - Helper de notificaciones para mostrar avisos locales.

### Recursos principales del proyecto

- **Lenguaje:** Kotlin.
- **Plataforma:** Android.
- **UI:** Jetpack Compose.
- **Diseño:** Material 3.
- **Navegación:** Navigation Compose.
- **Estado:** ViewModel, StateFlow y Flow.
- **Tareas en segundo plano:** WorkManager.
- **Build system:** Gradle Kotlin DSL.
- **Pruebas:** JUnit para cálculos básicos de estadísticas.
- **IDE recomendado:** Android Studio.

---

## 👥 Actores del Sistema

| Actor | Descripción | Permisos principales |
|------|-------------|----------------------|
| **Usuario principal** | Persona que utiliza la app para organizar sus hábitos diarios. | Registrarse, iniciar sesión, crear hábitos, completar hábitos, revisar estadísticas y editar su perfil. |
| **Presentador / Desarrollador** | Encargado de mostrar el funcionamiento del proyecto y explicar sus módulos. | Ejecutar la app, demostrar pantallas, revisar código, compilar el proyecto y explicar mejoras futuras. |

> El sistema está pensado para un único usuario en contexto de demostración, por lo que no maneja roles administrativos ni permisos avanzados.

---

## 🗂️ Estructura del Proyecto

```text
MyHabits/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   └── java/com/example/myhabits/
│   │       ├── MainActivity.kt
│   │       ├── data/
│   │       │   ├── Habit.kt
│   │       │   ├── HabitCompletion.kt
│   │       │   ├── HabitRepository.kt
│   │       │   ├── SessionManager.kt
│   │       │   ├── User.kt
│   │       │   └── UserDatabase.kt
│   │       ├── ui/dashboard/
│   │       │   ├── DashboardScreen.kt
│   │       │   ├── DashboardViewModel.kt
│   │       │   ├── HabitDialog.kt
│   │       │   ├── MainHubScreen.kt
│   │       │   ├── NotificationHelper.kt
│   │       │   ├── ReminderManager.kt
│   │       │   ├── ReminderWorker.kt
│   │       │   ├── StatsScreen.kt
│   │       │   └── StatsViewModel.kt
│   │       ├── ui/login/
│   │       │   ├── LoginScreen.kt
│   │       │   ├── LoginViewModel.kt
│   │       │   ├── RegistrationScreen.kt
│   │       │   └── RegistrationViewModel.kt
│   │       ├── ui/navigation/
│   │       │   └── NavRoutes.kt
│   │       └── ui/theme/
│   │           ├── Color.kt
│   │           ├── Theme.kt
│   │           └── Type.kt
│   ├── src/test/java/com/example/myhabits/
│   │   └── StatsCalculationTest.kt
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
├── MyHabits Logo.jpeg
└── README.md
```

---

## 🚀 Instalación y Ejecución

### Requisitos

- Android Studio instalado.
- JDK 11 o compatible.
- Android SDK configurado.
- Emulador Android o dispositivo físico.
- API mínima: **26**.
- Target SDK: **36**.

### Pasos

1. Clona o descarga este repositorio.

   ```bash
   git clone <URL_DEL_REPOSITORIO>
   cd MyHabits
   ```

2. Abre el proyecto en **Android Studio**.

3. Sincroniza Gradle.

4. Ejecuta la aplicación en un emulador o dispositivo Android.

También puedes compilar el APK debug desde terminal:

```bash
./gradlew assembleDebug
```

El APK generado se ubicará normalmente en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔔 Permisos

La aplicación solicita permiso para mostrar notificaciones:

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

Este permiso permite mostrar recordatorios locales relacionados con los hábitos configurados por el usuario.

---

## 🧪 Pruebas

El proyecto incluye pruebas unitarias básicas para validar cálculos de estadísticas y rachas:

```text
app/src/test/java/com/example/myhabits/StatsCalculationTest.kt
```

Para ejecutar las pruebas desde terminal:

```bash
./gradlew test
```

---

## 🎨 Diseño Visual

MyHabits utiliza una estética moderna, deportiva y motivadora, alineada con temas de salud y productividad.

Elementos principales del diseño:

- Fondo oscuro para resaltar el contenido.
- Acentos verde lima y azul/cian.
- Tarjetas con bordes redondeados.
- Botones llamativos y fáciles de identificar.
- Íconos y emojis para representar hábitos.
- Mensajes motivacionales.
- Navegación inferior simple entre Inicio, Estadísticas y Perfil.

El logo debe ubicarse en la raíz del proyecto con el nombre:

```text
MyHabits Logo.jpeg
```

Y se muestra en este README con:

```html
<img src="./MyHabits%20Logo.jpeg" alt="MyHabits Logo" width="220"/>
```

---

## ⚠️ Alcance y Limitaciones

Este proyecto está diseñado como una **demostración funcional**, no como una aplicación lista para producción.

Limitaciones actuales:

- Los datos se manejan de forma local/simulada para fines de presentación.
- No cuenta con backend ni sincronización en la nube.
- No está preparado para múltiples usuarios reales.
- No incluye recuperación de contraseña.
- No implementa cifrado avanzado de credenciales.
- La persistencia puede ser limitada según la implementación local del prototipo.
- Las estadísticas son básicas y están pensadas para demostración visual.

---

## 🛠️ Próximas Mejoras Sugeridas

Como el objetivo del proyecto es una presentación, las mejoras recomendadas se mantienen simples y realistas:

- Guardar usuarios y hábitos de forma permanente con Room.
- Guardar la sesión activa usando DataStore Preferences.
- Añadir confirmación antes de eliminar un hábito.
- Crear pantalla de configuración de notificaciones.
- Mejorar gráficos con vista mensual o anual.
- Agregar modo claro/oscuro configurable.
- Reemplazar emojis por íconos vectoriales.
- Añadir recuperación de contraseña simulada.
- Agregar validaciones más robustas para contraseñas.
- Incorporar más pruebas unitarias para ViewModels y repositorios.

---

## 🧑‍💻 Equipo de Desarrollo

| Nombre Completo | Rol |
|-----------------|-----|
| David Alejandro Espinoza Largaespada | Coordinador y desarrollador |

Proyecto desarrollado como aplicación Android de seguimiento de hábitos personales, orientado a una presentación académica y demostración funcional.

---

## 📄 Licencia

Licencia no especificada.

Agrega una licencia antes de publicar el proyecto si deseas compartirlo como software abierto.
