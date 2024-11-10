package net.adhikary.mrtbuddy

import platform.Foundation.NSNumber
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle

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