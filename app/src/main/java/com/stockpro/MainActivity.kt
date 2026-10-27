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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
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
import com.stockpro.data.BusinessStore
import com.stockpro.ui.StockProViewModel
import com.stockpro.ui.theme.StockProTheme
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(5, 10, 13)
        window.navigationBarColor = android.graphics.Color.rgb(5, 10, 13)
        setContent { StockProTheme { StockProApp() } }
    }
}

private val Lime = Color(0xFF48F2A0)
private val Mint = Color(0xFF27D9C3)
private val Muted = Color(0xFF91A7A8)
private val AppBg = Color(0xFF050A0D)
private val Panel = Color(0xFF0B151A)
private val PanelRaised = Color(0xFF102329)
private val Blue = Color(0xFF46BDEB)
private val Orange = Color(0xFFFFB45B)
private val Hairline = Color(0xFF20383A)

@Composable
private fun StockProApp(vm: StockProViewModel = viewModel()) {
    var showWelcome by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { delay(4200); showWelcome = false }
    AnimatedVisibility(visible = showWelcome, enter = fadeIn(tween(350)), exit = fadeOut(tween(500))) { WelcomeScreen() }
    AnimatedVisibility(visible = !showWelcome, enter = fadeIn(tween(650)) + scaleIn(initialScale = .98f, animationSpec = tween(650))) {
        MainShell(vm)
    }
}

@Composable
private fun WelcomeScreen() {
    val motion = rememberInfiniteTransition(label = "stockpro-cinematic-intro")
    val pulse by motion.animateFloat(.94f, 1.075f, infiniteRepeatable(tween(1550, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "logo-pulse")
    val glow by motion.animateFloat(.28f, .92f, infiniteRepeatable(tween(1150, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "logo-glow")
    val orbit by motion.animateFloat(0f, 360f, infiniteRepeatable(tween(9000, easing = androidx.compose.animation.core.LinearEasing)), label = "orbit-rotation")
    val scan by motion.animateFloat(-.2f, 1.2f, infiniteRepeatable(tween(2300, easing = FastOutSlowInEasing)), label = "scanline")
    val rise by motion.animateFloat(0f, 1f, infiniteRepeatable(tween(2800, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "horizon-rise")
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF010407), Color(0xFF03120F), Color(0xFF020609))))) {
        // Nebulosa y haces de luz detrás del emblema.
        Box(Modifier.align(Alignment.Center).size(330.dp).scale(pulse).background(Brush.radialGradient(listOf(Lime.copy(alpha = glow * .19f), Mint.copy(alpha = glow * .075f), Color.Transparent)), CircleShape))
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height
            // Puntos de energía con posiciones deterministas: no provocan trabajo aleatorio por fotograma.
            for (i in 0 until 48) {
                val x = ((i * 97 + 31) % 997) / 997f * w
                val baseY = ((i * 173 + 59) % 991) / 991f * h
                val y = (baseY + rise * (if (i % 2 == 0) 18f else -18f)).coerceIn(0f, h)
                val radius = if (i % 7 == 0) 2.1f else 1.0f
                drawCircle(if (i % 3 == 0) Mint.copy(alpha = .68f) else Lime.copy(alpha = .42f), radius, Offset(x, y))
                if (i % 8 == 0) drawLine(Lime.copy(alpha = .16f), Offset(x, y + 5f), Offset(x - 10f, y + 28f), strokeWidth = 1f)
            }
            // Horizonte digital en perspectiva en el tercio inferior.
            val horizon = h * .70f + rise * 13f
            for (i in 0..11) {
                val y = horizon + i * i * 2.25f
                if (y < h) drawLine(Mint.copy(alpha = .12f + i * .006f), Offset(0f, y), Offset(w, y), strokeWidth = 1f)
            }
            for (i in -8..8) drawLine(Lime.copy(alpha = .11f), Offset(w * .5f, horizon), Offset(w * .5f + i * w * .105f, h), strokeWidth = 1f)
            // Destello horizontal que recorre lentamente la escena.
            val beamY = h * (.24f + scan * .49f)
            drawLine(Brush.horizontalGradient(listOf(Color.Transparent, Mint.copy(alpha = .13f), Lime.copy(alpha = .34f), Mint.copy(alpha = .13f), Color.Transparent)), Offset(0f, beamY), Offset(w, beamY), strokeWidth = 2f)
        }
        // Halo y anillos orbitales animados.
        Box(Modifier.align(Alignment.Center).offset(y = (-78).dp).size(206.dp).rotate(orbit).border(1.dp, Brush.sweepGradient(listOf(Color.Transparent, Lime.copy(alpha = .9f), Mint.copy(alpha = .55f), Color.Transparent, Lime.copy(alpha = .65f))), CircleShape))
        Box(Modifier.align(Alignment.Center).offset(y = (-78).dp).size(174.dp).rotate(-orbit * .72f).border(1.dp, Brush.sweepGradient(listOf(Mint.copy(alpha = .7f), Color.Transparent, Lime.copy(alpha = .65f), Color.Transparent)), CircleShape))
        // Línea de luz en la base, como una plataforma holográfica.
        Box(Modifier.align(Alignment.Center).offset(y = 42.dp).fillMaxWidth(.72f).height(24.dp).background(Brush.radialGradient(listOf(Lime.copy(alpha = glow * .32f), Mint.copy(alpha = .12f), Color.Transparent)), CircleShape))
        Column(Modifier.align(Alignment.Center).fillMaxWidth().padding(horizontal = 28.dp).offset(y = (-8).dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(3.dp))
            Box(Modifier.size(142.dp).scale(pulse).background(Brush.radialGradient(listOf(Color(0xFF164E3C), Color(0xFF071A19), Color(0xFF03090C))), RoundedCornerShape(39.dp)).border(1.dp, Brush.linearGradient(listOf(Lime.copy(alpha = .95f), Mint.copy(alpha = .65f), Color(0xFF102C29))), RoundedCornerShape(39.dp)), contentAlignment = Alignment.Center) {
                Box(Modifier.size(119.dp).background(Brush.radialGradient(listOf(Lime.copy(alpha = glow * .16f), Color.Transparent)), CircleShape))
                Icon(Icons.Default.Inventory2, null, tint = Lime.copy(alpha = .16f), modifier = Modifier.size(91.dp).offset(x = 1.dp, y = 2.dp))
                Icon(Icons.Default.Inventory2, null, tint = Brush.linearGradient(listOf(Color(0xFFE1FFE9), Lime, Mint)).let { Lime }, modifier = Modifier.size(78.dp))
                Icon(Icons.Default.TrendingUp, null, tint = Color.White, modifier = Modifier.size(30.dp).align(Alignment.BottomEnd).offset(x = (-14).dp, y = (-14).dp))
                Box(Modifier.align(Alignment.TopCenter).offset(y = 3.dp).fillMaxWidth(.56f).height(2.dp).background(Brush.horizontalGradient(listOf(Color.Transparent, Color.White.copy(alpha = .9f), Color.Transparent))))
            }
            Spacer(Modifier.height(31.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Stock", color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, letterSpacing = (-1.5).sp)
                Text("Pro", color = Lime, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, letterSpacing = (-1.5).sp)
            }
            Text("P R E M I U M", color = Mint, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, letterSpacing = 5.5.sp)
            Spacer(Modifier.height(14.dp))
            Text("Tu negocio en buenas manos", color = Color.White.copy(alpha = .9f), style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(40.dp))
            Box(Modifier.fillMaxWidth(.70f).height(4.dp).background(Color(0xFF17302B), RoundedCornerShape(50))) {
                Box(Modifier.fillMaxWidth((.18f + glow * .82f).coerceIn(.18f, 1f)).fillMaxHeight().background(Brush.horizontalGradient(listOf(Color(0xFF00E676), Lime, Mint)), RoundedCornerShape(50)))
            }
            Spacer(Modifier.height(13.dp))
            Text("PREPARANDO TU ESPACIO DE TRABAJO", color = Muted.copy(alpha = .92f), style = MaterialTheme.typography.labelSmall, letterSpacing = 1.35.sp)
        }
        Text("GESTIÓN  ·  CONTROL  ·  CRECIMIENTO", color = Color.White.copy(alpha = .58f), style = MaterialTheme.typography.labelSmall, letterSpacing = 1.2.sp, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 35.dp))
        // Borde luminoso discreto que enmarca la experiencia de bienvenida.
        Box(Modifier.fillMaxSize().padding(9.dp).border(1.dp, Brush.verticalGradient(listOf(Lime.copy(alpha = .32f), Mint.copy(alpha = .08f), Lime.copy(alpha = .22f))), RoundedCornerShape(28.dp)))
    }
}


