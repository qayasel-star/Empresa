package com.stockpro.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stockpro.data.ProductEntity
import com.stockpro.data.ProductSku
import com.stockpro.data.CategoryEntity
import com.stockpro.data.StockProDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StockProViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = StockProDatabase.get(application).dao()
    val products = dao.observeProducts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val productCount = dao.observeProductCount().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
    val lowStockCount = dao.observeLowStockCount().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
    val categories = dao.observeCategories().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun saveProduct(id: Long?, name: String, sku: String, barcode: String, stock: Int, minStock: Int, unit: String, notes: String, purchasePrice: Double, salePrice: Double, expiryDate: String, imageKey: String, categoryId: Long?, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val cleanName = name.trim()
                require(cleanName.isNotEmpty()) { "El nombre es obligatorio." }
                val normalizedSku = ProductSku.normalize(sku)
                require(normalizedSku.isNotBlank()) { "La referencia SKU es obligatoria." }
                require(stock >= 0) { "El stock inicial no puede ser negativo." }
                require(minStock >= 0) { "El stock mínimo no puede ser negativo." }
                require(purchasePrice >= 0 && salePrice >= 0) { "Los precios no pueden ser negativos." }
                require(expiryDate.isBlank() || runCatching { java.time.LocalDate.parse(expiryDate) }.isSuccess) { "La fecha de vencimiento no es válida." }
                val now = System.currentTimeMillis()
                val normalizedBarcode = barcode.trim()
                if (id == null) {
                    dao.createProductWithInitialStock(ProductEntity(name = cleanName, sku = normalizedSku, barcode = normalizedBarcode, categoryId = categoryId, stockQuantity = 0, minimumStock = minStock, unit = unit.trim().ifBlank { "unidad" }, notes = notes.trim(), purchasePrice = purchasePrice, salePrice = salePrice, expiryDate = expiryDate.trim(), imageKey = imageKey, createdAt = now, updatedAt = now), stock)
                } else {
                    val old = dao.getProduct(id) ?: error("No se encontró el producto.")
                    val result = dao.updateProductAndStock(old.copy(name = cleanName, sku = normalizedSku, barcode = normalizedBarcode, categoryId = categoryId, minimumStock = minStock, unit = unit.trim().ifBlank { "unidad" }, notes = notes.trim(), purchasePrice = purchasePrice, salePrice = salePrice, expiryDate = expiryDate.trim(), imageKey = imageKey, updatedAt = now), stock)
                    result.getOrThrow()
                }
                onDone(null)
            } catch (e: Exception) {
                onDone(e.message ?: "No se pudo guardar el producto.")
            }
        }
    }

    fun saveCategory(name: String, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val clean = name.trim()
                require(clean.isNotEmpty()) { "Escribe un nombre para la categoría." }
                dao.insertCategory(CategoryEntity(name = clean))
                onDone(null)
            } catch (e: Exception) { onDone(if (e.message?.contains("UNIQUE", true) == true) "Ya existe una categoría con ese nombre." else e.message ?: "No se pudo guardar la categoría.") }
        }
    }

    fun saveExpiry(id: Long, date: String, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                require(date.isBlank() || runCatching { java.time.LocalDate.parse(date) }.isSuccess) { "Usa una fecha válida en formato AAAA-MM-DD." }
                val product = dao.getProduct(id) ?: error("No se encontró el producto.")
                dao.updateProduct(product.copy(expiryDate = date.trim(), updatedAt = System.currentTimeMillis()))
                onDone(null)
            } catch (e: Exception) { onDone(e.message ?: "No se pudo guardar la fecha.") }
        }
    }

    fun adjustStock(id: Long, delta: Int, reason: String, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val result = dao.adjustStock(id, delta, if (delta >= 0) "ENTRADA" else "SALIDA", reason)
                onDone(result.exceptionOrNull()?.message)
            } catch (e: Exception) {
                onDone(e.message ?: "No se pudo registrar el movimiento.")
            }
        }
    }
}
