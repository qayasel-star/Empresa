package com.stockpro

import android.os.Bundle
import androidx.compose.foundation.horizontalScroll
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stockpro.data.ProductEntity
import com.stockpro.data.CategoryEntity
import com.stockpro.data.ProductSku
import com.stockpro.data.ProductImageLibrary
import com.stockpro.data.BusinessStore
import com.stockpro.ui.StockProViewModel
import com.stockpro.ui.theme.StockProTheme
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
internal fun Dashboard(modifier: Modifier, count: Int, low: Int, products: List<ProductEntity>, onInventory: () -> Unit, onAddProduct: () -> Unit) {
    val context = LocalContext.current
    val entries = remember(products) { BusinessStore.entries(context) }
    val salesEntries = entries.filter { it.type == "Venta" }
    val purchaseEntries = entries.filter { it.type == "Compra" }
    val totalSales = salesEntries.sumOf { it.amount }
    val totalPurchases = purchaseEntries.sumOf { it.amount }
    val today = LocalDate.now()
    fun isToday(timestamp: Long): Boolean = java.time.Instant.ofEpochMilli(timestamp).atZone(java.time.ZoneId.systemDefault()).toLocalDate() == today
    val salesToday = salesEntries.filter { isToday(it.timestamp) }.sumOf { it.amount }
    val purchasesToday = purchaseEntries.filter { isToday(it.timestamp) }.sumOf { it.amount }
    val inventoryValue = products.sumOf { it.stockQuantity * it.purchasePrice }
    val notificationsEnabled = BusinessStore.setting(context, "notifications", "true") == "true"
    val lowProducts = if (notificationsEnabled) products.filter { it.stockQuantity <= it.minimumStock }.take(3) else emptyList()
    Column(modifier.fillMaxSize().stockProBackdrop().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(top = 12.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(50.dp).background(Brush.linearGradient(listOf(Color(0xFF123E32), Color(0xFF0A2024), Color(0xFF102329))), RoundedCornerShape(16.dp)).border(1.dp, Mint.copy(alpha = .48f), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Inventory2, null, tint = Lime, modifier = Modifier.size(27.dp))
                Box(Modifier.align(Alignment.BottomEnd).padding(7.dp).size(5.dp).background(Mint, CircleShape))
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Stock", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                    Text("Pro", color = Lime, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                }
                Text("Tu negocio en buenas manos", color = Muted, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = { }) { Icon(Icons.Default.Notifications, "Notificaciones", tint = Lime) }
        }
        Spacer(Modifier.height(18.dp))
        Text("Hola, bienvenido", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, letterSpacing = (-.5).sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(6.dp).background(Lime, CircleShape))
            Spacer(Modifier.width(7.dp))
            Text("TU NEGOCIO, BAJO CONTROL  ·  RESUMEN GENERAL", color = Muted, style = MaterialTheme.typography.labelSmall, letterSpacing = .8.sp)
        }
        Spacer(Modifier.height(18.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(24.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Brush.linearGradient(listOf(Lime.copy(alpha = .62f), Mint.copy(alpha = .14f), Hairline))), modifier = Modifier.fillMaxWidth().clickable(onClick = onInventory)) {
            Row(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF12563F), Color(0xFF0C2A28), Color(0xFF09151A)))).padding(19.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("VALOR DEL INVENTARIO", color = Lime, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(money(inventoryValue), color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
                    Text("VALORADO AL PRECIO DE COMPRA  ·  $count productos", color = Color(0xFFC5E5CF), style = MaterialTheme.typography.bodySmall)
                }
                Box(Modifier.size(58.dp).background(Lime.copy(alpha = .12f), RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Inventory2, null, tint = Lime, modifier = Modifier.size(34.dp)) }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            DashboardMetric("Productos", count.toString(), Icons.Default.ShoppingBag, Mint, Modifier.weight(1f), onInventory)
            DashboardMetric("Stock bajo", low.toString(), Icons.Default.Warning, Color(0xFFFFC477), Modifier.weight(1f), onInventory)
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            DashboardMetric("Ventas hoy", money(salesToday), Icons.Default.ShoppingCart, Blue, Modifier.weight(1f), {})
            DashboardMetric("Compras hoy", money(purchasesToday), Icons.Default.ShoppingBag, Orange, Modifier.weight(1f), {})
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            DashboardMetric("Ganancia neta acumulada", money(totalSales - totalPurchases), Icons.Default.TrendingUp, Lime, Modifier.weight(1f), {})
            DashboardMetric("Operaciones", entries.size.toString(), Icons.Default.ReceiptLong, Mint, Modifier.weight(1f), {})
        }
        Spacer(Modifier.height(18.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Hairline.copy(alpha = .9f)), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Productos bajos en stock", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    TextButton(onClick = onInventory) { Text("Ver todos", color = Lime) }
                }
                if (lowProducts.isEmpty()) {
                    Text(if (notificationsEnabled) "No hay productos por debajo del mínimo." else "Los avisos de stock en el inicio están desactivados en Configuración.", color = Muted, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 8.dp))
                } else {
                    lowProducts.forEach { product ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(40.dp).background(Color(0xFF173B35), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Inventory2, null, tint = Lime) }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(product.name, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("${product.stockQuantity} ${product.unit} disponibles", color = if (product.stockQuantity == 0) Color(0xFFFF6B6B) else Orange, style = MaterialTheme.typography.bodySmall)
                            }
                            Icon(Icons.Default.Warning, null, tint = if (product.stockQuantity == 0) Color(0xFFFF6B6B) else Orange)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Hairline.copy(alpha = .9f)), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("Actividad reciente", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Text("Últimas operaciones registradas · verde ventas / ámbar compras", color = Muted, style = MaterialTheme.typography.bodySmall) }; Icon(Icons.Default.BarChart, null, tint = Lime) }
                val chart = entries.take(7).reversed()
                if (chart.isEmpty()) { Spacer(Modifier.height(14.dp)); Text("Registra ventas y compras para ver aquí tu actividad.", color = Muted, style = MaterialTheme.typography.bodySmall) }
                else Row(Modifier.fillMaxWidth().height(112.dp).padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) { chart.forEach { e -> val barHeight = (18 + (e.amount.coerceAtMost(500.0) / 500.0 * 66).toInt()).dp; Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) { Box(Modifier.width(18.dp).height(barHeight).background(if (e.type == "Venta") Lime else Orange, RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))); Spacer(Modifier.height(4.dp)); Text(if (e.type == "Venta") "V" else "C", color = Muted, style = MaterialTheme.typography.labelSmall) } } }
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onInventory, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Lime)) { Icon(Icons.Default.Inventory2, null); Spacer(Modifier.width(6.dp)); Text("Inventario") }
            Button(onClick = onAddProduct, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = Color(0xFF082018))) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(6.dp)); Text("Añadir producto") }
        }
        Spacer(Modifier.height(12.dp))
        Text("●  Datos locales · Disponible sin conexión", color = Lime, style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
internal fun DashboardMetric(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(modifier = modifier.heightIn(min = 118.dp).clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(22.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Brush.linearGradient(listOf(color.copy(alpha = .52f), color.copy(alpha = .10f), Hairline)))) {
        Box(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(color.copy(alpha = .18f), Panel, Panel)))) {
            Box(Modifier.size(90.dp).align(Alignment.TopEnd).offset(x = 24.dp, y = (-20).dp).background(color.copy(alpha = .08f), CircleShape))
            Column(Modifier.fillMaxWidth().padding(15.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(36.dp).background(color.copy(alpha = .14f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = color, modifier = Modifier.size(20.dp)) }
                    Spacer(Modifier.weight(1f)); Box(Modifier.size(5.dp).background(color, CircleShape))
                }
                Spacer(Modifier.height(12.dp))
                Text(value, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Text(title.uppercase(), color = Muted, style = MaterialTheme.typography.labelSmall, letterSpacing = .7.sp)
            }
        }
    }
}

