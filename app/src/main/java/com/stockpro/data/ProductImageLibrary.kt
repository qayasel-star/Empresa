package com.stockpro.data

/** Biblioteca local de imágenes de productos incluida en la APK. */
object ProductImageLibrary {
    data class Item(val key: String, val label: String, val drawableName: String, val keywords: List<String>)
    val items = listOf(
        Item("arroz", "Arroz", "icon_arroz", listOf("arroz", "cereal", "alimento")),
        Item("azucar", "Azúcar", "icon_azucar", listOf("azucar", "azúcar", "dulce")),
        Item("harina", "Harina", "icon_harina", listOf("harina", "repostería")),
        Item("aceite", "Aceite", "icon_aceite", listOf("aceite", "oliva")),
        Item("leche", "Leche", "icon_leche", listOf("leche", "lácteo", "lacteo")),
        Item("pan", "Pan", "icon_pan", listOf("pan", "panadería", "panaderia")),
        Item("huevos", "Huevos", "icon_huevos", listOf("huevo")),
        Item("carne", "Carne", "icon_carne", listOf("carne", "res")),
        Item("pollo", "Pollo", "icon_pollo", listOf("pollo", "ave")),
        Item("pescado", "Pescado", "icon_pescado", listOf("pescado", "mar")),
        Item("frutas", "Frutas", "icon_frutas", listOf("fruta", "frutas")),
        Item("verduras", "Verduras", "icon_verduras", listOf("verdura", "vegetal")),
        Item("refresco", "Refresco", "icon_refresco", listOf("refresco", "gaseosa", "cola", "bebida")),
        Item("agua", "Agua", "icon_agua", listOf("agua")),
        Item("galletas", "Galletas", "icon_galletas", listOf("galleta", "snack")),
        Item("cafe", "Café", "icon_cafe", listOf("café", "cafe")),
        Item("te", "Té", "icon_te", listOf("té", "te")),
        Item("limpieza", "Limpieza", "icon_limpieza", listOf("limpieza", "detergente", "lejía", "lejia")),
        Item("higiene", "Higiene", "icon_higiene", listOf("higiene", "jabón", "jabon", "champú", "champu")),
        Item("electronica", "Electrónica", "icon_electronica", listOf("electrónica", "electronica", "cable", "auricular")),
        Item("ferreteria", "Ferretería", "icon_ferreteria", listOf("ferretería", "ferreteria", "herramienta")),
        Item("otros", "Otros", "icon_ferreteria", emptyList())
    )
    fun suggest(name: String, category: String = ""): String {
        val text = "$name $category".lowercase()
        return items.firstOrNull { item -> item.keywords.any { text.contains(it) } }?.key ?: "otros"
    }
    fun byKey(key: String): Item = items.firstOrNull { it.key == key } ?: items.last()
}
