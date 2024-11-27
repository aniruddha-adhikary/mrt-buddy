package net.adhikary.mrtbuddy.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.station
import net.adhikary.mrtbuddy.nfc.service.StationService
import org.jetbrains.compose.resources.stringResource


@Composable
fun InfoWindow(
    title: String,
    shouldAnimate: Boolean,
    onAnimationDone: () -> Unit
) {
    val surfaceContainer = MaterialTheme.colorScheme.surfaceContainer
    var animVal by remember { mutableStateOf(if (shouldAnimate) 0f else 1f) }

    LaunchedEffect(true) {
        if (shouldAnimate) {
            Animatable(0f).animateTo(
                targetValue = 1f,
                animationSpec = tween(250)
            ) {
                animVal = value
            }
            onAnimationDone()
        }
    }
    Box(
        modifier = Modifier
            .alpha(animVal)
            .graphicsLayer {
                scaleX = animVal
                scaleY = animVal
                transformOrigin = TransformOrigin(0.5f, 1f)
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(8.dp),
                        clip = false
                    )
                    .background(
                        color = surfaceContainer,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (title.contains("Diabari", ignoreCase = true)) {
                            StationService.translate(title)
                        } else {
                            "${StationService.translate(title)} ${stringResource(Res.string.station)}"
                        },
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Canvas(
                modifier = Modifier
                    .size(16.dp)
            ) {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width / 2, size.height)
                    close()
                }
                drawPath(
                    path = path,
                    color = surfaceContainer,
                )
            }
        }
    }
}