# Biblioteca de imágenes de productos StockPro

La biblioteca se integra en `app/src/main/res/drawable-nodpi/` y se indexa desde `com.stockpro.data.ProductImageLibrary`.

Incluye 21 recursos WebP para arroz, azúcar, harina, aceite, leche, pan, huevos, carne, pollo, pescado, frutas, verduras, refrescos, agua, galletas, café, té, limpieza, higiene, electrónica y ferretería. Las imágenes se empaquetan localmente; no requieren conexión a internet.

En el formulario de producto se sugiere una imagen usando el nombre y la categoría. El usuario puede seleccionar manualmente otra miniatura. La elección se persiste en `products.imageKey`, mediante migración Room 2→3, y aparece en la fila del inventario.

Los SKU siguen el patrón `PREFIJO-###`. Los cuatro casos pedidos se asignan como `ARR`, `ACE`, `AZU` y `REF`; se busca el siguiente número libre dentro de cada prefijo.
