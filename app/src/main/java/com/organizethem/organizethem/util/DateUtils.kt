package com.organizethem.organizethem.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateUtils {
    fun getDayOfWeek(dateString: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(dateString, formatter)
        return date.dayOfWeek.name.lowercase()
    }

    fun isDateInPast(dateString: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(dateString, formatter)
        return date.isBefore(LocalDate.now())
    }
}