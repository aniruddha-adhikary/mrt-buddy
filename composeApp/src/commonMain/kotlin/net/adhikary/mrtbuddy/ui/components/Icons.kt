package net.adhikary.mrtbuddy.ui.components

import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.apps
import mrtbuddy.composeapp.generated.resources.calculate
import mrtbuddy.composeapp.generated.resources.card
import mrtbuddy.composeapp.generated.resources.history
import org.jetbrains.compose.resources.painterResource

@Composable
public fun CalculatorIcon() {
    return Icon(
        painter = painterResource(Res.drawable.calculate),
        contentDescription = "Calculate"
    )
}

@Composable
public fun CardIcon() {
    return Icon(
        painter = painterResource(Res.drawable.card),
        contentDescription = "Calculate"
    )
}

@Composable
public fun AppsIcon() {
    return Icon(
        painter = painterResource(Res.drawable.apps),
        contentDescription = "Apps"
    )
}

@Composable
public fun HistoryIcon() {
    return Icon(
        painter = painterResource(Res.drawable.history),
        contentDescription = "Apps"
    )
}
