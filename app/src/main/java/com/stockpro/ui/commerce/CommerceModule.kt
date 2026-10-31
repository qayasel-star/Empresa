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
internal fun SalesScreen(modifier: Modifier, products: List<ProductEntity>, vm: StockProViewModel) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf<ProductEntity?>(null) }
    var qty by remember { mutableStateOf("1") }
    var amount by remember { mutableStateOf("") }
    LaunchedEffect(products) { if (selected == null && products.isNotEmpty()) { selected = products.first(); if (products.first().salePrice > 0) amount = (products.first().salePrice * (qty.toIntOrNull() ?: 1)).toString() } }
    var party by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    val recent = remember { mutableStateListOf<BusinessStore.Entry>().apply { addAll(BusinessStore.entries(context).filter { it.type == "Venta" }.take(5)) } }
    Column(modifier.fillMaxSize().stockProBackdrop().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(top = 20.dp, bottom = 24.dp)) {
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
        else Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            products.forEach { p ->
                val active = selected?.id == p.id
                Card(colors = CardDefaults.cardColors(containerColor = if (active) PanelRaised else Panel), shape = RoundedCornerShape(19.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Brush.linearGradient(listOf(if (active) Lime.copy(alpha = .72f) else Hairline, if (active) Mint.copy(alpha = .20f) else Color.Transparent))), modifier = Modifier.fillMaxWidth().clickable { selected = p; amount = if (p.salePrice > 0) (p.salePrice * (qty.toIntOrNull() ?: 1)).toString() else "" }) {
                    Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        ProductImageThumb(p.imageKey, Modifier.size(54.dp)); Spacer(Modifier.width(11.dp))
                        Column(Modifier.weight(1f)) { Text(p.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis); Text("${money(p.salePrice)} · Stock ${p.stockQuantity}", color = Muted, style = MaterialTheme.typography.bodySmall) }
                        if (active) Icon(Icons.Default.CheckCircle, null, tint = Lime)
                    }
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Mint.copy(alpha = .24f)), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("2 · Detalles de la operación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = qty, onValueChange = { qty = it.filter(Char::isDigit); if ((selected?.salePrice ?: 0.0) > 0) amount = ((qty.toIntOrNull() ?: 0) * (selected?.salePrice ?: 0.0)).toString() }, modifier = Modifier.fillMaxWidth(), label = { Text("Cantidad") }, singleLine = true)
                OutlinedTextField(value = amount, onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' || c == ',' }.replace(',', '.') }, modifier = Modifier.fillMaxWidth(), label = { Text("Importe total ($)") }, singleLine = true, leadingIcon = { Icon(Icons.Default.TrendingUp, null, tint = Lime) })
                OutlinedTextField(value = party, onValueChange = { party = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Cliente (opcional)") }, singleLine = true)
                Row(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Color(0xFF103A2B), Color(0xFF0C2023))), RoundedCornerShape(15.dp)).padding(horizontal = 14.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Total de venta", color = Muted, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                    Text(money(amount.toDoubleOrNull() ?: 0.0), color = Lime, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                }
                Button(onClick = {
                    val p = products.firstOrNull { it.id == selected?.id }; val n = qty.toIntOrNull(); val total = amount.toDoubleOrNull()
                    when { p == null -> message = "Selecciona un producto."; n == null || n <= 0 -> message = "Introduce una cantidad válida."; n > p.stockQuantity -> message = "No hay existencias suficientes."; total == null || total < 0 -> message = "Introduce un importe total válido."; else -> { saving = true; vm.adjustStock(p.id, -n, "Venta registrada${if (party.isBlank()) "" else " · ${party.trim()}"}") { error -> saving = false; if (error != null) message = error else { val entry = BusinessStore.Entry(System.currentTimeMillis(), "Venta", p.name, n, total, party.trim(), System.currentTimeMillis()); BusinessStore.addEntry(context, entry); recent.add(0, entry); message = "Venta registrada y existencias actualizadas."; qty = "1"; amount = if (p.salePrice > 0) p.salePrice.toString() else ""; party = "" } } } }
                }, enabled = !saving && products.isNotEmpty(), modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = Color(0xFF102218))) { Icon(Icons.Default.CheckCircle, null); Spacer(Modifier.width(8.dp)); Text(if (saving) "Registrando…" else "Confirmar venta", fontWeight = FontWeight.Bold) }
            }
        }
        Spacer(Modifier.height(20.dp)); Text("Ventas recientes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp))
        if (recent.isEmpty()) EmptyState("Sin ventas todavía", "Las ventas confirmadas aparecerán aquí.", Icons.Default.ReceiptLong) else recent.take(5).forEach { EntryRow(it, Lime) }
    }
    message?.let { AlertDialog(containerColor = PanelRaised, titleContentColor = Color.White, textContentColor = Muted, onDismissRequest = { message = null }, title = { Text("StockPro") }, text = { Text(it) }, confirmButton = { TextButton(onClick = { message = null }) { Text("Aceptar") } }) }
}

