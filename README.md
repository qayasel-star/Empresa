# StockPro Premium — proyecto Android

Aplicación Android nativa escrita en Kotlin y Jetpack Compose. Este es el proyecto continuo StockPro Premium del repositorio oficial `https://github.com/qayasel-star/Empresa`.

## Identidad visual
- Referencia principal: maqueta Premium compartida por el usuario, con negro/verde petróleo, verde lima neón, tarjetas redondeadas y navegación consistente.
- Icono de launcher incluido en densidades Android y formato adaptive icon.
- Nombre instalado: **StockPro Premium**.
- Pantalla de bienvenida animada y panel principal con tarjetas de métricas.

## Compilar APK desde un teléfono Android
1. Mantén el contenido del proyecto en la raíz del repositorio `Empresa` (deben quedar `app/`, `build.gradle.kts`, `settings.gradle.kts` y `.github/workflows/compilar-apk.yml`).
2. Abre la pestaña **Actions** de `https://github.com/qayasel-star/Empresa`.
3. Ejecuta **Compilar StockPro APK** o espera a que termine el flujo tras un commit.
4. En la ejecución correcta, descarga el artefacto `StockPro-Premium-APK`, que contiene el APK debug.

## Actualización visual
Se mejoraron el encabezado del panel, la bienvenida, el resumen de inventario, las tarjetas de métricas, las filas de productos, los encabezados de módulos y la paleta Material 3. Se conservan las acciones y rutas existentes. La apariencia final debe verificarse en el teléfono y pueden requerirse iteraciones visuales adicionales.

## Estado de verificación
El usuario confirmó que una versión anterior se compiló en GitHub Actions y se instaló. **Esta actualización visual concreta todavía necesita una nueva ejecución de GitHub Actions y una comprobación en el dispositivo**; no se afirma que este ZIP ya haya compilado.

## Límites conocidos
La sincronización en nube y la licencia comercial no se activan por el mero hecho de tener pantallas. Las copias de seguridad son locales/exportables y no sustituyen una infraestructura remota. Antes de usar la aplicación para operaciones comerciales reales, verifica los flujos de negocio y los datos almacenados.
