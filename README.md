# StockPro Premium — proyecto Android

Aplicación nativa Android escrita en Kotlin y Jetpack Compose, con interfaz oscura verde esmeralda/lima, almacenamiento local Room, inventario, movimientos de venta y compra, clientes/proveedores, alertas, informes, catálogo compartible, copias de seguridad y configuración.

## Identidad visual
- Icono de launcher incluido en densidades Android y en formato adaptive icon para Android 8.0+.
- Nombre que aparece instalado: **StockPro Premium**.
- Pantalla de bienvenida Compose con animación de pulso, brillo y transición hacia la aplicación.

## Compilar APK en GitHub desde el teléfono
1. Descarga y descomprime este ZIP.
2. Crea un repositorio GitHub vacío.
3. Sube **el contenido** de esta carpeta al nivel raíz del repositorio (deben quedar `app/`, `build.gradle.kts`, `settings.gradle.kts` y `.github/workflows/android.yml`).
4. Abre la pestaña **Actions** y ejecuta `Compilar StockPro Premium APK` si no comienza automáticamente.
5. Cuando el trabajo termine correctamente, abre la ejecución y descarga el artefacto `StockPro-Premium-debug-apk`. Dentro estará `app-debug.apk`.

## Estado de verificación
Este paquete se ha preparado como código fuente; **no se afirma que esté compilado ni probado en un dispositivo**. La compilación de referencia debe ejecutarse en GitHub Actions. Si falla, abre el registro del paso de compilación y utiliza el primer error Kotlin/Gradle para corregirlo.

## Límites conocidos
La sincronización en nube y la licencia comercial/servidor no se activan por el mero hecho de tener pantallas visuales. Las copias de seguridad son locales/exportables; no sustituyen una infraestructura remota. Revisa los datos de prueba antes de usar la app para operaciones comerciales reales.
