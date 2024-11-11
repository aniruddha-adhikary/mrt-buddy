package net.adhikary.mrtbuddy

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

actual fun changeLang(lang: String) {
    val locale = Locale(lang)
    Locale.setDefault(locale)
}

actual fun translateNumber(number: Int): String {
    return DecimalFormat.getInstance().format(number).replace(",", "")  // Remove commas
}