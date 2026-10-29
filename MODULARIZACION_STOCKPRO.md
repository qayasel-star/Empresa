# Modularización de StockPro Premium

La aplicación está organizada en paquetes funcionales dentro del módulo Gradle `:app`:

- `data/`: entidades Room, DAO, base de datos, reglas de stock y generador SKU.
- `ui/inventory/`: inicio, catálogo, formulario de producto, categorías y ajustes de stock.
- `ui/commerce/`: ventas, compras, contactos e informes.
- `ui/operations/`: alertas, configuración, respaldo, licencias y catálogo auxiliar.
- `ui/`: ViewModel y estado observable.
- `ui/theme/`: tema, colores y tipografía.

Siguiente paso arquitectónico recomendado: extraer `:core:model`, `:core:database`, `:feature:inventory`, `:feature:commerce` y `:feature:operations` después de estabilizar contratos y pruebas. No se declaran módulos Gradle independientes en esta entrega.
