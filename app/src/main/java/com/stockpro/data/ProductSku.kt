package com.stockpro.data

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

/** Generador de referencias legibles para productos nuevos. */
object ProductSku {
    private val timestampFormatter = DateTimeFormatter.ofPattern("yyMMdd-HHmmss", Locale.ROOT)

    /** Devuelve una referencia SP-YYMMDD-HHMMSS-XXXX al abrir el formulario de alta. */
    fun generate(now: LocalDateTime = LocalDateTime.now(), token: String = UUID.randomUUID().toString()): String {
        val timestamp = now.format(timestampFormatter)
        val suffix = token.replace("-", "").filter { it.isLetterOrDigit() }.take(4).uppercase(Locale.ROOT)
        require(suffix.length == 4) { "El identificador de SKU debe aportar al menos cuatro caracteres alfanuméricos." }
        return "SP-$timestamp-$suffix"
    }

    fun normalize(value: String): String = value.trim().uppercase(Locale.ROOT)
}
