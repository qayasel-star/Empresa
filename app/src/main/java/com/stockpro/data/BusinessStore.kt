package com.stockpro.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** Small offline store for business records not yet represented by the Room schema. */
object BusinessStore {
    data class Entry(val id: Long, val type: String, val item: String, val quantity: Int, val amount: Double, val party: String, val timestamp: Long)
    data class Contact(val id: Long, val type: String, val name: String, val phone: String, val email: String, val notes: String)
    private fun prefs(context: Context) = context.getSharedPreferences("stockpro_business_v1", Context.MODE_PRIVATE)

    fun entries(context: Context): List<Entry> = try {
        val a = JSONArray(prefs(context).getString("entries", "[]"))
        (0 until a.length()).map { i -> a.getJSONObject(i).let { Entry(it.optLong("id"), it.optString("type"), it.optString("item"), it.optInt("quantity"), it.optDouble("amount"), it.optString("party"), it.optLong("timestamp")) } }.sortedByDescending { it.timestamp }
    } catch (_: Exception) { emptyList() }
    fun addEntry(context: Context, entry: Entry) {
        val all = entries(context).toMutableList(); all.add(0, entry)
        val a = JSONArray(); all.take(500).forEach { e -> a.put(JSONObject().put("id", e.id).put("type", e.type).put("item", e.item).put("quantity", e.quantity).put("amount", e.amount).put("party", e.party).put("timestamp", e.timestamp)) }
        prefs(context).edit().putString("entries", a.toString()).apply()
    }
    fun contacts(context: Context, type: String): List<Contact> = try {
        val a = JSONArray(prefs(context).getString("contacts", "[]"))
        (0 until a.length()).map { i -> a.getJSONObject(i).let { Contact(it.optLong("id"), it.optString("type"), it.optString("name"), it.optString("phone"), it.optString("email"), it.optString("notes")) } }.filter { it.type == type }.sortedBy { it.name.lowercase() }
    } catch (_: Exception) { emptyList() }
    fun addContact(context: Context, contact: Contact) {
        val old = contacts(context, contact.type).filterNot { it.id == contact.id }
        val otherType = if (contact.type == "Proveedor") "Cliente" else "Proveedor"
        val merged = JSONArray(); (contacts(context, otherType) + (old + contact)).forEach { c -> merged.put(JSONObject().put("id", c.id).put("type", c.type).put("name", c.name).put("phone", c.phone).put("email", c.email).put("notes", c.notes)) }
        prefs(context).edit().putString("contacts", merged.toString()).apply()
    }
    fun deleteContact(context: Context, id: Long) {
        val a = JSONArray(); val raw = JSONArray(prefs(context).getString("contacts", "[]"))
        for (i in 0 until raw.length()) { val item = raw.getJSONObject(i); if (item.optLong("id") != id) a.put(item) }
        prefs(context).edit().putString("contacts", a.toString()).apply()
    }
    fun setting(context: Context, key: String, fallback: String) = prefs(context).getString(key, fallback) ?: fallback
    fun setSetting(context: Context, key: String, value: String) { prefs(context).edit().putString(key, value).apply() }
    fun export(context: Context, products: List<ProductEntity> = emptyList()): String {
        val root = JSONObject().put("format", "StockPro backup summary v1").put("createdAt", System.currentTimeMillis())
        val e = JSONArray(); entries(context).forEach { e.put(JSONObject().put("type", it.type).put("item", it.item).put("quantity", it.quantity).put("amount", it.amount).put("party", it.party).put("timestamp", it.timestamp)) }
        val c = JSONArray(); (contacts(context, "Proveedor") + contacts(context, "Cliente")).forEach { c.put(JSONObject().put("type", it.type).put("name", it.name).put("phone", it.phone).put("email", it.email).put("notes", it.notes)) }
        val p = JSONArray(); products.forEach { p.put(JSONObject().put("id", it.id).put("name", it.name).put("sku", it.sku).put("barcode", it.barcode).put("categoryId", it.categoryId).put("unit", it.unit).put("stockQuantity", it.stockQuantity).put("minimumStock", it.minimumStock).put("purchasePrice", it.purchasePrice).put("salePrice", it.salePrice).put("expiryDate", it.expiryDate).put("notes", it.notes)) }
        root.put("transactions", e).put("contacts", c).put("products", p); return root.toString(2)
    }
}
