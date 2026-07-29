package net.adhikary.mrtbuddy.ui.screens.developer

sealed interface DeveloperScreenEvent {
    data class ShowSnackbar(val message: String) : DeveloperScreenEvent
}
