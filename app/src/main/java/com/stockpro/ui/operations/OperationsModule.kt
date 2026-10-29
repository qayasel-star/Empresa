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

@Composable
internal fun AlertsScreen(modifier: Modifier, products: List<ProductEntity>, vm: StockProViewModel) {
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
    editing?.let { p -> AlertDialog(containerColor = PanelRaised, titleContentColor = Color.White, textContentColor = Muted, onDismissRequest = { editing = null }, title = { Text("Vencimiento · ${p.name}") }, text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { Text("Formato AAAA-MM-DD. Deja vacío para eliminar la fecha.", color = Muted, style = MaterialTheme.typography.bodySmall); OutlinedTextField(expiry, { expiry = it.filter { c -> c.isDigit() || c == '-' }.take(10) }, label = { Text("Fecha de vencimiento") }, placeholder = { Text("2026-12-31") }, singleLine = true) } }, confirmButton = { TextButton(onClick = { vm.saveExpiry(p.id, expiry) { error -> if (error != null) message = error else editing = null } }) { Text("Guardar") } }, dismissButton = { TextButton(onClick = { editing = null }) { Text("Cancelar") } })
    }
    message?.let { AlertDialog(containerColor = PanelRaised, titleContentColor = Color.White, textContentColor = Muted, onDismissRequest = { message = null }, title = { Text("Fecha no guardada") }, text = { Text(it) }, confirmButton = { TextButton(onClick = { message = null }) { Text("Aceptar") } }) }
}

@Composable
internal fun SettingsScreen(modifier: Modifier) {
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
internal fun SettingsGroup(title: String, subtitle: String, content: @Composable ColumnScope.() -> Unit) { Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Hairline.copy(alpha = .9f)), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp)) { Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, letterSpacing = (-.2).sp); Spacer(Modifier.height(3.dp)); Text(subtitle, color = Muted, style = MaterialTheme.typography.bodySmall, lineHeight = 17.sp); Spacer(Modifier.height(12.dp)); content() } } }

@Composable
internal fun SettingRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onToggle: (Boolean) -> Unit, isSwitch: Boolean, checked: Boolean = false, trailing: Boolean = false) { Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = Lime); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.SemiBold); Text(subtitle, color = Muted, style = MaterialTheme.typography.bodySmall) }; if (isSwitch) Switch(checked = checked, onCheckedChange = onToggle, colors = SwitchDefaults.colors(checkedThumbColor = AppBg, checkedTrackColor = Lime)) else if (trailing) Text("›", color = Lime, style = MaterialTheme.typography.headlineMedium) } }

@Composable
internal fun BackupScreen(modifier: Modifier, products: List<ProductEntity>) {
    val context = LocalContext.current; var info by remember { mutableStateOf<String?>(null) }
    Column(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF071014), AppBg, Color(0xFF06110D)))).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(top = 20.dp, bottom = 24.dp)) {
        PremiumHeader("Copias de seguridad", "Protege la información de tu negocio", Icons.Default.CloudUpload)
        StatHero("EXPORTACIÓN LOCAL", "Comparte un resumen de operaciones y contactos", Icons.Default.CloudUpload, Color(0xFF126F55))
        Spacer(Modifier.height(16.dp)); SettingsGroup("Antes de exportar", "Este archivo incluye los registros comerciales almacenados por StockPro.") { Text("• Historial de ventas y compras", color = Color.White.copy(alpha = .85f)); Text("• Contactos de clientes y proveedores", color = Color.White.copy(alpha = .85f)); Text("• Importes y fechas de las operaciones", color = Color.White.copy(alpha = .85f)); Spacer(Modifier.height(6.dp)); Text("Nota: se incluyen productos y datos comerciales en JSON; no es una copia integral restaurable de la base Room ni del historial detallado de movimientos.", color = Orange, style = MaterialTheme.typography.bodySmall) }
        Spacer(Modifier.height(14.dp)); Button(onClick = { val text = BusinessStore.export(context, products); val intent = Intent(Intent.ACTION_SEND).apply { type = "application/json"; putExtra(Intent.EXTRA_SUBJECT, "StockPro · resumen de datos"); putExtra(Intent.EXTRA_TEXT, text) }; context.startActivity(Intent.createChooser(intent, "Exportar datos de StockPro")); info = "Se abrió el menú para compartir el resumen JSON." }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = AppBg)) { Icon(Icons.Default.Share, null); Spacer(Modifier.width(8.dp)); Text("Exportar resumen JSON", fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(12.dp)); OutlinedButton(onClick = { info = "Para una copia integral y restaurable, falta conectar la exportación con la base de datos Room y añadir un flujo de importación validado." }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Lime)) { Text("Información sobre restauración") }
    }
    info?.let { AlertDialog(containerColor = PanelRaised, titleContentColor = Color.White, textContentColor = Muted, onDismissRequest = { info = null }, title = { Text("Copias de seguridad") }, text = { Text(it) }, confirmButton = { TextButton(onClick = { info = null }) { Text("Entendido") } }) }
}

@Composable
internal fun LicenseScreen(modifier: Modifier) {
    Column(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF071014), AppBg, Color(0xFF06110D)))).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(top = 20.dp, bottom = 24.dp)) {
        PremiumHeader("Tu licencia", "Estado de acceso a StockPro", Icons.Default.CheckCircle)
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0C653F)), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.CheckCircle, null, tint = Lime, modifier = Modifier.size(48.dp)); Spacer(Modifier.height(10.dp)); Text("EDICIÓN LOCAL", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold); Text("Inventario disponible sin conexión", color = Color.White.copy(alpha = .8f), textAlign = androidx.compose.ui.text.style.TextAlign.Center) } }
        Spacer(Modifier.height(14.dp)); SettingsGroup("Información de licencia", "Esta compilación no tiene todavía un servidor de activación conectado.") { SettingRow(Icons.Default.CheckCircle, "Modo", "Desarrollo local", {}, false); SettingRow(Icons.Default.Inventory2, "Dispositivo", "Este teléfono", {}, false); SettingRow(Icons.Default.Notifications, "Validación online", "No configurada", {}, false) }
        Spacer(Modifier.height(14.dp)); Text("No se muestra una fecha de expiración ficticia. La renovación y el historial real requieren integrar un proveedor de licencias o un backend propio.", color = Muted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
internal fun CatalogScreen(modifier: Modifier, products: List<ProductEntity>) {
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
internal fun EmptyState(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 30.dp), horizontalAlignment = Alignment.CenterHorizontally) { Box(Modifier.size(58.dp).background(Color(0xFF15372F), CircleShape), contentAlignment = Alignment.Center) { Icon(icon, null, tint = Lime, modifier = Modifier.size(28.dp)) }; Spacer(Modifier.height(10.dp)); Text(title, fontWeight = FontWeight.Bold); Spacer(Modifier.height(4.dp)); Text(subtitle, color = Muted, style = MaterialTheme.typography.bodySmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center) }
}

@Composable
internal fun PlaceholderScreen(modifier: Modifier, title: String, description: String) {
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
