package net.adhikary.mrtbuddy.ui.screens.home

import MoreScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.balance
import mrtbuddy.composeapp.generated.resources.fare
import mrtbuddy.composeapp.generated.resources.historyTab
import mrtbuddy.composeapp.generated.resources.more
import mrtbuddy.composeapp.generated.resources.openSourceLicenses
import mrtbuddy.composeapp.generated.resources.stationMap
import mrtbuddy.composeapp.generated.resources.transactions
import net.adhikary.mrtbuddy.ui.components.AppsIcon
import net.adhikary.mrtbuddy.ui.components.BalanceCard
import net.adhikary.mrtbuddy.ui.components.CalculatorIcon
import net.adhikary.mrtbuddy.ui.components.CardIcon
import net.adhikary.mrtbuddy.ui.components.HistoryIcon
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorScreen
import net.adhikary.mrtbuddy.ui.screens.history.HistoryScreen
import net.adhikary.mrtbuddy.ui.screens.licenses.OpenSourceLicensesScreen
import net.adhikary.mrtbuddy.ui.screens.stationmap.StationMapScreen
import net.adhikary.mrtbuddy.ui.screens.transactionlist.TransactionListScreen
import net.adhikary.mrtbuddy.ui.theme.MRTBuddyTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.datetime.LocalDateTime
import mrtbuddy.composeapp.generated.resources.recentActivity
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.translateNumber


//TimeFormatter function to format LocalDateTime to AM/PM format
fun formatTimeAMPM(dateTime: LocalDateTime): String {
    val hour = dateTime.hour % 12
    val displayHour = if (hour == 0) 12 else hour
    val minute = dateTime.minute
    val amPm = if (dateTime.hour < 12) "AM" else "PM"
    return "${displayHour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')} $amPm"
}
enum class Screen(val title: StringResource) {
    Home(title = Res.string.balance),
    Calculator(title = Res.string.fare),
    More(title = Res.string.more),
    History(title = Res.string.historyTab),
    TransactionList(title = Res.string.transactions),
    StationMap(title = Res.string.stationMap),
    Licenses(title = Res.string.openSourceLicenses)
}