@Composable
private fun StockProEmblem(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width; val h = size.height
        fun pt(x: Float, y: Float) = Offset(w * x, h * y)
        val stroke = (w * .045f).coerceAtLeast(2f)
        val glowStroke = stroke * 2.3f
        val top = Path().apply { moveTo(w*.50f, h*.07f); lineTo(w*.91f, h*.29f); lineTo(w*.50f, h*.51f); lineTo(w*.09f, h*.29f); close() }
        val left = Path().apply { moveTo(w*.09f, h*.29f); lineTo(w*.09f, h*.70f); lineTo(w*.50f, h*.93f); lineTo(w*.50f, h*.51f) }
        val right = Path().apply { moveTo(w*.91f, h*.29f); lineTo(w*.91f, h*.70f); lineTo(w*.50f, h*.93f) }
        // Soft neon bloom beneath the crisp geometric mark.
        drawPath(top, Lime.copy(alpha = .16f), style = Stroke(width = glowStroke * 2f))
        drawPath(left, Lime.copy(alpha = .18f), style = Stroke(width = glowStroke * 2f))
        drawPath(right, Mint.copy(alpha = .2f), style = Stroke(width = glowStroke * 2f))
        drawPath(top, Brush.linearGradient(listOf(Color(0xFFE4FFF0), Lime, Mint)), style = Stroke(width = stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
        drawPath(left, Brush.linearGradient(listOf(Lime, Color(0xFF13C96A))), style = Stroke(width = stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
        drawPath(right, Brush.linearGradient(listOf(Mint, Color(0xFF087D80))), style = Stroke(width = stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
        drawLine(Color(0xFFD9FFF0), pt(.50f,.07f), pt(.50f,.51f), strokeWidth = stroke*.8f)
        drawLine(Lime, pt(.50f,.51f), pt(.30f,.40f), strokeWidth = stroke*.8f)
        drawLine(Mint, pt(.50f,.51f), pt(.70f,.40f), strokeWidth = stroke*.8f)
        // Three ascending bars evoke inventory analytics without a generic app icon.
        drawLine(Color(0xFFE6FFF3), pt(.28f,.65f), pt(.28f,.74f), strokeWidth = stroke*1.35f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        drawLine(Lime, pt(.39f,.70f), pt(.39f,.79f), strokeWidth = stroke*1.35f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        drawLine(Mint, pt(.61f,.64f), pt(.61f,.76f), strokeWidth = stroke*1.35f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        drawLine(Mint, pt(.72f,.59f), pt(.72f,.69f), strokeWidth = stroke*1.35f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    }
}

private enum class Tab(val title: String) { HOME("Inicio"), INVENTORY("Inventario"), SALES("Ventas"), PURCHASES("Compras"), MORE("Más") }

@Composable
private fun MainShell(vm: StockProViewModel) {
    var tab by remember { mutableStateOf(Tab.HOME) }
    var module by remember { mutableStateOf("Inicio") }
    var addProductRequest by remember { mutableStateOf(0) }
    val products by vm.products.collectAsStateWithLifecycle()
    val count by vm.productCount.collectAsStateWithLifecycle()
    val low by vm.lowStockCount.collectAsStateWithLifecycle()
    Scaffold(
        containerColor = AppBg,
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF070F12), contentColor = Lime, tonalElevation = 0.dp, windowInsets = NavigationBarDefaults.windowInsets) {
                val tabs = listOf(Tab.HOME, Tab.INVENTORY, Tab.SALES, Tab.PURCHASES, Tab.MORE)
                val icons = listOf(Icons.Default.Home, Icons.Default.Inventory2, Icons.Default.ShoppingCart, Icons.Default.ArrowUpward, Icons.Default.MoreHoriz)
                tabs.forEachIndexed { i, item -> NavigationBarItem(selected = tab == item, onClick = { tab = item; module = item.title }, icon = { Icon(icons[i], contentDescription = item.title) }, label = { Text(item.title, maxLines = 1) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = Lime, selectedTextColor = Lime, indicatorColor = Color(0xFF173B35), unselectedIconColor = Muted, unselectedTextColor = Muted)) }
            }
        }
    ) { padding ->
        when (module) {
            "Inicio" -> Dashboard(Modifier.padding(padding), count, low, products, onInventory = { tab = Tab.INVENTORY; module = "Inventario" }, onAddProduct = { tab = Tab.INVENTORY; module = "Inventario"; addProductRequest++ })
            "Inventario" -> InventoryScreen(Modifier.padding(padding), products, vm, addProductRequest, onAddRequestHandled = { addProductRequest = 0 })
            "Ventas" -> SalesScreen(Modifier.padding(padding), products, vm)
            "Compras" -> PurchasesScreen(Modifier.padding(padding), products, vm)
            "Más" -> MoreScreen(Modifier.padding(padding), onOpen = { module = it })
            "Proveedores" -> ContactsScreen(Modifier.padding(padding), "Proveedor")
            "Clientes" -> ContactsScreen(Modifier.padding(padding), "Cliente")
            "Informes y estadísticas" -> ReportsScreen(Modifier.padding(padding))
            "Alertas de stock y vencimiento" -> AlertsScreen(Modifier.padding(padding), products, vm)
            "Configuración" -> SettingsScreen(Modifier.padding(padding))
            "Copias de seguridad" -> BackupScreen(Modifier.padding(padding), products)
            "Licencias" -> LicenseScreen(Modifier.padding(padding))
            "Catálogo" -> CatalogScreen(Modifier.padding(padding), products)
            else -> MoreScreen(Modifier.padding(padding), onOpen = { module = it })
        }
    }
}

@Composable
private fun Dashboard(modifier: Modifier, count: Int, low: Int, products: List<ProductEntity>, onInventory: () -> Unit, onAddProduct: () -> Unit) {
    val context = LocalContext.current
    val entries = remember(products) { BusinessStore.entries(context) }
    val totalSales = entries.filter { it.type == "Venta" }.sumOf { it.amount }
    val totalPurchases = entries.filter { it.type == "Compra" }.sumOf { it.amount }
    val inventoryValue = products.sumOf { it.stockQuantity * it.purchasePrice }
    val notificationsEnabled = BusinessStore.setting(context, "notifications", "true") == "true"
    val lowProducts = if (notificationsEnabled) products.filter { it.stockQuantity <= it.minimumStock }.take(3) else emptyList()
    Column(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF071014), AppBg, Color(0xFF06110D)))).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(top = 12.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).background(Brush.linearGradient(listOf(Color(0xFF1D7250), Color(0xFF102329))), RoundedCornerShape(15.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Inventory2, null, tint = Lime, modifier = Modifier.size(28.dp))
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
        Text("TU NEGOCIO, BAJO CONTROL  ·  RESUMEN GENERAL", color = Muted, style = MaterialTheme.typography.labelSmall, letterSpacing = .8.sp)
        Spacer(Modifier.height(18.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(24.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Lime.copy(alpha = .55f)), modifier = Modifier.fillMaxWidth().clickable(onClick = onInventory)) {
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
            DashboardMetric("Productos", count.toString(), Icons.Default.ShoppingBag, Blue, Modifier.weight(1f), onInventory)
            DashboardMetric("Stock bajo", low.toString(), Icons.Default.Warning, Orange, Modifier.weight(1f), onInventory)
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            DashboardMetric("Ventas", money(totalSales), Icons.Default.ShoppingCart, Color(0xFF087BEA), Modifier.weight(1f), {})
            DashboardMetric("Balance", money(totalSales - totalPurchases), Icons.Default.TrendingUp, Color(0xFF079B63), Modifier.weight(1f), {})
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
                Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("Actividad reciente", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Text("Operaciones registradas", color = Muted, style = MaterialTheme.typography.bodySmall) }; Icon(Icons.Default.BarChart, null, tint = Lime) }
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
private fun DashboardMetric(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(modifier = modifier.heightIn(min = 112.dp).clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(21.dp), border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = .24f))) {
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
private fun MoreScreen(modifier: Modifier, onOpen: (String) -> Unit) {
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
    Column(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF071014), AppBg, Color(0xFF06110D)))).padding(horizontal = 18.dp).padding(top = 22.dp)) {
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
private fun InventoryScreen(modifier: Modifier, products: List<ProductEntity>, vm: StockProViewModel, openAddRequest: Int, onAddRequestHandled: () -> Unit) {
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
    Column(modifier.fillMaxSize().padding(horizontal = 18.dp).padding(top = 18.dp)) {
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
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = !filterLow && selectedCategoryId == null, onClick = { filterLow = false; selectedCategoryId = null }, label = { Text("Todos (${products.size})") })
            FilterChip(selected = false, onClick = { showCategoriesInfo = true }, label = { Text("Categorías") })
            FilterChip(selected = filterLow, onClick = { filterLow = true }, label = { Text("Stock bajo") })
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
    if (showCategoriesInfo) AlertDialog(onDismissRequest = { showCategoriesInfo = false }, title = { Text("Categorías de productos") }, text = { Column(Modifier.heightIn(max = 440.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) { Text("Organiza el inventario por familias.", color = Muted); OutlinedTextField(newCategory, { newCategory = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nueva categoría") }, singleLine = true); categories.forEach { c -> Row(Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(12.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Inventory2, null, tint = Lime); Spacer(Modifier.width(10.dp)); Text(c.name) } } } }, confirmButton = { TextButton(onClick = { vm.saveCategory(newCategory) { error -> if (error == null) newCategory = "" else message = error } }) { Text("Añadir") } }, dismissButton = { TextButton(onClick = { showCategoriesInfo = false }) { Text("Cerrar") } })
    if (showForm) ProductDialog(editing, categories, onDismiss = { showForm = false }, onSave = { id, name, sku, barcode, stock, min, unit, notes, purchasePrice, salePrice, expiryDate, categoryId -> vm.saveProduct(id, name, sku, barcode, stock, min, unit, notes, purchasePrice, salePrice, expiryDate, categoryId) { error -> if (error != null) message = error else showForm = false } })
    adjust?.let { product -> StockAdjustmentDialog(product, onDismiss = { adjust = null }, onSave = { delta, reason -> vm.adjustStock(product.id, delta, reason) { error -> if (error != null) message = error else adjust = null } }) }
    message?.let { AlertDialog(onDismissRequest = { message = null }, title = { Text("No se pudo completar") }, text = { Text(it) }, confirmButton = { TextButton(onClick = { message = null }) { Text("Aceptar") } }) }
}

@Composable
private fun ProductRow(product: ProductEntity, onClick: () -> Unit, onAdjust: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Hairline.copy(alpha = .9f)), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).background(Color(0xFF15372F), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Inventory2, null, tint = Lime) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f).clickable(onClick = onClick)) {
                Text(product.name, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(if (product.sku.isBlank()) "Sin referencia" else "Ref. ${product.sku}", color = Muted, style = MaterialTheme.typography.bodySmall)
                Text("Stock: ${product.stockQuantity} ${product.unit}", color = if (product.stockQuantity <= product.minimumStock) Color(0xFFFFB86B) else Lime, style = MaterialTheme.typography.labelMedium)
                if (product.salePrice > 0) Text("Venta: ${money(product.salePrice)} · Compra: ${money(product.purchasePrice)}", color = Muted, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onAdjust) { Icon(Icons.Default.Add, "Ajustar stock", tint = Lime) }
        }
    }
}

@Composable
private fun ProductDialog(product: ProductEntity?, categories: List<CategoryEntity>, onDismiss: () -> Unit, onSave: (Long?, String, String, String, Int, Int, String, String, Double, Double, String, Long?) -> Unit) {
    var name by remember(product?.id) { mutableStateOf(product?.name.orEmpty()) }
    var sku by remember(product?.id) { mutableStateOf(product?.sku.orEmpty()) }
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
    AlertDialog(onDismissRequest = onDismiss, title = { Text(if (product == null) "Nuevo producto" else "Editar producto") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 520.dp)) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { OutlinedTextField(name, { name = it; validation = null }, label = { Text("Nombre *") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(sku, { sku = it }, label = { Text("Referencia / SKU") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(barcode, { barcode = it }, label = { Text("Código de barras") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(stock, { stock = it.filter { c -> c.isDigit() } }, label = { Text("Stock actual") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(min, { min = it.filter { c -> c.isDigit() } }, label = { Text("Avisar con stock igual o inferior a") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { Text("Categoría", fontWeight = FontWeight.SemiBold); Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) { FilterChip(selected = categoryId == null, onClick = { categoryId = null }, label = { Text("General") }); categories.forEach { c -> FilterChip(selected = categoryId == c.id, onClick = { categoryId = c.id }, label = { Text(c.name) }) } } }
                item { OutlinedTextField(unit, { unit = it }, label = { Text("Unidad (unidad, kg, caja…)") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(purchasePrice, { purchasePrice = it.filter { c -> c.isDigit() || c == '.' || c == ',' }.replace(',', '.') }, label = { Text("Precio de compra ($ por unidad)") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(salePrice, { salePrice = it.filter { c -> c.isDigit() || c == '.' || c == ',' }.replace(',', '.') }, label = { Text("Precio de venta ($ por unidad)") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(expiryDate, { expiryDate = it.filter { c -> c.isDigit() || c == '-' }.take(10) }, label = { Text("Vencimiento (AAAA-MM-DD, opcional)") }, placeholder = { Text("2026-12-31") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(notes, { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = 2) }
                item { validation?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            }
        }
    }, confirmButton = { TextButton(onClick = { val s = stock.toIntOrNull(); val m = min.toIntOrNull(); if (name.isBlank()) validation = "El nombre es obligatorio." else if (s == null || m == null) validation = "Introduce cantidades válidas." else if ((purchasePrice.toDoubleOrNull() ?: 0.0) < 0 || (salePrice.toDoubleOrNull() ?: 0.0) < 0) validation = "Los precios deben ser positivos." else if (expiryDate.isNotBlank() && !Regex("\\d{4}-\\d{2}-\\d{2}").matches(expiryDate)) validation = "Usa el formato de fecha AAAA-MM-DD." else onSave(product?.id, name, sku, barcode, s, m, unit, notes, purchasePrice.toDoubleOrNull() ?: 0.0, salePrice.toDoubleOrNull() ?: 0.0, expiryDate, categoryId) }) { Text("Guardar") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } })
}

@Composable
private fun StockAdjustmentDialog(product: ProductEntity, onDismiss: () -> Unit, onSave: (Int, String) -> Unit) {
    var amount by remember { mutableStateOf("1") }
    var reason by remember { mutableStateOf("") }
    var incoming by remember { mutableStateOf(true) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Movimiento de stock") }, text = {
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
private fun PremiumHeader(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 22.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("STOCKPRO  /  PREMIUM", color = Mint, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, letterSpacing = 1.7.sp)
            Spacer(Modifier.height(5.dp))
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Color.White, letterSpacing = (-.65).sp, lineHeight = 31.sp)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, color = Muted, style = MaterialTheme.typography.bodySmall, lineHeight = 18.sp)
        }
        Spacer(Modifier.width(12.dp))
        Box(Modifier.size(52.dp).background(Brush.linearGradient(listOf(Color(0xFF174D43), Color(0xFF102329), Panel)), RoundedCornerShape(17.dp)).then(Modifier), contentAlignment = Alignment.Center) {
            Box(Modifier.size(42.dp).background(Color.White.copy(alpha = .025f), RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Lime, modifier = Modifier.size(25.dp))
            }
        }
    }
}

private fun money(value: Double) = String.format(java.util.Locale.US, "$ %.2f", value)

@Composable
private fun SalesScreen(modifier: Modifier, products: List<ProductEntity>, vm: StockProViewModel) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf<ProductEntity?>(null) }
    var qty by remember { mutableStateOf("1") }
    var amount by remember { mutableStateOf("") }
    LaunchedEffect(products) { if (selected == null && products.isNotEmpty()) { selected = products.first(); if (products.first().salePrice > 0) amount = (products.first().salePrice * (qty.toIntOrNull() ?: 1)).toString() } }
    var party by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    val recent = remember { mutableStateListOf<BusinessStore.Entry>().apply { addAll(BusinessStore.entries(context).filter { it.type == "Venta" }.take(5)) } }
    Column(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF071014), AppBg, Color(0xFF06110D)))).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(top = 20.dp, bottom = 24.dp)) {
        PremiumHeader("Nueva venta", "Punto de venta · actualización de existencias", Icons.Default.ShoppingCart)
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(24.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Lime.copy(alpha = .25f)), modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Color(0xFF145B47), Color(0xFF102C2D), Color(0xFF0B151A)))).padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text("VENTA RÁPIDA", color = Color.White.copy(alpha = .8f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold); Text("Registra una operación", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text("El stock se descuenta al confirmar", color = Color.White.copy(alpha = .8f), style = MaterialTheme.typography.bodySmall) }
                Icon(Icons.Default.ReceiptLong, null, tint = Lime, modifier = Modifier.size(38.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("1 · Selecciona un producto", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        if (products.isEmpty()) EmptyState("Aún no hay productos", "Registra artículos en Inventario antes de realizar una venta.", Icons.Default.Inventory2)
        else Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            products.forEach { p -> FilterChip(selected = selected?.id == p.id, onClick = { selected = p; amount = if (p.salePrice > 0) (p.salePrice * (qty.toIntOrNull() ?: 1)).toString() else "" }, label = { Text("${p.name} · ${p.stockQuantity}", maxLines = 1) }, leadingIcon = if (selected?.id == p.id) ({ Icon(Icons.Default.CheckCircle, null) }) else null)
        } }
        Spacer(Modifier.height(14.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Hairline.copy(alpha = .9f)), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("2 · Detalles de la operación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = qty, onValueChange = { qty = it.filter(Char::isDigit); if ((selected?.salePrice ?: 0.0) > 0) amount = ((qty.toIntOrNull() ?: 0) * (selected?.salePrice ?: 0.0)).toString() }, modifier = Modifier.fillMaxWidth(), label = { Text("Cantidad") }, singleLine = true)
                OutlinedTextField(value = amount, onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' || c == ',' }.replace(',', '.') }, modifier = Modifier.fillMaxWidth(), label = { Text("Importe total ($)") }, singleLine = true, leadingIcon = { Icon(Icons.Default.TrendingUp, null, tint = Lime) })
                OutlinedTextField(value = party, onValueChange = { party = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Cliente (opcional)") }, singleLine = true)
                Button(onClick = {
                    val p = products.firstOrNull { it.id == selected?.id }; val n = qty.toIntOrNull(); val total = amount.toDoubleOrNull()
                    when { p == null -> message = "Selecciona un producto."; n == null || n <= 0 -> message = "Introduce una cantidad válida."; n > p.stockQuantity -> message = "No hay existencias suficientes."; total == null || total < 0 -> message = "Introduce un importe total válido."; else -> { saving = true; vm.adjustStock(p.id, -n, "Venta registrada${if (party.isBlank()) "" else " · ${party.trim()}"}") { error -> saving = false; if (error != null) message = error else { val entry = BusinessStore.Entry(System.currentTimeMillis(), "Venta", p.name, n, total, party.trim(), System.currentTimeMillis()); BusinessStore.addEntry(context, entry); recent.add(0, entry); message = "Venta registrada y existencias actualizadas."; qty = "1"; amount = if (p.salePrice > 0) p.salePrice.toString() else ""; party = "" } } } }
                }, enabled = !saving && products.isNotEmpty(), modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = Color(0xFF102218))) { Icon(Icons.Default.CheckCircle, null); Spacer(Modifier.width(8.dp)); Text(if (saving) "Registrando…" else "Confirmar venta", fontWeight = FontWeight.Bold) }
            }
        }
        Spacer(Modifier.height(20.dp)); Text("Ventas recientes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp))
        if (recent.isEmpty()) EmptyState("Sin ventas todavía", "Las ventas confirmadas aparecerán aquí.", Icons.Default.ReceiptLong) else recent.take(5).forEach { EntryRow(it, Lime) }
    }
    message?.let { AlertDialog(onDismissRequest = { message = null }, title = { Text("StockPro") }, text = { Text(it) }, confirmButton = { TextButton(onClick = { message = null }) { Text("Aceptar") } }) }
}

@Composable
private fun PurchasesScreen(modifier: Modifier, products: List<ProductEntity>, vm: StockProViewModel) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf<ProductEntity?>(null) }; var qty by remember { mutableStateOf("1") }; var amount by remember { mutableStateOf("") }; LaunchedEffect(products) { if (selected == null && products.isNotEmpty()) { selected = products.first(); if (products.first().purchasePrice > 0) amount = (products.first().purchasePrice * (qty.toIntOrNull() ?: 1)).toString() } }; var party by remember { mutableStateOf("") }; var message by remember { mutableStateOf<String?>(null) }; var saving by remember { mutableStateOf(false) }
    val recent = remember { mutableStateListOf<BusinessStore.Entry>().apply { addAll(BusinessStore.entries(context).filter { it.type == "Compra" }.take(5)) } }
    Column(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF071014), AppBg, Color(0xFF06110D)))).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(top = 20.dp, bottom = 24.dp)) {
        PremiumHeader("Registrar compra", "Entrada de mercancía · control de costes", Icons.Default.ShoppingBag)
        StatHero("RECEPCIÓN DE MERCANCÍA", "Actualiza el inventario al recibir productos", Icons.Default.Inventory2, Color(0xFFB96A16))
        Spacer(Modifier.height(18.dp)); Text("Producto recibido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp))
        if (products.isEmpty()) EmptyState("Inventario vacío", "Crea primero un producto para registrar compras.", Icons.Default.Inventory2) else Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { products.forEach { p -> Card(colors = CardDefaults.cardColors(containerColor = if (selected?.id == p.id) PanelRaised else Panel), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().clickable { selected = p; amount = if (p.purchasePrice > 0) (p.purchasePrice * (qty.toIntOrNull() ?: 1)).toString() else "" }) { Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Inventory2, null, tint = Lime); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(p.name, fontWeight = FontWeight.SemiBold); Text("Stock actual: ${p.stockQuantity} ${p.unit}", color = Muted, style = MaterialTheme.typography.bodySmall) }; if (selected?.id == p.id) Icon(Icons.Default.CheckCircle, null, tint = Lime) } } } }
        Spacer(Modifier.height(14.dp)); Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Hairline.copy(alpha = .9f)), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(qty, { qty = it.filter(Char::isDigit); if ((selected?.purchasePrice ?: 0.0) > 0) amount = ((qty.toIntOrNull() ?: 0) * (selected?.purchasePrice ?: 0.0)).toString() }, modifier = Modifier.fillMaxWidth(), label = { Text("Cantidad recibida") }, singleLine = true)
            OutlinedTextField(amount, { amount = it.filter { c -> c.isDigit() || c == '.' || c == ',' }.replace(',', '.') }, modifier = Modifier.fillMaxWidth(), label = { Text("Coste total ($)") }, singleLine = true)
            OutlinedTextField(party, { party = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Proveedor (opcional)") }, singleLine = true)
            Button(onClick = { val p = products.firstOrNull { it.id == selected?.id }; val n = qty.toIntOrNull(); val total = amount.toDoubleOrNull(); when { p == null -> message = "Selecciona el producto recibido."; n == null || n <= 0 -> message = "Introduce una cantidad válida."; total == null || total < 0 -> message = "Introduce un coste total válido."; else -> { saving = true; vm.adjustStock(p.id, n, "Compra recibida${if (party.isBlank()) "" else " · ${party.trim()}"}") { error -> saving = false; if (error != null) message = error else { val e = BusinessStore.Entry(System.currentTimeMillis(), "Compra", p.name, n, total, party.trim(), System.currentTimeMillis()); BusinessStore.addEntry(context, e); recent.add(0, e); message = "Compra registrada y existencias incrementadas."; qty = "1"; amount = if (p.purchasePrice > 0) p.purchasePrice.toString() else ""; party = "" } } } } }, enabled = !saving && products.isNotEmpty(), modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color(0xFF201508))) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text(if (saving) "Registrando…" else "Confirmar recepción", fontWeight = FontWeight.Bold) }
        } }
        Spacer(Modifier.height(20.dp)); Text("Compras recientes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); if (recent.isEmpty()) EmptyState("Sin compras todavía", "Las recepciones registradas aparecerán aquí.", Icons.Default.ShoppingBag) else recent.take(5).forEach { EntryRow(it, Orange) }
    }
    message?.let { AlertDialog(onDismissRequest = { message = null }, title = { Text("StockPro") }, text = { Text(it) }, confirmButton = { TextButton(onClick = { message = null }) { Text("Aceptar") } }) }
}

@Composable
private fun StatHero(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(22.dp), border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = .28f)), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(color.copy(alpha = .24f), PanelRaised, Panel))).padding(19.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, color = color, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.1.sp)
                Spacer(Modifier.height(7.dp))
                Text(subtitle, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, lineHeight = 22.sp)
            }
            Spacer(Modifier.width(12.dp))
            Box(Modifier.size(48.dp).background(color.copy(alpha = .12f), RoundedCornerShape(15.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(27.dp))
            }
        }
    }
}

@Composable
private fun EntryRow(entry: BusinessStore.Entry, accent: Color) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(15.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 7.dp)) { Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(40.dp).background(accent.copy(alpha = .15f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Icon(if (entry.type == "Venta") Icons.Default.ShoppingCart else Icons.Default.ShoppingBag, null, tint = accent) }; Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(entry.item, fontWeight = FontWeight.SemiBold); Text("${entry.quantity} ud. · ${entry.party.ifBlank { entry.type }}", color = Muted, style = MaterialTheme.typography.bodySmall) }; Text(money(entry.amount), color = accent, fontWeight = FontWeight.Bold) } }
}

