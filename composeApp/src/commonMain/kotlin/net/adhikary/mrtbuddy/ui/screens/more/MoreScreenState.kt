package net.adhikary.mrtbuddy.ui.screens.more

data class MoreScreenState(
    val isLoading: Boolean = false,
    val autoSaveEnabled: Boolean = false,
    val currentLanguage: String = "en",
    val isDynamicColorEnabled: Boolean = false,
    val error: String? = null
)
