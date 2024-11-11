package net.adhikary.mrtbuddy.ui.screens.home

// event are one time action which will be triggered by the user
// like navigation
// button tap

sealed interface MainScreenEvent {
    data class Error(val error: String) : MainScreenEvent
    data object ShowMessage : MainScreenEvent
}