@Composable
private fun ContactsScreen(modifier: Modifier, type: String) {
    val context = LocalContext.current; var query by remember { mutableStateOf("") }; var refresh by remember { mutableIntStateOf(0) }; var showForm by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }; var phone by remember { mutableStateOf("") }; var email by remember { mutableStateOf("") }; var notes by remember { mutableStateOf("") }; var error by remember { mutableStateOf<String?>(null) }; var editingContact by remember { mutableStateOf<BusinessStore.Contact?>(null) }; var deleteTarget by remember { mutableStateOf<BusinessStore.Contact?>(null) }
    val all = remember(refresh, type) { BusinessStore.contacts(context, type) }; val filtered = all.filter { it.name.contains(query, true) || it.phone.contains(query, true) || it.email.contains(query, true) }
    Column(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF071014), AppBg, Color(0xFF06110D)))).padding(horizontal = 18.dp).padding(top = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("STOCKPRO  /  PREMIUM", color = Mint, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp); Spacer(Modifier.height(4.dp)); Text(if (type == "Proveedor") "Proveedores" else "Clientes", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, letterSpacing = (-.5).sp); Text("Directorio de contactos del negocio", color = Muted, style = MaterialTheme.typography.bodySmall) }; FilledIconButton(onClick = { editingContact = null; name = ""; phone = ""; email = ""; notes = ""; error = null; showForm = true }, colors = IconButtonDefaults.filledIconButtonColors(containerColor = Lime, contentColor = AppBg)) { Icon(Icons.Default.Add, "Añadir") } }
        Spacer(Modifier.height(16.dp)); OutlinedTextField(query, { query = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Buscar nombre, teléfono o correo") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true, shape = RoundedCornerShape(16.dp)); Spacer(Modifier.height(14.dp))
        if (filtered.isEmpty()) EmptyState("Todavía no hay ${if (type == "Proveedor") "proveedores" else "clientes"}", "Añade un contacto para tenerlo siempre a mano.", Icons.Default.People) else LazyColumn(verticalArrangement = Arrangement.spacedBy(9.dp), contentPadding = PaddingValues(bottom = 20.dp)) { items(filtered, key = { it.id }) { c -> Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Hairline.copy(alpha = .9f)), modifier = Modifier.fillMaxWidth()) { Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(48.dp).background(Brush.linearGradient(listOf(Lime, Color(0xFF39B76B))), CircleShape).clickable { editingContact = c; name = c.name; phone = c.phone; email = c.email; notes = c.notes; error = null; showForm = true }, contentAlignment = Alignment.Center) { Text(c.name.take(1).uppercase(), color = AppBg, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge) }; Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f).clickable { editingContact = c; name = c.name; phone = c.phone; email = c.email; notes = c.notes; error = null; showForm = true }) { Text(c.name, fontWeight = FontWeight.Bold); if (c.phone.isNotBlank()) Text(c.phone, color = Color.White.copy(alpha = .85f), style = MaterialTheme.typography.bodySmall); if (c.email.isNotBlank()) Text(c.email, color = Muted, style = MaterialTheme.typography.bodySmall); if (c.notes.isNotBlank()) Text(c.notes, color = Muted, style = MaterialTheme.typography.bodySmall) }; IconButton(onClick = { deleteTarget = c }) { Icon(Icons.Default.Delete, "Eliminar contacto", tint = Color(0xFFFF7777)) } } } } }
    }
    if (showForm) AlertDialog(onDismissRequest = { showForm = false }, title = { Text(if (editingContact == null) "Nuevo ${type.lowercase()}" else "Editar ${type.lowercase()}") }, text = { Column(Modifier.heightIn(max = 460.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(9.dp)) { OutlinedTextField(name, { name = it; error = null }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre *") }, singleLine = true); OutlinedTextField(phone, { phone = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Teléfono") }, singleLine = true); OutlinedTextField(email, { email = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Correo electrónico") }, singleLine = true); OutlinedTextField(notes, { notes = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Notas") }, minLines = 2); error?.let { Text(it, color = MaterialTheme.colorScheme.error) } } }, confirmButton = { TextButton(onClick = { if (name.isBlank()) error = "El nombre es obligatorio." else { BusinessStore.addContact(context, BusinessStore.Contact(editingContact?.id ?: System.currentTimeMillis(), type, name.trim(), phone.trim(), email.trim(), notes.trim())); refresh++; showForm = false } }) { Text("Guardar") } }, dismissButton = { TextButton(onClick = { showForm = false }) { Text("Cancelar") } })
    deleteTarget?.let { c -> AlertDialog(onDismissRequest = { deleteTarget = null }, title = { Text("Eliminar contacto") }, text = { Text("¿Quieres eliminar a ${c.name}?") }, confirmButton = { TextButton(onClick = { BusinessStore.deleteContact(context, c.id); refresh++; deleteTarget = null }) { Text("Eliminar") } }, dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("Cancelar") } }) }
}

