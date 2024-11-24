package net.adhikary.mrtbuddy.ui.screens.farecalculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.data.model.FareCalculator

class FareCalculatorViewModel : ViewModel() {
    private val fareCalculator = FareCalculator.getInstance()
    private val MRT_PASS_DISCOUNT = 0.10f // 10% discount for MRT/Rapid Pass

    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(FareCalculatorState())
    val state: StateFlow<FareCalculatorState> get() = _state.asStateFlow()

    private val _events = Channel<FareCalculatorEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        onAction(FareCalculatorAction.OnInit)
    }

    fun onAction(action: FareCalculatorAction) {
        when (action) {
            is FareCalculatorAction.OnInit -> {
                viewModelScope.launch {
                    // Initialization logic if needed
                }
            }
            is FareCalculatorAction.UpdateFromStation -> {
                _state.value = _state.value.copy(fromStation = action.station, fromExpanded = false)
                calculateFares()
            }
            is FareCalculatorAction.UpdateToStation -> {
                _state.value = _state.value.copy(toStation = action.station, toExpanded = false)
                calculateFares()
            }
            is FareCalculatorAction.UpdateCardState -> {
                _state.value = _state.value.copy(cardState = action.cardState)
            }
            FareCalculatorAction.ToggleFromExpanded -> {
                _state.value = _state.value.copy(
                    fromExpanded = !_state.value.fromExpanded,
                    toExpanded = false
                )
            }
            FareCalculatorAction.ToggleToExpanded -> {
                _state.value = _state.value.copy(
                    toExpanded = !_state.value.toExpanded,
                    fromExpanded = false
                )
            }
            FareCalculatorAction.DismissDropdowns -> {
                _state.value = _state.value.copy(fromExpanded = false, toExpanded = false)
            }
        }
    }

    private fun calculateFares() = viewModelScope.launch {
        val fromStation = _state.value.fromStation
        val toStation = _state.value.toStation
        if (fromStation != null && toStation != null) {
            val fare = fareCalculator.calculateFare(fromStation, toStation)
            val discountedFare = (fare * (1 - MRT_PASS_DISCOUNT)).toInt()
            _state.value = _state.value.copy(
                calculatedFare = fare,
                discountedFare = discountedFare
            )
        } else {
            _state.value = _state.value.copy(
                calculatedFare = 0,
                discountedFare = 0
            )
        }
    }

    val stations = fareCalculator.getAllStations()

}
