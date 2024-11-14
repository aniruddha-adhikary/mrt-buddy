package net.adhikary.mrtbuddy.ui.screens.more

sealed interface MoreScreenEvent {
    data class Error(val message: String) : MoreScreenEvent
}
