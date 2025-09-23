package net.adhikary.mrtbuddy.ui.screens.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.balance
import mrtbuddy.composeapp.generated.resources.cardId
import mrtbuddy.composeapp.generated.resources.lastScan
import mrtbuddy.composeapp.generated.resources.unnamedCard
import net.adhikary.mrtbuddy.data.CardEntity
import net.adhikary.mrtbuddy.ui.theme.DarkMRTPass
import net.adhikary.mrtbuddy.ui.theme.DarkRapidPass
import net.adhikary.mrtbuddy.ui.theme.LightMRTPass
import net.adhikary.mrtbuddy.ui.theme.LightRapidPass
import net.adhikary.mrtbuddy.ui.theme.Transparent
import net.adhikary.mrtbuddy.ui.theme.white80
import net.adhikary.mrtbuddy.ui.theme.white98
import net.adhikary.mrtbuddy.utils.TimeUtils
import net.adhikary.mrtbuddy.utils.isRapidPassIdm
import org.jetbrains.compose.resources.stringResource

@Composable
fun CardItem(
    card: CardEntity,
    balance: Int?,
    onCardSelected: () -> Unit,
    onRenameClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val isRapidPass = isRapidPassIdm(card.idm)
    var showDropdown by remember { mutableStateOf(false) }
    var isIdVisible by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    // Enhanced status logic for MRT Buddy context
    val isLowBalance = balance != null && balance < 50
    val isCriticalBalance = balance != null && balance < 20
    val isVeryLowBalance = balance != null && balance < 10
    val isInactive = card.lastScanTime?.let { lastScan ->
        val currentTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        val daysDifference = (currentTime - lastScan) / (1000 * 60 * 60 * 24)
        daysDifference >= 7
    } ?: true

    // Enhanced card colors with better gradients
    val cardColor = if (isRapidPass) {
        if (isDarkTheme) DarkRapidPass else LightRapidPass
    } else {
        if (isDarkTheme) DarkMRTPass else LightMRTPass
    }

    // Enhanced card gradient with better color transitions
    val cardGradient = if (isRapidPass) {
        // Rapid Pass: navy blue, deep sky blue, light sky blue, red
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF001F3F), // navy blue
                Color(0xFF1877F2), // deep sky blue
                Color(0xFF56CCF2), // light sky blue
                Color(0xFFFF3B30)  // red
            ),
            start = Offset(0f, 0f),
            end = Offset(1200f, 600f)
        )
    } else {
        // MRT Pass: deep green, teal, light sky blue, red
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF006400), // deep green
                Color(0xFF009688), // teal
                Color(0xFF56CCF2), // light sky blue
                Color(0xFFFF3B30)  // red
            ),
            start = Offset(0f, 0f),
            end = Offset(1200f, 600f)
        )
    }


    // Smooth animations
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(200),
        label = "card_press"
    )

    val animatedRotation by animateFloatAsState(
        targetValue = if (isPressed) -0.8f else 0f,
        animationSpec = tween(200),
        label = "card_rotation"
    )

    // Transit card design with MRT Buddy branding - Fixed height
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .scale(animatedScale)
            .rotate(animatedRotation)
            .clickable {
                isPressed = true
                onCardSelected()
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 16.dp,
            pressedElevation = 8.dp
        ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(cardColor)
        ) {
            // Enhanced card texture with MRT patterns
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Transparent)
            )

            // Menu - Fixed positioning
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(
                        color = Transparent
                    )
            ) {
                Surface(
                    modifier = Modifier
                        .size(32.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.25f)
                ) {
                    IconButton(
                        onClick = { showDropdown = !showDropdown }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Inline menu overlay (avoid Popup/DropdownMenu which uses SubcomposeLayout)
                if (showDropdown) {
                    Card(modifier = Modifier
                        .wrapContentSize()
                        .padding(top = 40.dp)
                        .zIndex(2f),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
                        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showDropdown = false
                                    onRenameClick()
                                }
                                .padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Rename Card")
                            }

                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showDropdown = false
                                    onDeleteClick()
                                }
                                .padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Delete Card", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            // Main card content - Simplified layout without expansion
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .height(200.dp)
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header section
                Column(
                    modifier = Modifier.background(Color.Transparent),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Card type with status indicators
                    Row(
                        modifier = Modifier.background(Color.Transparent),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isRapidPass) "RAPID PASS" else "MRT PASS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        // Status indicators
                        when {
                            isVeryLowBalance -> {
                                Surface(
                                    modifier = Modifier.background(Color.Transparent),
                                    shape = RoundedCornerShape(3.dp),
                                    color = Color.Red
                                ) {
                                    Text(
                                        text = "CRITICAL",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = Color.White,
                                        modifier = Modifier
                                            .background(Color.Transparent)
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }

                        if (isInactive) {
                            Surface(
                                shape = CircleShape,
                                color = Color.Gray.copy(alpha = 0.7f),
                                modifier = Modifier.size(6.dp)
                            ) {}
                        }
                    }

                    // Card name
                    Text(
                        text = card.name ?: stringResource(Res.string.unnamedCard),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Balance section with contactless icon always visible
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (balance != null) {
                        Column(
                            modifier = Modifier.background(Color.Transparent),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "BALANCE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 1.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Row(
                                modifier = Modifier.background(Color.Transparent),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "৳ $balance",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 26.sp,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = when {
                                        isVeryLowBalance -> Color.Red
                                        isLowBalance -> Color.Yellow
                                        else -> Color.White
                                    }
                                )
                                if (isLowBalance) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        modifier = Modifier.background(Color.Transparent),
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (isVeryLowBalance) Color.Red else Color.Yellow
                                    ) {
                                        Text(
                                            text = if (isVeryLowBalance) "CRITICAL" else "LOW",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.5.sp
                                            ),
                                            color = if (isVeryLowBalance) Color.White else Color.Black,
                                            modifier = Modifier.background(Color.Transparent)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    // Contactless icon always visible with 50dp size and background
                    Surface(
                        modifier = Modifier.size(30.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.3f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Contactless,
                            contentDescription = "Contactless",
                            tint = Color.White,
                            modifier = Modifier
                                .size(30.dp)
                                .alpha(0.5f) // 50% opacity

                        )
                    }
                }

                // Bottom section - Card details
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Card ID
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.Transparent)
                            .clickable { isIdVisible = !isIdVisible },
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "CARD ID",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = if (isIdVisible) {
                                card.idm.chunked(4).joinToString(" ")
                            } else {
                                "•••• •••• •••• ••••"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp,
                                fontSize = 14.sp
                            ),
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    // Visibility toggle
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(28.dp)
                    ) {
                        IconButton(
                            onClick = { isIdVisible = !isIdVisible }
                        ) {
                            Icon(
                                imageVector = if (isIdVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isIdVisible) "Hide ID" else "Show ID",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Last scan with contactless icon on top-right
                    Column(
                        modifier = Modifier.background(Color.Transparent),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.background(Color.Transparent),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "LAST SCAN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 1.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            // Contactless icon on top-right of Last Scan with background
//                            Surface(
//                                modifier = Modifier.size(16.dp),
//                                shape = CircleShape,
//                                color = Color.White.copy(alpha = 0.3f)
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.Contactless,
//                                    contentDescription = "Contactless",
//                                    tint = Color.White,
//                                    modifier = Modifier
//                                        .size(10.dp)
//                                        .padding(3.dp)
//                                )
//                            }
                        }
                        Text(
                            text = if (card.lastScanTime != null) {
                                TimeUtils.getTimeAgoString(card.lastScanTime)
                            } else {
                                "Never scanned"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = if (isInactive) {
                                Color.Red.copy(alpha = 0.9f)
                            } else {
                                Color.White.copy(alpha = 0.9f)
                            },
                            textAlign = TextAlign.End
                        )
                    }
                }
            }

            // Enhanced shine effect
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent,
                                Color.Transparent
                            ),
                            start = Offset(-100f, -100f),
                            end = Offset(400f, 400f)
                        )
                    )
            )


            // MRT Buddy watermark - Bank card style typography
            Text(
                text = "MRT BUDDY",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp,
                    fontFamily = FontFamily.SansSerif
                ),
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .rotate(-90f)
            )
        }
    }
}
