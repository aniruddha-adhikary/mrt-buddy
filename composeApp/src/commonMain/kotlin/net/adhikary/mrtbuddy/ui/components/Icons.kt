package net.adhikary.mrtbuddy.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.calculate
import mrtbuddy.composeapp.generated.resources.card
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
public fun MapIcon() {
    return Icon(
        imageVector = Icons.Default.LocationOn,
        contentDescription = "Calculate"
    )
}
