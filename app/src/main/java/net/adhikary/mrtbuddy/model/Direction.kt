package net.adhikary.mrtbuddy.model

enum class Direction {
    NORTHBOUND,
    SOUTHBOUND;

    val displayName: String
        get() = when (this) {
            NORTHBOUND -> "Northbound"
            SOUTHBOUND -> "Southbound"
        }
}
