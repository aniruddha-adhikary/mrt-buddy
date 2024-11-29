package net.adhikary.mrtbuddy.ui.screens.more

sealed interface MoreScreenAction {
    object OnInit : MoreScreenAction
    data class SetAutoSave(val enabled: Boolean) : MoreScreenAction
    data class SetLanguage(val language: String) : MoreScreenAction
    data class SetDynamicColor(val enabled: Boolean) : MoreScreenAction
    object StationMap : MoreScreenAction
    object OpenLicenses : MoreScreenAction
}
