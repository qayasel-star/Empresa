# Auditoría de StockPro Premium

## Hallazgos visuales basados en las capturas del dispositivo
- La identidad general oscura/esmeralda existe, pero los cuadros de diálogo muestran superficies moradas/grisáceas que no corresponden a la marca.
- En Ventas y Compras, los estados sin productos ocupan demasiado espacio y hacen que los formularios parezcan inactivos; debe probarse con inventario real y con inventario vacío.
- La barra inferior y los formularios necesitan verificaciones en distintos tamaños de pantalla y con barras del sistema activas.
- La bienvenida Compose ya contiene animación vectorial, órbitas, partículas, cuadrícula de perspectiva, haz de luz y progreso. Su apariencia/fluidez debe confirmarse en dispositivos físicos.
- Los recursos del launcher incluyen iconos PNG por densidad y un icono adaptativo vectorial con cubo, órbitas y señal de crecimiento.

## Cambios de esta revisión
- Se fijó explícitamente el color de superficie, título y texto de los AlertDialog para evitar que hereden tonos de superficie ajenos a la paleta StockPro.
- Los chips de categoría del formulario de producto ahora usan contenedores y estados seleccionados de la paleta esmeralda/cian.
- Las barras de estado y navegación de la configuración Android se alinearon con el fondo oscuro de la aplicación y el acento esmeralda.
- SKU: el generador está separado en `data/ProductSku.kt`, crea referencias legibles con prefijo `SP-`, fecha/hora y sufijo alfanumérico; la normalización se aplica al guardar.

## Arquitectura actual
La aplicación sigue siendo un solo módulo Gradle `:app`, organizado por paquetes funcionales (`data`, `ui/inventory`, `ui/commerce`, `ui/operations`, `ui/theme`). No son módulos Gradle independientes.

## Riesgos y validaciones pendientes
- No se ha ejecutado una compilación Android completa de este ZIP ni pruebas instrumentadas en dispositivo en este entorno.
- La unicidad de SKU se verifica desde la lógica de guardado, pero conviene reforzarla con índice único en Room y gestionar conflictos de inserción de forma transaccional.
- Falta una revisión funcional completa de ventas, compras, contactos, alertas, informes y copias de seguridad con datos reales.
- La bienvenida se muestra desde Compose al abrir la actividad; no se ha validado una transición desde un splash nativo de Android 12+.
- La integración del icono está presente en recursos y manifiesto; no se ha inspeccionado en launchers de distintos fabricantes.

## Próximos pasos de aceptación
1. Ejecutar `gradle assembleDebug` en GitHub Actions y corregir cualquier error real del log.
2. Instalar la APK compilada y revisar bienvenida, launcher, diálogos, teclado, navegación y orientación.
3. Probar alta/edición/duplicación de SKU, ventas que descuentan stock y compras que lo incrementan.
4. Añadir pruebas de UI y una restricción única de SKU con migración de base de datos si corresponde.
