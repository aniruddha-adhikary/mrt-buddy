package net.adhikary.mrtbuddy.nfc

import kotlin.test.Test
import kotlin.test.assertEquals

class NfcCommandGeneratorTest {
    private val generator = NfcCommandGenerator()
    private val idm = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08)

    @Test
    fun firstWindowCommandLayout() {
        val command = generator.generateReadCommand(idm, startBlockNumber = 0)

        // LEN = 14 + 10*2 = 34
        assertEquals(34, command.size)
        assertEquals(34.toByte(), command[0])
        assertEquals(0x06.toByte(), command[1])
        // IDm at 2..9
        assertEquals(idm.toList(), command.copyOfRange(2, 10).toList())
        // number of services
        assertEquals(0x01.toByte(), command[10])
        // service code 0x220F little-endian
        assertEquals(0x0F.toByte(), command[11])
        assertEquals(0x22.toByte(), command[12])
        // number of blocks
        assertEquals(0x0A.toByte(), command[13])
    }

    @Test
    fun firstWindowBlockListEncoding() {
        val command = generator.generateReadCommand(idm, startBlockNumber = 0)

        for (i in 0 until 10) {
            assertEquals(0x80.toByte(), command[14 + i * 2])
            assertEquals(i.toByte(), command[14 + i * 2 + 1])
        }
    }

    @Test
    fun secondWindowBlockListStartsAtTen() {
        val command = generator.generateReadCommand(idm, startBlockNumber = 10)

        assertEquals(34, command.size)
        assertEquals(0x0A.toByte(), command[13])
        for (i in 0 until 10) {
            assertEquals(0x80.toByte(), command[14 + i * 2])
            assertEquals((10 + i).toByte(), command[14 + i * 2 + 1])
        }
    }
}
