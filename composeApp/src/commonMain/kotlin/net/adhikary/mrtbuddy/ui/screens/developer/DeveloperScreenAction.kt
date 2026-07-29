package net.adhikary.mrtbuddy.ui.screens.developer

sealed interface DeveloperScreenAction {
    object OnInit : DeveloperScreenAction

    object ScanDemoCard : DeveloperScreenAction

    data class SetNfcDumpCapture(val enabled: Boolean) : DeveloperScreenAction

    object ShareLastDump : DeveloperScreenAction
}
