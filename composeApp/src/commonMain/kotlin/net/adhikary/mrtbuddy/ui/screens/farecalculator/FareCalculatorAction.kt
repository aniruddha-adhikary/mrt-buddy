package net.adhikary.mrtbuddy.ui.screens.farecalculator

import net.adhikary.mrtbuddy.data.model.Station
import net.adhikary.mrtbuddy.model.CardState

sealed interface FareCalculatorAction {
    object OnInit : FareCalculatorAction
    data class UpdateFromStation(val station: Station) : FareCalculatorAction
    data class UpdateToStation(val station: Station) : FareCalculatorAction
    data class UpdateCardState(val cardState: CardState) : FareCalculatorAction
    object ToggleFromExpanded : FareCalculatorAction
    object ToggleToExpanded : FareCalculatorAction
    object DismissDropdowns : FareCalculatorAction
}
