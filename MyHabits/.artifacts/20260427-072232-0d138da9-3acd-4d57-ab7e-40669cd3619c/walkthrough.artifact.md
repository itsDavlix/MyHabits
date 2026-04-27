# Actualización: Filtros de Hábitos en el Dashboard

He añadido una nueva sección de filtros rápidos para que puedas organizar y encontrar tus hábitos mucho más fácilmente.

## Cambios Realizados

### 1. Chips de Filtrado
- **Cuatro Modos de Vista**: He implementado una barra de selección con cuatro filtros:
    - **Todos**: La vista completa de tus hábitos del día.
    - **Favoritos**: Muestra solo aquellos que has marcado como preferidos (con el corazón).
    - **Pendientes**: Filtra instantáneamente para ver qué te falta por completar hoy.
    - **Completados**: Una lista de tus victorias del día.
- **Estética Neón**: Los chips utilizan el color `EnergyLime` para resaltar la selección activa y `DarkSurface` para el resto, manteniendo la línea visual de la app.

### 2. Lógica Reactiva
- **Filtrado en Tiempo Real**: Al pulsar cualquier chip, la lista se actualiza al instante.
- **Persistencia de Fecha**: Los filtros funcionan en armonía con el selector de fecha; puedes ver los "Completados" de ayer o los "Pendientes" de mañana de forma sencilla.

## Verificación Realizada
- **Compilación Exitosa**: El proyecto compila correctamente con la nueva lógica de filtrado en el ViewModel.
- **Prueba de Uso**: He verificado que los filtros se aplican correctamente sobre la lista de hábitos activos del día seleccionado.
- **Despliegue**: La actualización ya está instalada en tu emulador.

¡Prueba a filtrar por "Pendientes" para ver lo que te queda por conquistar hoy!
