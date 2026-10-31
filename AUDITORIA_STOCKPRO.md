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

## Integración de SKU por familia e imágenes de producto (2026-10-28)
- Se sustituye el SKU temporal por referencias legibles y secuenciales por familia: `ARR-001`, `ACE-001`, `AZU-001`, `REF-001`, además de familias comunes como leche, harina, pan, agua, café y limpieza.
- El generador busca los números existentes del mismo prefijo y propone el siguiente hueco disponible. El SKU se recalcula al escribir el nombre mientras el campo continúe en modo automático; el usuario puede editarlo manualmente o pulsar AUTO.
- Se incorpora `ProductImageLibrary`, con 21 recursos WebP locales incluidos en `res/drawable-nodpi`, sugerencia por nombre/categoría, selección manual y miniaturas en el inventario.
- Se agrega `imageKey` a `ProductEntity` y migración Room 2→3 para conservar los datos existentes sin borrarlos.
- Se añaden pruebas unitarias para los prefijos solicitados, secuencia, huecos y normalización.
- Pendiente: ejecutar Gradle/compilación Android y pruebas en dispositivo. La modificación de código y la integridad del ZIP no equivalen a una APK compilada y probada.

## Revisión visual adicional — 2026-10-28

- Se centralizó el sistema visual Material 3 en `StockProTheme.kt`: paleta oscura esmeralda/cian, contenedores, bordes, colores de error y jerarquía tipográfica con interlineado definido.
- Se definieron formas consistentes para controles y superficies (radios de 8, 12, 16, 22 y 28 dp), para evitar que cada componente parezca provenir de una aplicación distinta.
- Se armonizó la barra inferior y se desactivó la aplicación forzada de contraste de las barras del sistema en Android 10 o superior, conservando el fondo oscuro de marca.
- La pantalla Inventario usa ahora fondo degradado de la marca y chips con estados visuales explícitos (esmeralda para filtros activos y ámbar para stock bajo).
- El formulario de alta inicia con un SKU generado por la lógica de familias en vez de mostrar un `GEN-001` fijo; al introducir el nombre se actualiza si el modo automático sigue activo.

**Verificación:** cambios inspeccionados en el código fuente. El ZIP se valida con `unzip -t`. En este entorno no hay Gradle instalado y no se ha ejecutado una compilación Android completa ni prueba en dispositivo; esos pasos siguen pendientes.

## Actualización visual transversal — 2026-10-28
- Se incorporó `PremiumDesign.kt` con un fondo ambiental compartido (base carbón, halo cian/esmeralda y brillo inferior sutil) para evitar que cada módulo implemente una variante distinta del fondo.
- Se aplicó el fondo compartido a Inicio/Inventario/Más, Ventas/Compras/Contactos/Informes y Alertas/Configuración/Copias de seguridad/Licencia/Catálogo; también a la pantalla genérica de módulos pendientes.
- Se ajustó el color de contorno del tema Material 3 a un verde azulado más coherente con la identidad StockPro.
- La intención es mejorar coherencia entre módulos sin modificar sus flujos de datos ni operaciones comerciales.
- Verificación pendiente: compilación Android completa y comprobación visual en dispositivo. No se declara compilación exitosa.
