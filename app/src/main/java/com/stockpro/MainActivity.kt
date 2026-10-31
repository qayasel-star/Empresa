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
        window.statusBarColor = android.graphics.Color.rgb(2, 8, 7)
        window.navigationBarColor = android.graphics.Color.rgb(2, 8, 7)
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        window.decorView.systemUiVisibility = 0
        setContent { StockProTheme { StockProApp() } }
    }
}

internal val Lime = Color(0xFF35F59B)
internal val Mint = Color(0xFF20DCC6)
internal val Muted = Color(0xFF91A7A8)
internal val AppBg = Color(0xFF030908)
internal val Panel = Color(0xFF091713)
internal val PanelRaised = Color(0xFF102A25)
internal val Blue = Color(0xFF46BDEB)
internal val Orange = Color(0xFFFFB45B)
internal val Hairline = Color(0xFF245247)

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
    val motion = rememberInfiniteTransition(label = "stockpro_cinematic_intro")
    val pulse by motion.animateFloat(.92f, 1.08f, infiniteRepeatable(tween(1450, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "logoPulse")
    val orbit by motion.animateFloat(0f, 360f, infiniteRepeatable(tween(7200, easing = androidx.compose.animation.core.LinearEasing)), label = "orbit")
    val sweep by motion.animateFloat(-.45f, 1.45f, infiniteRepeatable(tween(1900, easing = FastOutSlowInEasing), RepeatMode.Restart), label = "lightSweep")
    val progress by motion.animateFloat(.08f, .96f, infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Restart), label = "progress")
    val breathe by motion.animateFloat(.35f, .95f, infiniteRepeatable(tween(1100), RepeatMode.Reverse), label = "ambientGlow")
    val emerald = Color(0xFF00FF91)
    val mint = Color(0xFF25F5D0)
    val deep = Color(0xFF020707)

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF020706), Color(0xFF031510), Color(0xFF010404))))) {
        // Animated cinematic environment: perspective grid, moving particles, light field and orbit rings.
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height
            val cx = w * .5f; val cy = h * .355f
            drawRect(Brush.radialGradient(listOf(Color(0xFF0B6C45).copy(alpha = .25f), Color.Transparent), Offset(cx, cy), w * .72f))
            drawRect(Brush.radialGradient(listOf(Color(0xFF00D9C0).copy(alpha = .10f), Color.Transparent), Offset(cx, h * .72f), w * .68f))
            // Distant perspective lines converge on the luminous horizon.
            val horizonY = h * .66f
            for (i in 0..16) {
                val x = w * i / 16f
                drawLine(Color(0xFF20DCA0).copy(alpha = .055f), Offset(cx, horizonY), Offset(x, h), strokeWidth = 1f)
            }
            for (i in 1..9) {
                val t = i / 9f
                val y = horizonY + (h - horizonY) * t * t
                drawLine(Color(0xFF20DCA0).copy(alpha = .07f * t), Offset(0f, y), Offset(w, y), strokeWidth = 1f)
            }
            // Horizon beam
            drawLine(Brush.horizontalGradient(listOf(Color.Transparent, emerald.copy(alpha = .12f), mint.copy(alpha = .72f), emerald.copy(alpha = .12f), Color.Transparent)), Offset(w * .06f, horizonY), Offset(w * .94f, horizonY), strokeWidth = 2.4f)
            // Particles follow gently shifting paths.
            for (i in 0..39) {
                val seed = i * 37.7f
                val x = ((seed + w * ((i % 7) / 7f) + sweep * w * .13f) % w)
                val y = h * (.10f + ((i * 19 % 73) / 100f))
                val r = if (i % 6 == 0) 2.5f else 1.15f
                drawCircle(if (i % 3 == 0) mint.copy(alpha = .45f) else emerald.copy(alpha = .32f), r, Offset(x, y))
                if (i % 9 == 0) drawLine(emerald.copy(alpha = .18f), Offset(x - 8f, y), Offset(x + 8f, y), strokeWidth = 1f)
            }
            // Rotating dual energy rings around the brand emblem.
            val rx = w * .31f; val ry = h * .045f
            rotate(orbit, pivot = Offset(cx, cy + h * .015f)) {
                drawArc(Brush.sweepGradient(listOf(Color.Transparent, emerald.copy(alpha = .9f), mint.copy(alpha = .9f), Color.Transparent)), 205f, 118f, false, Offset(cx - rx, cy - ry), androidx.compose.ui.geometry.Size(rx * 2, ry * 2), style = Stroke(width = 3.2f))
            }
            rotate(-orbit * .72f, pivot = Offset(cx, cy + h * .015f)) {
                drawArc(Brush.sweepGradient(listOf(Color.Transparent, mint.copy(alpha = .75f), emerald.copy(alpha = .25f), Color.Transparent)), 12f, 132f, false, Offset(cx - rx * 1.12f, cy - ry * 1.25f), androidx.compose.ui.geometry.Size(rx * 2.24f, ry * 2.5f), style = Stroke(width = 1.5f))
            }
            // Neon cube mark built from vector paths; layered strokes create a luminous 3D edge.
            val unit = w * .145f * pulse
            val top = Offset(cx, cy - unit * 1.12f)
            val left = Offset(cx - unit, cy - unit * .53f)
            val right = Offset(cx + unit, cy - unit * .53f)
            val mid = Offset(cx, cy + unit * .03f)
            val leftBottom = Offset(cx - unit, cy + unit * .65f)
            val rightBottom = Offset(cx + unit, cy + unit * .65f)
            val bottom = Offset(cx, cy + unit * 1.18f)
            fun edge(points: List<Offset>, color: Color, width: Float) {
                val path = Path().apply { moveTo(points.first().x, points.first().y); points.drop(1).forEach { lineTo(it.x, it.y) } }
                drawPath(path, color.copy(alpha = .12f), style = Stroke(width = width * 4.8f))
                drawPath(path, color.copy(alpha = .35f), style = Stroke(width = width * 2.2f))
                drawPath(path, color, style = Stroke(width = width))
            }
            edge(listOf(top, left, mid, right, top), mint, 3.4f)
            edge(listOf(left, leftBottom, bottom, rightBottom, right), emerald, 3.4f)
            edge(listOf(mid, Offset(cx, cy + unit * .72f)), mint, 2.6f)
            edge(listOf(left, Offset(cx - unit * .40f, cy - unit * .28f), Offset(cx - unit * .40f, cy + unit * .34f), mid), emerald, 2.8f)
            edge(listOf(mid, Offset(cx + unit * .42f, cy - unit * .28f), Offset(cx + unit * .42f, cy + unit * .34f), right), mint, 2.8f)
            // Moving diagonal highlight across the emblem.
            val highlightX = cx + (sweep - .5f) * unit * 2.4f
            drawLine(Brush.verticalGradient(listOf(Color.Transparent, Color.White.copy(alpha = .7f), Color.Transparent), startY = cy - unit, endY = cy + unit), Offset(highlightX, cy - unit), Offset(highlightX + unit * .24f, cy + unit), strokeWidth = 2.2f)
            drawCircle(emerald.copy(alpha = breathe * .13f), unit * 1.85f, Offset(cx, cy))
        }

        Column(Modifier.fillMaxSize().padding(horizontal = 30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.weight(1.12f))
            // Reserve space for the animated vector emblem above; logo is kept as crisp native text.
            Spacer(Modifier.height(210.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Stock", color = Color.White, fontSize = 39.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1.5).sp)
                Text("Pro", color = emerald, fontSize = 39.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1.5).sp)
            }
            Text("P R E M I U M   E D I T I O N", color = mint, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 3.2.sp)
            Spacer(Modifier.height(24.dp))
            Text("Tu negocio en buenas manos", color = Color.White.copy(alpha = .94f), fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Text("Control inteligente. ", color = Color(0xFFB8D1C8), fontSize = 13.sp)
                Text("Negocio en equilibrio.", color = emerald, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.weight(.75f))
            // Custom glowing progress rail.
            Box(Modifier.fillMaxWidth(.66f).height(4.dp).background(Color(0xFF16382D), RoundedCornerShape(50))) {
                Box(Modifier.fillMaxWidth(progress).fillMaxHeight().background(Brush.horizontalGradient(listOf(emerald, mint)), RoundedCornerShape(50)))
            }
            Spacer(Modifier.height(14.dp))
            Text("INICIALIZANDO TU ESPACIO DE TRABAJO", color = Color(0xFF80A99A), fontSize = 9.sp, letterSpacing = 1.8.sp)
            Spacer(Modifier.height(28.dp))
        }
    }
}