@Composable
private fun ReportsScreen(modifier: Modifier) {
    val context = LocalContext.current; var filter by remember { mutableStateOf("Todo") }; val entries = remember { BusinessStore.entries(context) }; val sales = entries.filter { it.type == "Venta" }; val purchases = entries.filter { it.type == "Compra" }; val totalSales = sales.sumOf { it.amount }; val totalPurchases = purchases.sumOf { it.amount }; val profit = totalSales - totalPurchases
    Column(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF071014), AppBg, Color(0xFF06110D)))).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(top = 20.dp, bottom = 24.dp)) {
        PremiumHeader("Informes y estadísticas", "Resultados calculados desde operaciones registradas", Icons.Default.BarChart)
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.fillMaxWidth()) { SummaryCard("Ventas", money(totalSales), Icons.Default.ShoppingCart, Color(0xFF087BEA), Modifier.weight(1f)); SummaryCard("Compras", money(totalPurchases), Icons.Default.ShoppingBag, Orange, Modifier.weight(1f)) }
        Spacer(Modifier.height(10.dp)); SummaryCard("Balance de operaciones", money(profit), Icons.Default.TrendingUp, if (profit >= 0) Color(0xFF087D51) else Color(0xFFB73535), Modifier.fillMaxWidth())
        Spacer(Modifier.height(18.dp)); Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Actividad reciente", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Text("Importes de operaciones guardadas en este dispositivo", color = Muted, style = MaterialTheme.typography.bodySmall); Spacer(Modifier.height(18.dp)); val chart = entries.take(7).reversed(); if (chart.isEmpty()) EmptyState("Tu gráfico aparecerá aquí", "Registra ventas y compras para empezar a ver la evolución.", Icons.Default.BarChart) else Row(Modifier.fillMaxWidth().height(145.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) { chart.forEach { e -> val h = (24 + (e.amount.coerceAtMost(500.0) / 500.0 * 85).toInt()).dp; Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom, modifier = Modifier.weight(1f)) { Box(Modifier.width(18.dp).height(h).background(if (e.type == "Venta") Lime else Orange, RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))); Spacer(Modifier.height(5.dp)); Text(if (e.type == "Venta") "V" else "C", color = Muted, style = MaterialTheme.typography.labelSmall) } } } } }
        Spacer(Modifier.height(18.dp)); Text("Últimas operaciones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("Todo", "Venta", "Compra").forEach { FilterChip(selected = filter == it, onClick = { filter = it }, label = { Text(it) }) } }; Spacer(Modifier.height(8.dp)); val shown = entries.filter { filter == "Todo" || it.type == filter }; if (shown.isEmpty()) EmptyState("Sin datos para mostrar", "Los informes se actualizarán automáticamente al registrar operaciones.", Icons.Default.BarChart) else shown.take(20).forEach { EntryRow(it, if (it.type == "Venta") Lime else Orange) }
    }
}

