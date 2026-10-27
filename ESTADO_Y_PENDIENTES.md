# Estado real de StockPro Premium

## Incluido en este paquete
- Proyecto Android nativo Kotlin + Jetpack Compose y tema oscuro verde esmeralda/lima.
- Pantalla de bienvenida animada e iconos de aplicación.
- Navegación y pantallas de inicio, inventario, ventas, compras, contactos, alertas, informes, catálogo, exportación local, licencia y ajustes.
- Persistencia local Room para productos/categorías/movimientos y validaciones básicas de productos y ajustes de stock.
- Historial comercial local para operaciones que ya estén conectadas en la interfaz.
- Workflow de GitHub Actions para intentar generar un APK de depuración.

## No afirmar como terminado
- Este proyecto no ha sido compilado ni probado en dispositivo durante esta entrega.
- Las pantallas de proveedores/clientes, informes, ajustes y licencia deben auditarse funcionalmente; una pantalla visible no implica que el flujo completo esté implementado.
- Ventas y compras aún no tienen un flujo comercial integral transaccional con cabecera, líneas, pagos, instantáneas de precios y movimientos atómicos.
- No está implementada una restauración integral de copia de seguridad desde Room.
- No hay sincronización real en la nube ni servidor de licencias.
- No se incluye lectura de códigos de barras; no añadirla como función implementada.
- No se ha comprobado accesibilidad, migración de bases de datos, seguridad ni funcionamiento en un teléfono físico.
- No se incluye Gradle Wrapper (`gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`). El workflow instala Gradle en GitHub Actions.

## Siguiente verificación obligatoria
1. Ejecutar `.github/workflows/compilar-apk.yml` en GitHub Actions.
2. Corregir todos los errores de compilación.
3. Probar alta/edición de productos, entradas/salidas, ventas, compras, exportación y persistencia.
4. Revisar la interfaz de cada pantalla contra las referencias visuales.

Este archivo distingue el código existente de las funciones pendientes. No usar este paquete como garantía de que todos los módulos están finalizados o de que ya existe un APK compilado.

## Revisión visual — 27/10/2026
- Se refinó la identidad visual base hacia negro profundo y verde neón, con contraste más consistente.
- Se rediseñó la bienvenida con brillo animado, marca StockPro Premium, barra de carga y jerarquía tipográfica.
- Se actualizaron las tarjetas métricas y de informes con superficies oscuras, acentos por color y bordes discretos.
- Se reforzaron encabezados de módulos, contornos de tarjetas y la barra inferior.
- Esta revisión visual no sustituye una auditoría funcional ni demuestra por sí sola que el proyecto compile; debe ejecutarse `gradle assembleDebug` en CI y revisarse en un teléfono real.


## Refinamiento visual premium — 27/10/2026 (segunda pasada)
- Unifiqué los tokens de color en negro grafito, verde esmeralda, menta/cian y acentos semánticos; se redujo el uso de contornos verdes intensos en todas las tarjetas.
- Apliqué fondos atmosféricos oscuros a las pantallas principales y superficies con degradados más discretos.
- Reforcé la jerarquía de títulos con el overline de marca StockPro Premium en encabezados compartidos, Inventario y Contactos.
- Rediseñé las tarjetas de resumen/acción para usar degradados controlados, bordes de bajo contraste y contenedores de iconos coherentes.
- Ajusté la tipografía global y los colores de Material 3 para mejorar consistencia entre formularios, navegación y contenido.
- Se mantuvieron los flujos y modelos existentes; esta pasada es visual y no afirma completar las funciones de negocio pendientes.
- Pendiente imprescindible: compilar con GitHub Actions y revisar capturas reales en teléfono para corregir cualquier diferencia de densidad, recorte o contraste.


### Bienvenida cinematográfica StockPro (27-10-2026)
- Se renovó `WelcomeScreen` con fondo negro/verde, partículas de luz, horizonte digital en perspectiva, barrido luminoso, halos, dos órbitas animadas, pulso del emblema, brillo y barra de progreso animada.
- La bienvenida permanece aproximadamente 4,2 segundos antes de la transición a la aplicación.
- La animación está construida en Jetpack Compose y no depende de imágenes remotas ni de permisos de red.
- Pendiente: compilar mediante GitHub Actions e inspeccionar la animación en un dispositivo Android real; no se declara compilación verificada desde este entorno.