@Composable
internal fun PurchasesScreen(modifier: Modifier, products: List<ProductEntity>, vm: StockProViewModel) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf<ProductEntity?>(null) }; var qty by remember { mutableStateOf("1") }; var amount by remember { mutableStateOf("") }; LaunchedEffect(products) { if (selected == null && products.isNotEmpty()) { selected = products.first(); if (products.first().purchasePrice > 0) amount = (products.first().purchasePrice * (qty.toIntOrNull() ?: 1)).toString() } }; var party by remember { mutableStateOf("") }; var message by remember { mutableStateOf<String?>(null) }; var saving by remember { mutableStateOf(false) }
    val recent = remember { mutableStateListOf<BusinessStore.Entry>().apply { addAll(BusinessStore.entries(context).filter { it.type == "Compra" }.take(5)) } }
    Column(modifier.fillMaxSize().stockProBackdrop().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(top = 20.dp, bottom = 24.dp)) {
        PremiumHeader("Registrar compra", "Entrada de mercancía · control de costes", Icons.Default.ShoppingBag)
        StatHero("RECEPCIÓN DE MERCANCÍA", "Actualiza el inventario al recibir productos", Icons.Default.Inventory2, Color(0xFFB96A16))
        Spacer(Modifier.height(18.dp)); Text("Producto recibido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp))
        if (products.isEmpty()) EmptyState("Inventario vacío", "Crea primero un producto para registrar compras.", Icons.Default.Inventory2) else Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { products.forEach { p -> Card(colors = CardDefaults.cardColors(containerColor = if (selected?.id == p.id) PanelRaised else Panel), shape = RoundedCornerShape(18.dp), border = androidx.compose.foundation.BorderStroke(1.dp, if (selected?.id == p.id) Lime.copy(alpha = .62f) else Hairline), modifier = Modifier.fillMaxWidth().clickable { selected = p; amount = if (p.purchasePrice > 0) (p.purchasePrice * (qty.toIntOrNull() ?: 1)).toString() else "" }) { Row(Modifier.padding(11.dp), verticalAlignment = Alignment.CenterVertically) { ProductImageThumb(p.imageKey, Modifier.size(54.dp)); Spacer(Modifier.width(11.dp)); Column(Modifier.weight(1f)) { Text(p.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis); Text("${money(p.purchasePrice)} · Stock actual: ${p.stockQuantity} ${p.unit}", color = Muted, style = MaterialTheme.typography.bodySmall) }; if (selected?.id == p.id) Icon(Icons.Default.CheckCircle, null, tint = Lime) } } } }
        Spacer(Modifier.height(14.dp)); Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(22.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Mint.copy(alpha = .25f)), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Detalles de recepción", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(qty, { qty = it.filter(Char::isDigit); if ((selected?.purchasePrice ?: 0.0) > 0) amount = ((qty.toIntOrNull() ?: 0) * (selected?.purchasePrice ?: 0.0)).toString() }, modifier = Modifier.fillMaxWidth(), label = { Text("Cantidad recibida") }, singleLine = true)
            OutlinedTextField(amount, { amount = it.filter { c -> c.isDigit() || c == '.' || c == ',' }.replace(',', '.') }, modifier = Modifier.fillMaxWidth(), label = { Text("Coste total ($)") }, singleLine = true)
            OutlinedTextField(party, { party = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Proveedor (opcional)") }, singleLine = true)
            Row(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Color(0xFF103A2B), Color(0xFF0C2023))), RoundedCornerShape(15.dp)).padding(horizontal = 14.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Total de compra", color = Muted, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                Text(money(amount.toDoubleOrNull() ?: 0.0), color = Lime, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            }
            Button(onClick = { val p = products.firstOrNull { it.id == selected?.id }; val n = qty.toIntOrNull(); val total = amount.toDoubleOrNull(); when { p == null -> message = "Selecciona el producto recibido."; n == null || n <= 0 -> message = "Introduce una cantidad válida."; total == null || total < 0 -> message = "Introduce un coste total válido."; else -> { saving = true; vm.adjustStock(p.id, n, "Compra recibida${if (party.isBlank()) "" else " · ${party.trim()}"}") { error -> saving = false; if (error != null) message = error else { val e = BusinessStore.Entry(System.currentTimeMillis(), "Compra", p.name, n, total, party.trim(), System.currentTimeMillis()); BusinessStore.addEntry(context, e); recent.add(0, e); message = "Compra registrada y existencias incrementadas."; qty = "1"; amount = if (p.purchasePrice > 0) p.purchasePrice.toString() else ""; party = "" } } } } }, enabled = !saving && products.isNotEmpty(), modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = Color(0xFF082018))) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text(if (saving) "Registrando…" else "Confirmar recepción", fontWeight = FontWeight.Bold) }
        } }
        Spacer(Modifier.height(20.dp)); Text("Compras recientes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); if (recent.isEmpty()) EmptyState("Sin compras todavía", "Las recepciones registradas aparecerán aquí.", Icons.Default.ShoppingBag) else recent.take(5).forEach { EntryRow(it, Orange) }
    }
    message?.let { AlertDialog(containerColor = PanelRaised, titleContentColor = Color.White, textContentColor = Muted, onDismissRequest = { message = null }, title = { Text("StockPro") }, text = { Text(it) }, confirmButton = { TextButton(onClick = { message = null }) { Text("Aceptar") } }) }
}

