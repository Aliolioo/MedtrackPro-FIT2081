package com.adytio.s35637889.medtrack.util

fun normalizePhone(phone: String): String {
    val digits = phone.trim().filter { it.isDigit() }

    return when {
        digits.startsWith("04") && digits.length == 10 -> digits
        digits.startsWith("4") && digits.length == 9 -> "0$digits"
        else -> digits
    }
}