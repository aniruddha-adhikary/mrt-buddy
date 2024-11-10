package net.adhikary.mrtbuddy

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.appName
import mrtbuddy.composeapp.generated.resources.language
import net.adhikary.mrtbuddy.dao.DemoDao
import net.adhikary.mrtbuddy.managers.RescanManager
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.nfc.getNFCManager
import net.adhikary.mrtbuddy.ui.screens.MainScreen
import net.adhikary.mrtbuddy.ui.theme.MRTBuddyTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(dao: DemoDao) {
    val scope = rememberCoroutineScope()
    val nfcManager = getNFCManager()
    val McardState = remember { mutableStateOf<CardState>(CardState.WaitingForTap) }
    val Mtransactions = remember { mutableStateOf<List<Transaction>>(emptyList()) }

    if(RescanManager.isRescanRequested.value) {
        nfcManager.startScan()
        RescanManager.resetRescanRequest()
    }

    scope.launch {
        nfcManager.transactions.collectLatest {
            Mtransactions.value = it
        }
    }
    scope.launch {
        nfcManager.cardState.collectLatest {
            McardState.value = it
        }
    }

    nfcManager.startScan()

    MRTBuddyTheme {
        var lang by remember { mutableStateOf(Language.English.isoFormat) }
        LocalizedApp (
            language= lang
        ) {
            Scaffold {
                Box(
                   Modifier.systemBarsPadding()
                ) {
                    Column {
                        TopAppBar(
                            title = {
                                Text(text = stringResource(Res.string.appName))
                            },
                            backgroundColor = MaterialTheme.colors.background,
                            actions = {
                                OutlinedButton(
                                    onClick = {
                                        lang = switchLanguage(lang)
                                        changeLang(lang)
                                    },
                                    shape = RoundedCornerShape(24.dp),
                                ) {
                                    Text(text = stringResource(Res.string.language))
                                }
                            }
                        )
                        MainScreen(
                            cardState = McardState.value,
                            transactions = Mtransactions.value
                        )
                    }
                }
            }


        }


    }
}

private fun switchLanguage(lang: String) : String{
    print("Switching language")
    return when (lang) {
        Language.English.isoFormat -> Language.Bangla.isoFormat
        Language.Bangla.isoFormat -> Language.English.isoFormat
        else -> Language.English.isoFormat
    }
}