@Composable
internal fun StatHero(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
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
internal fun EntryRow(entry: BusinessStore.Entry, accent: Color) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(15.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 7.dp)) { Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(40.dp).background(accent.copy(alpha = .15f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Icon(if (entry.type == "Venta") Icons.Default.ShoppingCart else Icons.Default.ShoppingBag, null, tint = accent) }; Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(entry.item, fontWeight = FontWeight.SemiBold); Text("${entry.quantity} ud. · ${entry.party.ifBlank { entry.type }}", color = Muted, style = MaterialTheme.typography.bodySmall) }; Text(money(entry.amount), color = accent, fontWeight = FontWeight.Bold) } }
}

@Composable
internal fun ContactsScreen(modifier: Modifier, type: String) {
    val context = LocalContext.current; var query by remember { mutableStateOf("") }; var refresh by remember { mutableIntStateOf(0) }; var showForm by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }; var phone by remember { mutableStateOf("") }; var email by remember { mutableStateOf("") }; var notes by remember { mutableStateOf("") }; var error by remember { mutableStateOf<String?>(null) }; var editingContact by remember { mutableStateOf<BusinessStore.Contact?>(null) }; var deleteTarget by remember { mutableStateOf<BusinessStore.Contact?>(null) }
    val all = remember(refresh, type) { BusinessStore.contacts(context, type) }; val filtered = all.filter { it.name.contains(query, true) || it.phone.contains(query, true) || it.email.contains(query, true) }
    Column(modifier.fillMaxSize().stockProBackdrop().padding(horizontal = 18.dp).padding(top = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("STOCKPRO  /  PREMIUM", color = Mint, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp); Spacer(Modifier.height(4.dp)); Text(if (type == "Proveedor") "Proveedores" else "Clientes", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, letterSpacing = (-.5).sp); Text("Directorio de contactos del negocio", color = Muted, style = MaterialTheme.typography.bodySmall) }; FilledIconButton(onClick = { editingContact = null; name = ""; phone = ""; email = ""; notes = ""; error = null; showForm = true }, colors = IconButtonDefaults.filledIconButtonColors(containerColor = Lime, contentColor = AppBg)) { Icon(Icons.Default.Add, "Añadir") } }
        Spacer(Modifier.height(16.dp)); OutlinedTextField(query, { query = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Buscar nombre, teléfono o correo") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true, shape = RoundedCornerShape(16.dp)); Spacer(Modifier.height(14.dp))
        if (filtered.isEmpty()) EmptyState("Todavía no hay ${if (type == "Proveedor") "proveedores" else "clientes"}", "Añade un contacto para tenerlo siempre a mano.", Icons.Default.People) else LazyColumn(verticalArrangement = Arrangement.spacedBy(9.dp), contentPadding = PaddingValues(bottom = 20.dp)) { items(filtered, key = { it.id }) { c -> Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Hairline.copy(alpha = .9f)), modifier = Modifier.fillMaxWidth()) { Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(48.dp).background(Brush.linearGradient(listOf(Lime, Color(0xFF39B76B))), CircleShape).clickable { editingContact = c; name = c.name; phone = c.phone; email = c.email; notes = c.notes; error = null; showForm = true }, contentAlignment = Alignment.Center) { Text(c.name.take(1).uppercase(), color = AppBg, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge) }; Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f).clickable { editingContact = c; name = c.name; phone = c.phone; email = c.email; notes = c.notes; error = null; showForm = true }) { Text(c.name, fontWeight = FontWeight.Bold); if (c.phone.isNotBlank()) Text(c.phone, color = Color.White.copy(alpha = .85f), style = MaterialTheme.typography.bodySmall); if (c.email.isNotBlank()) Text(c.email, color = Muted, style = MaterialTheme.typography.bodySmall); if (c.notes.isNotBlank()) Text(c.notes, color = Muted, style = MaterialTheme.typography.bodySmall) }; IconButton(onClick = { deleteTarget = c }) { Icon(Icons.Default.Delete, "Eliminar contacto", tint = Color(0xFFFF7777)) } } } } }
    }
    if (showForm) AlertDialog(containerColor = PanelRaised, titleContentColor = Color.White, textContentColor = Muted, onDismissRequest = { showForm = false }, title = { Text(if (editingContact == null) "Nuevo ${type.lowercase()}" else "Editar ${type.lowercase()}") }, text = { Column(Modifier.heightIn(max = 460.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(9.dp)) { OutlinedTextField(name, { name = it; error = null }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre *") }, singleLine = true); OutlinedTextField(phone, { phone = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Teléfono") }, singleLine = true); OutlinedTextField(email, { email = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Correo electrónico") }, singleLine = true); OutlinedTextField(notes, { notes = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Notas") }, minLines = 2); error?.let { Text(it, color = MaterialTheme.colorScheme.error) } } }, confirmButton = { TextButton(onClick = { if (name.isBlank()) error = "El nombre es obligatorio." else { BusinessStore.addContact(context, BusinessStore.Contact(editingContact?.id ?: System.currentTimeMillis(), type, name.trim(), phone.trim(), email.trim(), notes.trim())); refresh++; showForm = false } }) { Text("Guardar") } }, dismissButton = { TextButton(onClick = { showForm = false }) { Text("Cancelar") } })
    deleteTarget?.let { c -> AlertDialog(containerColor = PanelRaised, titleContentColor = Color.White, textContentColor = Muted, onDismissRequest = { deleteTarget = null }, title = { Text("Eliminar contacto") }, text = { Text("¿Quieres eliminar a ${c.name}?") }, confirmButton = { TextButton(onClick = { BusinessStore.deleteContact(context, c.id); refresh++; deleteTarget = null }) { Text("Eliminar") } }, dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("Cancelar") } }) }
}