@Composable
@Preview
fun MainScreen(
    viewModel: MainScreenViewModel = koinViewModel(),
    navController: NavHostController = rememberNavController()
) {
    MRTBuddyTheme {
        val uiState by viewModel.state.collectAsState()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentScreen = Screen.valueOf(
            backStackEntry?.destination?.route ?: Screen.Home.name
        )
        var selectedCardIdm by remember { mutableStateOf<String?>(null) }
        val hasTransactions = uiState.transaction.isNotEmpty()

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f)
                        )
                    )
                ),
            bottomBar = {
                if (currentScreen != Screen.StationMap) {
                    ModernNavigationBar(currentScreen, navController)
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.name,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(route = Screen.Home.name) {
                    ModernHomeScreen(
                        uiState = uiState,
                        hasTransactions = hasTransactions,
                        paddingValues = paddingValues
                    )
                }

                composable(route = Screen.Calculator.name) {
                    FareCalculatorScreen(
                        cardState = uiState.cardState,
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                composable(route = Screen.More.name) {
                    MoreScreen(
                        onNavigateToStationMap = {
                            navController.navigate(Screen.StationMap.name)
                        },
                        onNavigateToLicenses = {
                            navController.navigate(Screen.Licenses.name)
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                composable(route = Screen.History.name) {
                    HistoryScreen(
                        onCardSelected = { cardIdm ->
                            selectedCardIdm = cardIdm
                            navController.navigate(Screen.TransactionList.name)
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                composable(route = Screen.TransactionList.name) {
                    selectedCardIdm?.let { cardIdm ->
                        TransactionListScreen(
                            cardIdm = cardIdm,
                            onBack = {
                                navController.navigateUp()
                            },
                            paddingValues = paddingValues
                        )
                    }
                }

                composable(route = Screen.StationMap.name) {
                    StationMapScreen(
                        onBack = {
                            navController.navigateUp()
                        },
                    )
                }

                composable(route = Screen.Licenses.name) {
                    OpenSourceLicensesScreen(
                        onBack = {
                            navController.navigateUp()
                        },
                        paddingValues = paddingValues
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernNavigationBar(
    currentScreen: Screen,
    navController: NavHostController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 0.dp),
        shape = RoundedCornerShape(0.dp), // Removed corner radius for bottom nav
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
        )
    ) {
        NavigationBar(
            windowInsets = WindowInsets.navigationBars,
            tonalElevation = 0.dp,
            containerColor = Color.Transparent,
            modifier = Modifier
                .wrapContentSize() // Set a minimum height for better touch targets
                .padding(
                    horizontal = 0.dp,
                    vertical = 0.dp
                ) // Remove extra padding for a tighter fit
        ) {
            NavigationBarItem(
                icon = {
                    ModernNavIcon(
                        icon = { CalculatorIcon() },
                        isSelected = currentScreen == Screen.Calculator
                    )
                },
                label = {
                    Text(
                        stringResource(Res.string.fare),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (currentScreen == Screen.Calculator) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                },
                selected = currentScreen == Screen.Calculator,
                onClick = {
                    navigateToScreen(navController, Screen.Calculator)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            )
            NavigationBarItem(
                icon = {
                    ModernNavIcon(
                        icon = { CardIcon() },
                        isSelected = currentScreen == Screen.Home
                    )
                },
                label = {
                    Text(
                        stringResource(Res.string.balance),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (currentScreen == Screen.Home) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                },
                selected = currentScreen == Screen.Home,
                onClick = {
                    navigateToScreen(navController, Screen.Home)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            )
            NavigationBarItem(
                icon = {
                    ModernNavIcon(
                        icon = { HistoryIcon() },
                        isSelected = currentScreen == Screen.History || currentScreen == Screen.TransactionList
                    )
                },
                label = {
                    Text(
                        stringResource(Res.string.historyTab),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (currentScreen == Screen.History || currentScreen == Screen.TransactionList) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                },
                selected = currentScreen == Screen.History || currentScreen == Screen.TransactionList,
                onClick = {
                    navigateToScreen(navController, Screen.History)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            )
            NavigationBarItem(
                icon = {
                    ModernNavIcon(
                        icon = { AppsIcon() },
                        isSelected = currentScreen == Screen.More
                    )
                },
                label = {
                    Text(
                        stringResource(Res.string.more),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (currentScreen == Screen.More) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                },
                selected = currentScreen == Screen.More,
                onClick = {
                    navigateToScreen(navController, Screen.More)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            )
        }
    }
}

@Composable
private fun ModernNavIcon(
    icon: @Composable () -> Unit,
    isSelected: Boolean
) {
    Box(
        modifier = Modifier
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        val iconColor =
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                alpha = 0.7f
            )
        CompositionLocalProvider(LocalContentColor provides iconColor) {
            icon()
        }
    }
}

@Composable
private fun ModernHomeScreen(
    uiState: MainScreenState,
    hasTransactions: Boolean,
    paddingValues: PaddingValues,

) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 15.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Welcome section with modern styling - Fixed at top
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Welcome back",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
                Text(
                    text = "MRT Buddy",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            // Quick stats card
            Card(
                modifier = Modifier
                    .height(60.dp)
                    .width(120.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${uiState.transaction.size}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Transactions",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }

        // Enhanced Balance Card with modern styling - Fixed
        Card(
            modifier = Modifier
                .wrapContentSize()
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            BalanceCard(
                cardState = uiState.cardState,
                cardName = uiState.cardName,
                cardIdm = uiState.cardIdm,
                modifier = Modifier.padding(4.dp)
            )
        }

        if (hasTransactions) {
            // Recent Transactions section header - Fixed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.recentActivity),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.clickable {
                        // Handle click here

                    }
                ) {
//                    Text(
//                        text = "View All",
//                        modifier = Modifier
//                            .padding(horizontal = 12.dp, vertical = 6.dp),
//                        style = MaterialTheme.typography.labelMedium.copy(
//                            fontWeight = FontWeight.Medium,
//                            color = MaterialTheme.colorScheme.primary
//                        )
//
//                    )
                }
            }

            // Scrollable Recent Activity Section - Only this part scrolls
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Takes remaining space
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val validTransactions = uiState.transactionWithAmount
                        .filter { it.transaction.timestamp.year >= 2015 }

                    items(validTransactions.size) { index ->
                        val transactionWithAmount = validTransactions[index]
                        ModernTransactionItem(
                            transaction = transactionWithAmount,
                            showDivider = index < validTransactions.size - 1
                        )
                    }
                }
            }
        } else {
            // If no transactions, add some empty space
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ModernTransactionItem(
    transaction: net.adhikary.mrtbuddy.model.TransactionWithAmount,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (transaction.transaction.fromStation.isNotEmpty())
                        "${StationService.translate(transaction.transaction.fromStation)} → ${
                            StationService.translate(transaction.transaction.toStation)
                        }"
                    else "Balance Update",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${transaction.transaction.timestamp.dayOfMonth}/${transaction.transaction.timestamp.monthNumber}/${transaction.transaction.timestamp.year}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = formatTimeAMPM(transaction.transaction.timestamp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Light
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "৳${translateNumber(transaction.transaction.balance)}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                transaction.amount?.let { amount ->
                    Text(
                        text = if (amount > 0) "+৳${translateNumber(amount)}" else "৳${translateNumber(amount)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (amount > 0)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        if (showDivider) {
            androidx.compose.material3.HorizontalDivider(
                modifier = Modifier.padding(top = 12.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
        }
    }
}

private fun navigateToScreen(navController: NavHostController, screen: Screen) {
    navController.navigate(screen.name) {
        popUpTo(navController.graph.findStartDestination().route ?: Screen.Home.name) {
            saveState = true
            inclusive = false
        }
        launchSingleTop = true
        restoreState = true
    }
}