@Composable
private fun SummaryCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp), border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = .24f))) {
        Column(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(color.copy(alpha = .15f), Panel))).padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(34.dp).background(color.copy(alpha = .14f), RoundedCornerShape(11.dp)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = color, modifier = Modifier.size(19.dp)) }
                Spacer(Modifier.weight(1f)); Box(Modifier.size(5.dp).background(color, CircleShape))
            }
            Spacer(Modifier.height(12.dp)); Text(value, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(title, color = Muted, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun AlertsScreen(modifier: Modifier, products: List<ProductEntity>, vm: StockProViewModel) {
    var editing by remember { mutableStateOf<ProductEntity?>(null) }; var expiry by remember { mutableStateOf("") }; var message by remember { mutableStateOf<String?>(null) }
    val low = products.filter { it.stockQuantity <= it.minimumStock }; val expiring = products.filter { it.expiryDate.isNotBlank() }
    Column(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF071014), AppBg, Color(0xFF06110D)))).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(top = 20.dp, bottom = 24.dp)) {
        PremiumHeader("Alertas", "Mantén bajo control el stock y los vencimientos", Icons.Default.Notifications)
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.fillMaxWidth()) { SummaryCard("Stock bajo", low.size.toString(), Icons.Default.Warning, Orange, Modifier.weight(1f)); SummaryCard("Con vencimiento", expiring.size.toString(), Icons.Default.CheckCircle, Color(0xFF087BEA), Modifier.weight(1f)) }
        Spacer(Modifier.height(18.dp)); Text("Stock bajo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp))
        if (low.isEmpty()) EmptyState("Todo bajo control", "No hay productos en el umbral de stock bajo.", Icons.Default.CheckCircle) else low.forEach { p -> Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) { Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Warning, null, tint = if (p.stockQuantity == 0) Color(0xFFFF6B6B) else Orange); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(p.name, fontWeight = FontWeight.SemiBold); Text("${p.stockQuantity} ${p.unit} · mínimo ${p.minimumStock}", color = Muted, style = MaterialTheme.typography.bodySmall) }; TextButton(onClick = { editing = p; expiry = p.expiryDate }) { Text("Vencimiento", color = Lime) } } } }
        Spacer(Modifier.height(14.dp)); Text("Fechas de vencimiento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Text("Añade una fecha en formato AAAA-MM-DD a cada producto que lo necesite.", color = Muted, style = MaterialTheme.typography.bodySmall); Spacer(Modifier.height(8.dp))
        if (expiring.isEmpty()) Text("No se han configurado fechas de vencimiento.", color = Muted, modifier = Modifier.padding(vertical = 12.dp)) else expiring.forEach { p -> val date = p.expiryDate; val days = runCatching { ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(date)).toInt() }.getOrNull(); val status = when { days == null -> "Fecha no válida · $date"; days < 0 -> "Vencido hace ${-days} días"; days == 0 -> "Vence hoy"; else -> "Vence en $days días" }; val tone = if (days != null && days <= 3) Color(0xFFFF7777) else Lime; Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 7.dp).clickable { editing = p; expiry = date }) { Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Notifications, null, tint = tone); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(p.name, fontWeight = FontWeight.SemiBold); Text(status, color = tone, style = MaterialTheme.typography.bodySmall) }; Text("Editar", color = Muted, style = MaterialTheme.typography.labelSmall) } } }
    }
    editing?.let { p -> AlertDialog(onDismissRequest = { editing = null }, title = { Text("Vencimiento · ${p.name}") }, text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { Text("Formato AAAA-MM-DD. Deja vacío para eliminar la fecha.", color = Muted, style = MaterialTheme.typography.bodySmall); OutlinedTextField(expiry, { expiry = it.filter { c -> c.isDigit() || c == '-' }.take(10) }, label = { Text("Fecha de vencimiento") }, placeholder = { Text("2026-12-31") }, singleLine = true) } }, confirmButton = { TextButton(onClick = { vm.saveExpiry(p.id, expiry) { error -> if (error != null) message = error else editing = null } }) { Text("Guardar") } }, dismissButton = { TextButton(onClick = { editing = null }) { Text("Cancelar") } })
    }
    message?.let { AlertDialog(onDismissRequest = { message = null }, title = { Text("Fecha no guardada") }, text = { Text(it) }, confirmButton = { TextButton(onClick = { message = null }) { Text("Aceptar") } }) }
}