@Composable
internal fun ReportsScreen(modifier: Modifier) {
    val context = LocalContext.current; var filter by remember { mutableStateOf("Todo") }; val entries = remember { BusinessStore.entries(context) }; val sales = entries.filter { it.type == "Venta" }; val purchases = entries.filter { it.type == "Compra" }; val totalSales = sales.sumOf { it.amount }; val totalPurchases = purchases.sumOf { it.amount }; val profit = totalSales - totalPurchases
    Column(modifier.fillMaxSize().stockProBackdrop().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(top = 20.dp, bottom = 24.dp)) {
        PremiumHeader("Informes y estadísticas", "Resultados calculados desde operaciones registradas", Icons.Default.BarChart)
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.fillMaxWidth()) { SummaryCard("Ventas", money(totalSales), Icons.Default.ShoppingCart, Color(0xFF087BEA), Modifier.weight(1f)); SummaryCard("Compras", money(totalPurchases), Icons.Default.ShoppingBag, Orange, Modifier.weight(1f)) }
        Spacer(Modifier.height(10.dp)); SummaryCard("Balance de operaciones", money(profit), Icons.Default.TrendingUp, if (profit >= 0) Color(0xFF087D51) else Color(0xFFB73535), Modifier.fillMaxWidth())
        Spacer(Modifier.height(18.dp)); Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Actividad reciente", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Text("Importes de operaciones guardadas en este dispositivo", color = Muted, style = MaterialTheme.typography.bodySmall); Spacer(Modifier.height(18.dp)); val chart = entries.take(7).reversed(); if (chart.isEmpty()) EmptyState("Tu gráfico aparecerá aquí", "Registra ventas y compras para empezar a ver la evolución.", Icons.Default.BarChart) else Row(Modifier.fillMaxWidth().height(145.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) { chart.forEach { e -> val h = (24 + (e.amount.coerceAtMost(500.0) / 500.0 * 85).toInt()).dp; Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom, modifier = Modifier.weight(1f)) { Box(Modifier.width(18.dp).height(h).background(if (e.type == "Venta") Lime else Orange, RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))); Spacer(Modifier.height(5.dp)); Text(if (e.type == "Venta") "V" else "C", color = Muted, style = MaterialTheme.typography.labelSmall) } } } } }
        Spacer(Modifier.height(18.dp)); Text("Últimas operaciones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("Todo", "Venta", "Compra").forEach { FilterChip(selected = filter == it, onClick = { filter = it }, label = { Text(it) }) } }; Spacer(Modifier.height(8.dp)); val shown = entries.filter { filter == "Todo" || it.type == filter }; if (shown.isEmpty()) EmptyState("Sin datos para mostrar", "Los informes se actualizarán automáticamente al registrar operaciones.", Icons.Default.BarChart) else shown.take(20).forEach { EntryRow(it, if (it.type == "Venta") Lime else Orange) }
    }
}

@Composable
internal fun SummaryCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
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

