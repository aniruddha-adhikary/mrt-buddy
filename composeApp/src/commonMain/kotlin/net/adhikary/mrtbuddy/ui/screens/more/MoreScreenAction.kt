package net.adhikary.mrtbuddy.ui.screens.more

sealed interface MoreScreenAction {
    object OnInit : MoreScreenAction
    data class SetAutoSave(val enabled: Boolean) : MoreScreenAction
    data class SetLanguage(val language: String) : MoreScreenAction
    object OpenLicenses : MoreScreenAction
}
