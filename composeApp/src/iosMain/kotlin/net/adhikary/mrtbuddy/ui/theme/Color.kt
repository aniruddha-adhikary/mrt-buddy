package net.adhikary.mrtbuddy.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import platform.UIKit.UIColor

fun Color.toUIColor(): UIColor {
    val argb = this.toArgb()
    val red = ((argb shr 16) and 0xFF) / 255.0
    val green = ((argb shr 8) and 0xFF) / 255.0
    val blue = (argb and 0xFF) / 255.0
    val alpha = ((argb shr 24) and 0xFF) / 255.0
    return UIColor(red = red, green = green, blue = blue, alpha = alpha)
}