@Composable
private fun SettingsScreen(modifier: Modifier) {
    val context = LocalContext.current; var notifications by remember { mutableStateOf(BusinessStore.setting(context, "notifications", "true") == "true") }; var saved by remember { mutableStateOf(false) }
    Column(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF071014), AppBg, Color(0xFF06110D)))).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(top = 20.dp, bottom = 24.dp)) {
        PremiumHeader("Configuración", "Personaliza tu espacio de trabajo", Icons.Default.Settings)
        SettingsGroup("Cuenta y seguridad", "Preferencias guardadas localmente en este dispositivo") { SettingRow(Icons.Default.People, "Perfil del negocio", "Datos de cuenta local", { }, false) }
        Spacer(Modifier.height(12.dp)); SettingsGroup("Notificaciones", "Controla los avisos dentro del panel principal") { SettingRow(Icons.Default.Notifications, "Alertas de stock en Inicio", "Mostrar productos que requieren atención", { notifications = it; BusinessStore.setSetting(context, "notifications", it.toString()); saved = true }, true, notifications); Text("Las notificaciones del sistema y el sonido de operaciones aún no están conectados.", color = Muted, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 5.dp)) }
        Spacer(Modifier.height(12.dp)); SettingsGroup("Apariencia e idioma", "Preferencias activas de esta versión") { SettingRow(Icons.Default.Settings, "Tema", "Oscuro premium verde petróleo", {}, false); SettingRow(Icons.Default.People, "Idioma", "Español", {}, false); Text("La selección de temas adicionales y la traducción completa se integrarán en una siguiente fase.", color = Muted, style = MaterialTheme.typography.bodySmall) }
        Spacer(Modifier.height(12.dp)); SettingsGroup("Acerca de StockPro", "Gestión comercial sin conexión") { Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Inventory2, null, tint = Lime); Spacer(Modifier.width(12.dp)); Column { Text("StockPro Premium UI", fontWeight = FontWeight.SemiBold); Text("Versión de desarrollo · datos locales", color = Muted, style = MaterialTheme.typography.bodySmall) } } }
        if (saved) { Spacer(Modifier.height(10.dp)); Text("✓ Preferencia guardada en este dispositivo", color = Lime, style = MaterialTheme.typography.bodySmall) }
    }
}

