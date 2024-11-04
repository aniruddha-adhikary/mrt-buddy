package net.adhikary.mrtbuddy.nfc.service

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
        stationMap.getOrDefault(code, "Unknown Station ($code)")
}