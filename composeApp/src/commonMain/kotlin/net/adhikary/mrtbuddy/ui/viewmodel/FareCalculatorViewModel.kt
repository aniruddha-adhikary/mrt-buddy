package net.adhikary.mrtbuddy.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import net.adhikary.mrtbuddy.data.model.FareCalculator
import net.adhikary.mrtbuddy.data.model.Station
import net.adhikary.mrtbuddy.model.CardState

class FareCalculatorViewModel {
    private val fareCalculator = FareCalculator.getInstance()
    private val MRT_PASS_DISCOUNT = 0.10f // 10% discount for MRT/Rapid Pass

    var cardState by mutableStateOf<CardState>(CardState.WaitingForTap)
        private set

    var fromStation by mutableStateOf<Station?>(null)
        private set

    var toStation by mutableStateOf<Station?>(null)
        private set

    var calculatedFare by mutableStateOf(0)
        private set

    var discountedFare by mutableStateOf(0)
        private set

    var fromExpanded by mutableStateOf(false)
        private set

    var toExpanded by mutableStateOf(false)
        private set

    val stations = fareCalculator.getAllStations()

    fun updateCardState(newState: CardState) {
        cardState = newState
    }

    fun hasEnoughBalance(): Boolean {
        return when (cardState) {
            is CardState.Balance -> (cardState as CardState.Balance).amount >= calculatedFare
            else -> false
        }
    }

    fun getBalance(): Int? {
        return when (cardState) {
            is CardState.Balance -> (cardState as CardState.Balance).amount
            else -> null
        }
    }

    fun updateFromStation(station: Station) {
        fromStation = station
        fromExpanded = false
        calculateFares()
    }

    fun updateToStation(station: Station) {
        toStation = station
        toExpanded = false
        calculateFares()
    }

    fun toggleFromExpanded() {
        fromExpanded = !fromExpanded
        if (fromExpanded) toExpanded = false
    }

    fun toggleToExpanded() {
        toExpanded = !toExpanded
        if (toExpanded) fromExpanded = false
    }

    private fun calculateFares() {
        calculatedFare = if (fromStation != null && toStation != null) {
            fareCalculator.calculateFare(fromStation!!, toStation!!)
        } else {
            0
        }
        // Calculate discounted fare for MRT/Rapid Pass
        discountedFare = (calculatedFare * (1 - MRT_PASS_DISCOUNT)).toInt()
    }

    fun dismissDropdowns() {
        fromExpanded = false
        toExpanded = false
    }

    fun getSavings(): Int {
        return calculatedFare - discountedFare
    }

    companion object {
        private var instance: FareCalculatorViewModel? = null

        fun getInstance(): FareCalculatorViewModel {
            if (instance == null) {
                instance = FareCalculatorViewModel()
            }
            return instance!!
        }

        // Add reset method for testing
        fun reset() {
            instance = null
        }
    }
}