@Composable
private fun SettingsGroup(title: String, subtitle: String, content: @Composable ColumnScope.() -> Unit) { Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Hairline.copy(alpha = .9f)), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp)) { Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, letterSpacing = (-.2).sp); Spacer(Modifier.height(3.dp)); Text(subtitle, color = Muted, style = MaterialTheme.typography.bodySmall, lineHeight = 17.sp); Spacer(Modifier.height(12.dp)); content() } } }

@Composable
private fun SettingRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onToggle: (Boolean) -> Unit, isSwitch: Boolean, checked: Boolean = false, trailing: Boolean = false) { Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = Lime); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.SemiBold); Text(subtitle, color = Muted, style = MaterialTheme.typography.bodySmall) }; if (isSwitch) Switch(checked = checked, onCheckedChange = onToggle, colors = SwitchDefaults.colors(checkedThumbColor = AppBg, checkedTrackColor = Lime)) else if (trailing) Text("›", color = Lime, style = MaterialTheme.typography.headlineMedium) } }

@Composable
private fun BackupScreen(modifier: Modifier, products: List<ProductEntity>) {
    val context = LocalContext.current; var info by remember { mutableStateOf<String?>(null) }
    Column(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF071014), AppBg, Color(0xFF06110D)))).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(top = 20.dp, bottom = 24.dp)) {
        PremiumHeader("Copias de seguridad", "Protege la información de tu negocio", Icons.Default.CloudUpload)
        StatHero("EXPORTACIÓN LOCAL", "Comparte un resumen de operaciones y contactos", Icons.Default.CloudUpload, Color(0xFF126F55))
        Spacer(Modifier.height(16.dp)); SettingsGroup("Antes de exportar", "Este archivo incluye los registros comerciales almacenados por StockPro.") { Text("• Historial de ventas y compras", color = Color.White.copy(alpha = .85f)); Text("• Contactos de clientes y proveedores", color = Color.White.copy(alpha = .85f)); Text("• Importes y fechas de las operaciones", color = Color.White.copy(alpha = .85f)); Spacer(Modifier.height(6.dp)); Text("Nota: se incluyen productos y datos comerciales en JSON; no es una copia integral restaurable de la base Room ni del historial detallado de movimientos.", color = Orange, style = MaterialTheme.typography.bodySmall) }
        Spacer(Modifier.height(14.dp)); Button(onClick = { val text = BusinessStore.export(context, products); val intent = Intent(Intent.ACTION_SEND).apply { type = "application/json"; putExtra(Intent.EXTRA_SUBJECT, "StockPro · resumen de datos"); putExtra(Intent.EXTRA_TEXT, text) }; context.startActivity(Intent.createChooser(intent, "Exportar datos de StockPro")); info = "Se abrió el menú para compartir el resumen JSON." }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = AppBg)) { Icon(Icons.Default.Share, null); Spacer(Modifier.width(8.dp)); Text("Exportar resumen JSON", fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(12.dp)); OutlinedButton(onClick = { info = "Para una copia integral y restaurable, falta conectar la exportación con la base de datos Room y añadir un flujo de importación validado." }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Lime)) { Text("Información sobre restauración") }
    }
    info?.let { AlertDialog(onDismissRequest = { info = null }, title = { Text("Copias de seguridad") }, text = { Text(it) }, confirmButton = { TextButton(onClick = { info = null }) { Text("Entendido") } }) }
}

