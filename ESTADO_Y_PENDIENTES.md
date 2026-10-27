# Estado y pendientes

- Proyecto Android nativo Kotlin + Jetpack Compose.
- Room para datos locales de productos y movimientos.
- Interfaz para panel, inventario, ventas, compras, contactos, alertas, informes, catálogo, copias de seguridad, licencia y ajustes.
- Icono Android de StockPro Premium incluido en mipmap (densidades) y adaptive icon.
- Bienvenida animada implementada dentro de Compose.
- GitHub Actions para compilar `assembleDebug` y publicar el APK.

## Pendiente de verificación
- Ejecutar GitHub Actions y resolver cualquier error de compilación.
- Instalar APK resultante en teléfono físico y comprobar icono/recorte del launcher.
- Probar flujos de alta/edición, ventas, compras, exportación/importación y persistencia.
- La sincronización real en la nube y la validación de licencia requieren backend; las pantallas no significan que esos servicios estén conectados.


## Estado de diseño tras la actualización visual
- Se aplicó una paleta oscura más profunda, acentos verde lima y superficies degradadas en la bienvenida del panel, métricas, encabezados de módulos y filas de inventario.
- Se mantienen las funciones existentes de navegación, inventario, movimientos, ventas, compras, contactos, alertas, informes, copias de seguridad y configuración.
- La apariencia final debe validarse en el dispositivo Android del usuario; la referencia es una maqueta de alta fidelidad y pueden requerirse iteraciones adicionales para igualar detalles, iconografía y espaciados.
- La compilación de esta actualización está pendiente de ejecutar en GitHub Actions.
