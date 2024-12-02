package net.adhikary.mrtbuddy.utils

public fun isRapidPassIdm(cardIdm: String): Boolean {
    return cardIdm.startsWith("01 27") || cardIdm.startsWith("0127")
}