@Composable
internal fun MoreScreen(modifier: Modifier, onOpen: (String) -> Unit) {
    val modules = listOf(
        Triple("Proveedores", "Administra los datos de tus suministradores", Icons.Default.Storefront),
        Triple("Clientes", "Mantén organizada la información de tus clientes", Icons.Default.People),
        Triple("Informes y estadísticas", "Consulta ventas, ganancias y productos más vendidos", Icons.Default.BarChart),
        Triple("Alertas de stock y vencimiento", "Identifica productos que requieren atención", Icons.Default.Notifications),
        Triple("Catálogo", "Prepara y comparte tu catálogo de productos", Icons.Default.ShoppingBag),
        Triple("Licencias", "Consulta el estado de la licencia de la aplicación", Icons.Default.CheckCircle),
        Triple("Configuración", "Preferencias, idioma, tema y seguridad", Icons.Default.Settings),
        Triple("Copias de seguridad", "Exporta un resumen de los datos del negocio", Icons.Default.CloudUpload)
    )
    Column(modifier.fillMaxSize().stockProBackdrop().padding(horizontal = 18.dp).padding(top = 22.dp)) {
        Text("Más herramientas", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Todo lo que necesitas para gestionar tu negocio", color = Muted)
        Spacer(Modifier.height(18.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
            items(modules) { item ->
                Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(17.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Hairline.copy(alpha = .95f)), modifier = Modifier.fillMaxWidth().clickable { onOpen(item.first) }) {
                    Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(46.dp).background(Color(0xFF15372F), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) { Icon(item.third, null, tint = Lime) }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) { Text(item.first, fontWeight = FontWeight.SemiBold); Text(item.second, color = Muted, style = MaterialTheme.typography.bodySmall) }
                        Text("›", color = Lime, style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }
        }
    }
}

@Composable
internal fun InventoryScreen(modifier: Modifier, products: List<ProductEntity>, vm: StockProViewModel, openAddRequest: Int, onAddRequestHandled: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var filterLow by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var showCategoriesInfo by remember { mutableStateOf(false) }
    val categories by vm.categories.collectAsStateWithLifecycle()
    var newCategory by remember { mutableStateOf("") }
    var editing by remember { mutableStateOf<ProductEntity?>(null) }
    var showForm by remember { mutableStateOf(false) }
    var adjust by remember { mutableStateOf<ProductEntity?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(openAddRequest) {
        if (openAddRequest > 0) {
            editing = null
            showForm = true
            onAddRequestHandled()
        }
    }
    val filtered = products.filter { it.name.contains(query, true) || it.sku.contains(query, true) || it.barcode.contains(query, true) }.filter { !filterLow || it.stockQuantity <= it.minimumStock }.filter { selectedCategoryId == null || it.categoryId == selectedCategoryId }
    Column(modifier.fillMaxSize().stockProBackdrop().padding(horizontal = 18.dp).padding(top = 18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("STOCKPRO  /  PREMIUM", color = Mint, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                Spacer(Modifier.height(4.dp))
                Text("Inventario", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, letterSpacing = (-.5).sp)
                Text("Control de existencias y precios", color = Muted, style = MaterialTheme.typography.bodySmall)
            }
            FilledIconButton(onClick = { editing = null; showForm = true }, colors = IconButtonDefaults.filledIconButtonColors(containerColor = Lime, contentColor = Color(0xFF17210F))) { Icon(Icons.Default.Add, "Añadir producto") }
        }
        Spacer(Modifier.height(18.dp))
        OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Buscar producto o código") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true, shape = RoundedCornerShape(16.dp))
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = !filterLow && selectedCategoryId == null, onClick = { filterLow = false; selectedCategoryId = null }, label = { Text("Todos (${products.size})") }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Lime, selectedLabelColor = AppBg, containerColor = Panel, labelColor = Muted))
            FilterChip(selected = false, onClick = { showCategoriesInfo = true }, label = { Text("Categorías") }, colors = FilterChipDefaults.filterChipColors(containerColor = Panel, labelColor = Muted))
            FilterChip(selected = filterLow, onClick = { filterLow = !filterLow; if (filterLow) selectedCategoryId = null }, label = { Text("Stock bajo · ${products.count { it.stockQuantity <= it.minimumStock }}") }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF4A321B), selectedLabelColor = Orange, containerColor = Panel, labelColor = Muted))
        }
        if (categories.isNotEmpty()) { Spacer(Modifier.height(8.dp)); Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) { categories.forEach { c -> FilterChip(selected = selectedCategoryId == c.id, onClick = { selectedCategoryId = if (selectedCategoryId == c.id) null else c.id }, label = { Text(c.name) }) } } }
        Spacer(Modifier.height(10.dp))
        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Inventory2, null, tint = Muted, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(if (query.isBlank() && !filterLow) "Todavía no hay productos" else "No se encontraron productos", fontWeight = FontWeight.SemiBold)
                    Text("Pulsa + para registrar el primero.", color = Muted)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filtered, key = { it.id }) { product ->
                    ProductRow(product, onClick = { editing = product; showForm = true }, onAdjust = { adjust = product })
                }
            }
        }
    }
    if (showCategoriesInfo) AlertDialog(containerColor = PanelRaised, titleContentColor = Color.White, textContentColor = Muted, onDismissRequest = { showCategoriesInfo = false }, title = { Text("Categorías de productos") }, text = { Column(Modifier.heightIn(max = 440.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) { Text("Organiza el inventario por familias.", color = Muted); OutlinedTextField(newCategory, { newCategory = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nueva categoría") }, singleLine = true); categories.forEach { c -> Row(Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(12.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Inventory2, null, tint = Lime); Spacer(Modifier.width(10.dp)); Text(c.name) } } } }, confirmButton = { TextButton(onClick = { vm.saveCategory(newCategory) { error -> if (error == null) newCategory = "" else message = error } }) { Text("Añadir") } }, dismissButton = { TextButton(onClick = { showCategoriesInfo = false }) { Text("Cerrar") } })
    if (showForm) ProductDialog(editing, categories, products, onDismiss = { showForm = false }, onSave = { id, name, sku, barcode, stock, min, unit, notes, purchasePrice, salePrice, expiryDate, imageKey, categoryId -> vm.saveProduct(id, name, sku, barcode, stock, min, unit, notes, purchasePrice, salePrice, expiryDate, imageKey, categoryId) { error -> if (error != null) message = error else showForm = false } })
    adjust?.let { product -> StockAdjustmentDialog(product, onDismiss = { adjust = null }, onSave = { delta, reason -> vm.adjustStock(product.id, delta, reason) { error -> if (error != null) message = error else adjust = null } }) }
    message?.let { AlertDialog(containerColor = PanelRaised, titleContentColor = Color.White, textContentColor = Muted, onDismissRequest = { message = null }, title = { Text("No se pudo completar") }, text = { Text(it) }, confirmButton = { TextButton(onClick = { message = null }) { Text("Aceptar") } }) }
}

@Composable
internal fun ProductRow(product: ProductEntity, onClick: () -> Unit, onAdjust: () -> Unit) {
    val isLow = product.stockQuantity <= product.minimumStock
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(22.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Brush.linearGradient(listOf(if (isLow) Orange.copy(alpha = .62f) else Mint.copy(alpha = .48f), Hairline, Color.Transparent))), modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(Modifier.background(Brush.horizontalGradient(listOf(if (isLow) Orange.copy(alpha = .07f) else Mint.copy(alpha = .055f), Panel, Panel))).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            ProductImageThumb(product.imageKey, Modifier.size(70.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.White)
                if (product.salePrice > 0) Text(money(product.salePrice), color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                Text("Stock: ${product.stockQuantity} ${product.unit}  ·  ${if (product.sku.isBlank()) "Sin SKU" else product.sku}", color = Mint, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Surface(color = if (isLow) Color(0xFF4B2025) else Color(0xFF103D2D), shape = RoundedCornerShape(50), border = androidx.compose.foundation.BorderStroke(1.dp, if (isLow) Color(0xFFFF7272).copy(alpha=.45f) else Lime.copy(alpha=.28f))) {
                    Text(if (isLow) "Stock bajo" else "Disponible", color = if (isLow) Color(0xFFFF8585) else Lime, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp))
                }
                IconButton(onClick = onAdjust, modifier = Modifier.size(34.dp)) { Icon(Icons.Default.Add, "Ajustar stock", tint = Lime) }
            }
        }
    }
}

@Composable
internal fun ProductImageThumb(imageKey: String, modifier: Modifier = Modifier) {
    val item = ProductImageLibrary.byKey(imageKey)
    val context = LocalContext.current
    val resId = remember(item.drawableName) { context.resources.getIdentifier(item.drawableName, "drawable", context.packageName) }
    Box(modifier.background(Brush.linearGradient(listOf(Color(0xFF174B3A), Color(0xFF0A1D19))), RoundedCornerShape(15.dp)).border(1.dp, Mint.copy(alpha = .25f), RoundedCornerShape(15.dp)), contentAlignment = Alignment.Center) {
        if (resId != 0) Image(painterResource(resId), contentDescription = item.label, modifier = Modifier.fillMaxSize().padding(3.dp), contentScale = ContentScale.Crop)
        else Icon(Icons.Default.Inventory2, null, tint = Lime)
    }
}

internal fun generateProductSku(): String = ProductSku.generate("", "", emptyList())

@Composable
internal fun ProductDialog(product: ProductEntity?, categories: List<CategoryEntity>, existingProducts: List<ProductEntity>, onDismiss: () -> Unit, onSave: (Long?, String, String, String, Int, Int, String, String, Double, Double, String, String, Long?) -> Unit) {
    var name by remember(product?.id) { mutableStateOf(product?.name.orEmpty()) }
    var sku by remember(product?.id) { mutableStateOf(product?.sku?.takeIf { it.isNotBlank() } ?: if (product == null) ProductSku.generate("", "", existingProducts.map { it.sku }) else "") }
    var autoSku by remember(product?.id) { mutableStateOf(product == null) }
    var imageKey by remember(product?.id) { mutableStateOf(product?.imageKey ?: "otros") }
    var barcode by remember(product?.id) { mutableStateOf(product?.barcode.orEmpty()) }
    var stock by remember(product?.id) { mutableStateOf((product?.stockQuantity ?: 0).toString()) }
    var min by remember(product?.id) { mutableStateOf((product?.minimumStock ?: 0).toString()) }
    var unit by remember(product?.id) { mutableStateOf(product?.unit ?: "unidad") }
    var notes by remember(product?.id) { mutableStateOf(product?.notes.orEmpty()) }
    var purchasePrice by remember(product?.id) { mutableStateOf(if ((product?.purchasePrice ?: 0.0) == 0.0) "" else product?.purchasePrice.toString()) }
    var salePrice by remember(product?.id) { mutableStateOf(if ((product?.salePrice ?: 0.0) == 0.0) "" else product?.salePrice.toString()) }
    var expiryDate by remember(product?.id) { mutableStateOf(product?.expiryDate.orEmpty()) }
    var categoryId by remember(product?.id) { mutableStateOf(product?.categoryId) }
    var validation by remember { mutableStateOf<String?>(null) }
    AlertDialog(containerColor = PanelRaised, titleContentColor = Color.White, textContentColor = Muted, onDismissRequest = onDismiss, title = {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(42.dp).background(Brush.linearGradient(listOf(Color(0xFF123E32), Color(0xFF0B2023))), RoundedCornerShape(13.dp)).border(1.dp, Mint.copy(alpha = .42f), RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Inventory2, null, tint = Lime, modifier = Modifier.size(23.dp))
            }
            Column {
                Text(if (product == null) "Nuevo producto" else "Editar producto", fontWeight = FontWeight.Bold)
                Text("FICHA DE INVENTARIO · STOCKPRO", color = Muted, style = MaterialTheme.typography.labelSmall, letterSpacing = 1.sp)
            }
        }
    }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 520.dp)) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { OutlinedTextField(name, { value -> name = value; validation = null; if (autoSku && product == null) sku = ProductSku.generate(value, categories.firstOrNull { it.id == categoryId }?.name.orEmpty(), existingProducts.map { it.sku }) }, label = { Text("Nombre *") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item {
                    OutlinedTextField(
                        value = sku,
                        onValueChange = { sku = it.uppercase().filter { c -> c.isLetterOrDigit() || c == '-' || c == '_' }; autoSku = false },
                        label = { Text("Referencia / SKU *") },
                        supportingText = { Text(if (product == null) "Generado automáticamente al abrir el formulario · puedes editarlo" else "Referencia del producto · puedes editarla") },
                        trailingIcon = { TextButton(onClick = { sku = ProductSku.generate(name, categories.firstOrNull { it.id == categoryId }?.name.orEmpty(), existingProducts.map { it.sku }); autoSku = true }) { Text("AUTO", color = Lime, style = MaterialTheme.typography.labelSmall) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item { OutlinedTextField(barcode, { barcode = it }, label = { Text("Código de barras") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(stock, { stock = it.filter { c -> c.isDigit() } }, label = { Text("Stock actual") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(min, { min = it.filter { c -> c.isDigit() } }, label = { Text("Avisar con stock igual o inferior a") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { Text("Categoría", fontWeight = FontWeight.SemiBold); Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) { FilterChip(selected = categoryId == null, onClick = { categoryId = null; if (product == null) { imageKey = ProductImageLibrary.suggest(name); if (autoSku) sku = ProductSku.generate(name, "", existingProducts.map { it.sku }) } }, label = { Text("General") }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF174D43), selectedLabelColor = Lime, containerColor = Panel, labelColor = Muted)); categories.forEach { c -> FilterChip(selected = categoryId == c.id, onClick = { categoryId = c.id; if (product == null) { imageKey = ProductImageLibrary.suggest(name, c.name); if (autoSku) sku = ProductSku.generate(name, c.name, existingProducts.map { it.sku }) } }, label = { Text(c.name) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF174D43), selectedLabelColor = Lime, containerColor = Panel, labelColor = Muted)) } } }
                item {
                    val suggested = ProductImageLibrary.suggest(name, categories.firstOrNull { it.id == categoryId }?.name.orEmpty())
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Imagen del producto", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        TextButton(onClick = { imageKey = suggested }) { Text("Sugerir automáticamente", color = Lime) }
                    }
                    Text("Biblioteca propia · ${ProductImageLibrary.items.size} iconos realistas", color = Muted, style = MaterialTheme.typography.bodySmall)
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        ProductImageLibrary.items.forEach { item ->
                            val context = LocalContext.current
                            val resId = remember(item.drawableName) { context.resources.getIdentifier(item.drawableName, "drawable", context.packageName) }
                            Column(Modifier.width(76.dp).background(if (imageKey == item.key) Color(0xFF123E32) else Panel, RoundedCornerShape(12.dp)).border(1.dp, if (imageKey == item.key) Lime else Hairline, RoundedCornerShape(12.dp)).clickable { imageKey = item.key }.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                if (resId != 0) Image(painterResource(resId), contentDescription = item.label, modifier = Modifier.size(54.dp), contentScale = ContentScale.Crop)
                                Text(item.label, color = if (imageKey == item.key) Lime else Muted, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                            }
                        }
                    }
                }
                item { OutlinedTextField(unit, { unit = it }, label = { Text("Unidad (unidad, kg, caja…)") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(purchasePrice, { purchasePrice = it.filter { c -> c.isDigit() || c == '.' || c == ',' }.replace(',', '.') }, label = { Text("Precio de compra ($ por unidad)") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(salePrice, { salePrice = it.filter { c -> c.isDigit() || c == '.' || c == ',' }.replace(',', '.') }, label = { Text("Precio de venta ($ por unidad)") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(expiryDate, { expiryDate = it.filter { c -> c.isDigit() || c == '-' }.take(10) }, label = { Text("Vencimiento (AAAA-MM-DD, opcional)") }, placeholder = { Text("2026-12-31") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(notes, { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = 2) }
                item { validation?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            }
        }
    }, confirmButton = { TextButton(onClick = { val s = stock.toIntOrNull(); val m = min.toIntOrNull(); if (name.isBlank()) validation = "El nombre es obligatorio." else if (sku.isBlank()) validation = "La referencia SKU es obligatoria; genera una o escribe una referencia." else if (s == null || m == null) validation = "Introduce cantidades válidas." else if ((purchasePrice.toDoubleOrNull() ?: 0.0) < 0 || (salePrice.toDoubleOrNull() ?: 0.0) < 0) validation = "Los precios deben ser positivos." else if (expiryDate.isNotBlank() && !Regex("\\d{4}-\\d{2}-\\d{2}").matches(expiryDate)) validation = "Usa el formato de fecha AAAA-MM-DD." else onSave(product?.id, name, sku, barcode, s, m, unit, notes, purchasePrice.toDoubleOrNull() ?: 0.0, salePrice.toDoubleOrNull() ?: 0.0, expiryDate, imageKey, categoryId) }) { Text("Guardar") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } })
}

@Composable
internal fun StockAdjustmentDialog(product: ProductEntity, onDismiss: () -> Unit, onSave: (Int, String) -> Unit) {
    var amount by remember { mutableStateOf("1") }
    var reason by remember { mutableStateOf("") }
    var incoming by remember { mutableStateOf(true) }
    AlertDialog(containerColor = PanelRaised, titleContentColor = Color.White, textContentColor = Muted, onDismissRequest = onDismiss, title = { Text("Movimiento de stock") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(product.name, fontWeight = FontWeight.SemiBold)
            Text("Stock actual: ${product.stockQuantity} ${product.unit}", color = Muted)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = incoming, onClick = { incoming = true }, label = { Text("Entrada +") })
                FilterChip(selected = !incoming, onClick = { incoming = false }, label = { Text("Salida −") })
            }
            OutlinedTextField(amount, { amount = it.filter(Char::isDigit) }, label = { Text("Cantidad") }, singleLine = true)
            OutlinedTextField(reason, { reason = it }, label = { Text("Motivo (recomendado)") }, minLines = 2)
        }
    }, confirmButton = { TextButton(onClick = { val n = amount.toIntOrNull(); if (n != null && n > 0) onSave(if (incoming) n else -n, reason.ifBlank { if (incoming) "Entrada manual" else "Salida manual" }) }) { Text("Registrar") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } })
}

@Composable
internal fun PremiumHeader(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 22.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("STOCKPRO  /  PREMIUM", color = Mint, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, letterSpacing = 1.7.sp)
            Spacer(Modifier.height(5.dp))
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Color.White, letterSpacing = (-.65).sp, lineHeight = 31.sp)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, color = Muted, style = MaterialTheme.typography.bodySmall, lineHeight = 18.sp)
        }
        Spacer(Modifier.width(12.dp))
        Box(Modifier.size(56.dp).background(Brush.linearGradient(listOf(Color(0xFF1B6B4D), Color(0xFF102D2B), Panel)), RoundedCornerShape(18.dp)).border(1.dp, Mint.copy(alpha = .38f), RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) {
            Box(Modifier.size(42.dp).background(Color.White.copy(alpha = .025f), RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Lime, modifier = Modifier.size(25.dp))
            }
        }
    }
}

internal fun money(value: Double) = String.format(java.util.Locale.US, "$ %.2f", value)

