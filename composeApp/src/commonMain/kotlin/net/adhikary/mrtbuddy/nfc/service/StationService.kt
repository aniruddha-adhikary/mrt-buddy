package net.adhikary.mrtbuddy.nfc.service

import androidx.compose.runtime.Composable
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.agargaon
import mrtbuddy.composeapp.generated.resources.bangladeshSecretariat
import mrtbuddy.composeapp.generated.resources.bijoySarani
import mrtbuddy.composeapp.generated.resources.dhakaUniversity
import mrtbuddy.composeapp.generated.resources.farmgate
import mrtbuddy.composeapp.generated.resources.karwanBazar
import mrtbuddy.composeapp.generated.resources.kazipara
import mrtbuddy.composeapp.generated.resources.mirpur10
import mrtbuddy.composeapp.generated.resources.mirpur11
import mrtbuddy.composeapp.generated.resources.motijheel
import mrtbuddy.composeapp.generated.resources.pallabi
import mrtbuddy.composeapp.generated.resources.shahbagh
import mrtbuddy.composeapp.generated.resources.shewrapara
import mrtbuddy.composeapp.generated.resources.uttaraCenter
import mrtbuddy.composeapp.generated.resources.uttaraNorth
import mrtbuddy.composeapp.generated.resources.uttaraSouth
import org.jetbrains.compose.resources.stringResource

class StationService {
    private val stationMap = mapOf(
        10 to "Motijheel",
        20 to "Bangladesh Secretariat",
        25 to "Dhaka University",
        30 to "Shahbagh",
        35 to "Karwan Bazar",
        40 to "Farmgate",
        45 to "Bijoy Sarani",
        50 to "Agargaon",
        55 to "Shewrapara",
        60 to "Kazipara",
        65 to "Mirpur 10",
        70 to "Mirpur 11",
        75 to "Pallabi",
        80 to "Uttara South",
        85 to "Uttara Center",
        90 to "Uttara North"
    )

    fun getStationName(code: Int): String =
        stationMap.getOrElse(code) { "Unknown Station ($code)" }

    companion object {
        @Composable
        fun translate(stationName: String): String {
            return when (stationName) {
                "Motijheel" -> stringResource(Res.string.motijheel)
                "Bangladesh Secretariat" -> stringResource(Res.string.bangladeshSecretariat)
                "Dhaka University" -> stringResource(Res.string.dhakaUniversity)
                "Shahbagh" -> stringResource(Res.string.shahbagh)
                "Karwan Bazar" -> stringResource(Res.string.karwanBazar)
                "Farmgate" -> stringResource(Res.string.farmgate)
                "Bijoy Sarani" -> stringResource(Res.string.bijoySarani)
                "Agargaon" -> stringResource(Res.string.agargaon)
                "Shewrapara" -> stringResource(Res.string.shewrapara)
                "Kazipara" -> stringResource(Res.string.kazipara)
                "Mirpur 10" -> stringResource(Res.string.mirpur10)
                "Mirpur-10" -> stringResource(Res.string.mirpur10)
                "Mirpur 11" -> stringResource(Res.string.mirpur11)
                "Mirpur-11" -> stringResource(Res.string.mirpur11)
                "Pallabi" -> stringResource(Res.string.pallabi)
                "Uttara South" -> stringResource(Res.string.uttaraSouth)
                "Uttara Center" -> stringResource(Res.string.uttaraCenter)
                "Uttara North" -> stringResource(Res.string.uttaraNorth)
                else -> "" // Default to English if no match is found
            }
        }
    }
}