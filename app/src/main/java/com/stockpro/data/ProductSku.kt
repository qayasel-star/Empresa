package com.stockpro.data

import java.util.Locale

/** SKU legible por familia y secuencia: ARR-001, ACE-001, AZU-001, REF-001. */
object ProductSku {
    private val knownPrefixes = linkedMapOf(
        "arroz" to "ARR", "aceite" to "ACE", "azucar" to "AZU", "azúcar" to "AZU",
        "refresco" to "REF", "gaseosa" to "REF", "cola" to "REF", "leche" to "LEC",
        "harina" to "HAR", "pan" to "PAN", "huevo" to "HUE", "agua" to "AGU",
        "cafe" to "CAF", "café" to "CAF", "te" to "TE", "té" to "TE",
        "galleta" to "GAL", "pasta" to "PAS", "detergente" to "DET", "jabon" to "JAB",
        "jabón" to "JAB", "champu" to "CHA", "champú" to "CHA", "pollo" to "POL",
        "carne" to "CAR", "pescado" to "PES", "fruta" to "FRU", "verdura" to "VER"
    )

    fun prefixFor(name: String, category: String = ""): String {
        val normalized = name.trim().lowercase(Locale.ROOT)
        knownPrefixes.entries.firstOrNull { normalized.contains(it.key) }?.let { return it.value }
        val cat = category.trim().uppercase(Locale.ROOT).filter { it.isLetterOrDigit() }
        if (cat.isNotBlank() && cat != "GENERAL") return cat.take(3).padEnd(3, 'X')
        if (normalized.isBlank()) return "GEN"
        val words = normalized.split(Regex("[^\\p{L}\\p{N}]+")).filter { it.isNotBlank() }
        return (words.joinToString("") { it.take(1) }.uppercase(Locale.ROOT) + "XXX").take(3)
    }

    fun generate(name: String, category: String = "", existingSkus: Collection<String> = emptyList()): String {
        val prefix = prefixFor(name, category)
        val used = existingSkus.asSequence().map { normalize(it) }
            .filter { it.startsWith("$prefix-") }
            .mapNotNull { it.substringAfterLast('-').toIntOrNull() }.toSet()
        var next = 1
        while (next in used) next++
        return "$prefix-${next.toString().padStart(3, '0')}"
    }

    fun normalize(value: String): String = value.trim().uppercase(Locale.ROOT)
}
