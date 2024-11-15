package net.adhikary.mrtbuddy.ui.screens.farecalculator

import net.adhikary.mrtbuddy.data.model.Station
import net.adhikary.mrtbuddy.model.CardState

data class FareCalculatorState(
    val cardState: CardState = CardState.WaitingForTap,
    val fromStation: Station? = null,
    val toStation: Station? = null,
    val calculatedFare: Int = 0,
    val discountedFare: Int = 0,
    val fromExpanded: Boolean = false,
    val toExpanded: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
