package net.adhikary.mrtbuddy.nfc.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class ByteParserTest {
    @Test
    fun extractInt16LittleEndian() {
        val bytes = byteArrayOf(0x34, 0x12)
        assertEquals(0x1234, ByteParser.extractInt16(bytes))
    }

    @Test
    fun extractInt16WithOffset() {
        val bytes = byteArrayOf(0x00, 0x00, 0xFF.toByte(), 0x00)
        assertEquals(0x00FF, ByteParser.extractInt16(bytes, 2))
    }

    @Test
    fun extractInt24LittleEndian() {
        val bytes = byteArrayOf(0x56, 0x34, 0x12)
        assertEquals(0x123456, ByteParser.extractInt24(bytes))
    }

    @Test
    fun extractInt24BigEndian() {
        val bytes = byteArrayOf(0x12, 0x34, 0x56)
        assertEquals(0x123456, ByteParser.extractInt24BigEndian(bytes))
    }

    @Test
    fun extractByteIsUnsigned() {
        val bytes = byteArrayOf(0x00, 0xFF.toByte())
        assertEquals(255, ByteParser.extractByte(bytes, 1))
    }

    @Test
    fun toHexStringSpaceSeparatedUppercase() {
        val bytes = byteArrayOf(0x08, 0x52, 0x10, 0x00)
        assertEquals("08 52 10 00", ByteParser.toHexString(bytes))
    }

    @Test
    fun toHexStringHandlesHighBytes() {
        val bytes = byteArrayOf(0xD2.toByte(), 0x0F, 0xFF.toByte())
        assertEquals("D2 0F FF", ByteParser.toHexString(bytes))
    }
}
