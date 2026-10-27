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
