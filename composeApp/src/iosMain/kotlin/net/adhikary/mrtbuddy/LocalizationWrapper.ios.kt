package net.adhikary.mrtbuddy

import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle
import platform.Foundation.currentLocale

actual fun changeLang(
    lang: String
) {
    NSUserDefaults.standardUserDefaults.setObject(arrayListOf(lang),"AppleLanguages")
}

actual fun translateNumber(
    number: Int
): String {
    val numberFormatter = NSNumberFormatter()
    numberFormatter.numberStyle = NSNumberFormatterDecimalStyle
    return (numberFormatter.stringFromNumber(NSNumber(number)) ?: "").replace(",", "")
}

actual fun translateDoubleNumber(number: Double): String {
    val formatter = NSNumberFormatter().apply {
        numberStyle = NSNumberFormatterDecimalStyle
        minimumFractionDigits = 2u
        maximumFractionDigits = 2u
        locale = NSLocale.currentLocale
    }
    return formatter.stringFromNumber(NSNumber(number))?.replace(",", "") ?: number.toString()
}