@Composable
private fun LicenseScreen(modifier: Modifier) {
    Column(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF071014), AppBg, Color(0xFF06110D)))).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(top = 20.dp, bottom = 24.dp)) {
        PremiumHeader("Tu licencia", "Estado de acceso a StockPro", Icons.Default.CheckCircle)
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0C653F)), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.CheckCircle, null, tint = Lime, modifier = Modifier.size(48.dp)); Spacer(Modifier.height(10.dp)); Text("EDICIÓN LOCAL", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold); Text("Inventario disponible sin conexión", color = Color.White.copy(alpha = .8f), textAlign = androidx.compose.ui.text.style.TextAlign.Center) } }
        Spacer(Modifier.height(14.dp)); SettingsGroup("Información de licencia", "Esta compilación no tiene todavía un servidor de activación conectado.") { SettingRow(Icons.Default.CheckCircle, "Modo", "Desarrollo local", {}, false); SettingRow(Icons.Default.Inventory2, "Dispositivo", "Este teléfono", {}, false); SettingRow(Icons.Default.Notifications, "Validación online", "No configurada", {}, false) }
        Spacer(Modifier.height(14.dp)); Text("No se muestra una fecha de expiración ficticia. La renovación y el historial real requieren integrar un proveedor de licencias o un backend propio.", color = Muted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun CatalogScreen(modifier: Modifier, products: List<ProductEntity>) {
    val context = LocalContext.current; var query by remember { mutableStateOf("") }; val filtered = products.filter { it.name.contains(query, true) }
    Column(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF071014), AppBg, Color(0xFF06110D)))).padding(horizontal = 18.dp).padding(top = 20.dp)) {
        PremiumHeader("Catálogo", "Prepara una lista de productos para compartir", Icons.Default.ShoppingBag)
        OutlinedTextField(query, { query = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Buscar producto…") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true, shape = RoundedCornerShape(16.dp)); Spacer(Modifier.height(12.dp))
        if (filtered.isEmpty()) EmptyState("Catálogo vacío", "Añade productos en Inventario para mostrarlos aquí.", Icons.Default.Inventory2) else LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 12.dp)) { items(filtered, key = { it.id }) { p -> Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) { Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(42.dp).background(Color(0xFF173B35), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Inventory2, null, tint = Lime) }; Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(p.name, fontWeight = FontWeight.SemiBold); Text("${p.stockQuantity} ${p.unit} disponibles", color = Muted, style = MaterialTheme.typography.bodySmall) }; if (p.stockQuantity > 0) Text("Disponible", color = Lime, style = MaterialTheme.typography.labelSmall) else Text("Agotado", color = Color(0xFFFF6B6B), style = MaterialTheme.typography.labelSmall) } } } }
        Button(onClick = { val text = buildString { appendLine("CATÁLOGO STOCKPRO"); appendLine("Productos disponibles"); appendLine(); filtered.filter { it.stockQuantity > 0 }.forEach { appendLine("${it.name} — ${it.stockQuantity} ${it.unit}") } }; val intent = Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_SUBJECT, "Catálogo de productos"); putExtra(Intent.EXTRA_TEXT, text) }; context.startActivity(Intent.createChooser(intent, "Compartir catálogo")) }, enabled = filtered.any { it.stockQuantity > 0 }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD49B18), contentColor = Color(0xFF17140A))) { Icon(Icons.Default.Share, null); Spacer(Modifier.width(8.dp)); Text("Compartir catálogo", fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun EmptyState(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 30.dp), horizontalAlignment = Alignment.CenterHorizontally) { Box(Modifier.size(58.dp).background(Color(0xFF15372F), CircleShape), contentAlignment = Alignment.Center) { Icon(icon, null, tint = Lime, modifier = Modifier.size(28.dp)) }; Spacer(Modifier.height(10.dp)); Text(title, fontWeight = FontWeight.Bold); Spacer(Modifier.height(4.dp)); Text(subtitle, color = Muted, style = MaterialTheme.typography.bodySmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center) }
}

@Composable
private fun PlaceholderScreen(modifier: Modifier, title: String, description: String) {
    Column(modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Inventory2, null, tint = Lime, modifier = Modifier.size(54.dp))
        Spacer(Modifier.height(18.dp))
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(description, color = Muted, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(Modifier.height(18.dp))
        Text("Módulo pendiente de desarrollo", color = Lime, style = MaterialTheme.typography.labelLarge)
    }
}
