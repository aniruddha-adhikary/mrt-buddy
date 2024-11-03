package net.adhikary.mrtbuddy.nfc.parser

class ByteParser {
    fun toHexString(bytes: ByteArray): String =
        bytes.joinToString(" ") { "%02X".format(it) }

    fun extractInt16(bytes: ByteArray, offset: Int = 0): Int =
        ((bytes[offset + 1].toInt() and 0xFF) shl 8) or
                (bytes[offset].toInt() and 0xFF)

    fun extractInt24(bytes: ByteArray, offset: Int = 0): Int =
        ((bytes[offset + 2].toInt() and 0xFF) shl 16) or
                ((bytes[offset + 1].toInt() and 0xFF) shl 8) or
                (bytes[offset].toInt() and 0xFF)

    fun extractByte(bytes: ByteArray, offset: Int): Int =
        bytes[offset].toInt() and 0xFF
}