internal enum class Tab(val title: String) { HOME("Inicio"), INVENTORY("Inventario"), SALES("Ventas"), PURCHASES("Compras"), MORE("Más") }

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
            NavigationBar(modifier = Modifier.border(width = 1.dp, color = Color(0xFF1B5745), shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)), containerColor = Color(0xF506100D), contentColor = Lime, tonalElevation = 0.dp, windowInsets = NavigationBarDefaults.windowInsets) {
                val tabs = listOf(Tab.HOME, Tab.INVENTORY, Tab.SALES, Tab.PURCHASES, Tab.MORE)
                val icons = listOf(Icons.Default.Home, Icons.Default.Inventory2, Icons.Default.ShoppingCart, Icons.Default.ArrowUpward, Icons.Default.MoreHoriz)
                tabs.forEachIndexed { i, item -> NavigationBarItem(selected = tab == item, onClick = { tab = item; module = item.title }, icon = { Icon(icons[i], contentDescription = item.title) }, label = { Text(item.title, maxLines = 1, fontSize = 10.sp, fontWeight = if (tab == item) FontWeight.Bold else FontWeight.Medium) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = Lime, selectedTextColor = Lime, indicatorColor = Color(0xFF123B30), unselectedIconColor = Muted, unselectedTextColor = Muted)) }
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

