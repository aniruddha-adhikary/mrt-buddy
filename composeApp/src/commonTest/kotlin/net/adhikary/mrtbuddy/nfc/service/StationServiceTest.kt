package net.adhikary.mrtbuddy.nfc.service

import kotlin.test.Test
import kotlin.test.assertEquals

class StationServiceTest {
    @Test
    fun knownMetroStations() {
        assertEquals("Motijheel", StationService.getStationName(10))
        assertEquals("Uttara North", StationService.getStationName(90))
    }

    @Test
    fun knownHatirjheelStations() {
        assertEquals("Mohanagar (HJ)", StationService.getStationName(13))
        assertEquals("FDC (HJ)", StationService.getStationName(28))
    }

    @Test
    fun unknownCodeRendersWithCodeNumber() {
        assertEquals("Unknown (200)", StationService.getStationName(200))
    }
}
