package com.example.tubarriolimpioapp.util

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object DateFormatter {

    private val formatterSalida: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    fun formatFecha(raw: String?): String {
        if (raw.isNullOrBlank()) return ""
        return try {
            val odt = OffsetDateTime.parse(raw)
            odt.format(formatterSalida)
        } catch (e: Exception) {
            // Si no se puede parsear (por ejemplo ya viene formateada),
            // devolvemos el valor original para no romper nada.
            raw
        }
    }
}
