package net.adhikary.mrtbuddy.nfc.parser

object ByteParser {
    private val hexChars = "0123456789ABCDEF"

    fun toHexString(bytes: ByteArray): String =
        bytes.joinToString(" ") { byte ->
            val unsigned = byte.toInt() and 0xFF
            val highNibble = hexChars[unsigned shr 4]
            val lowNibble = hexChars[unsigned and 0x0F]
            "$highNibble$lowNibble"
        }

    fun extractInt16(bytes: ByteArray, offset: Int = 0): Int =
        ((bytes[offset + 1].toInt() and 0xFF) shl 8) or
                (bytes[offset].toInt() and 0xFF)

    fun extractInt24(bytes: ByteArray, offset: Int = 0): Int =
        ((bytes[offset + 2].toInt() and 0xFF) shl 16) or
                ((bytes[offset + 1].toInt() and 0xFF) shl 8) or
                (bytes[offset].toInt() and 0xFF)

    fun extractByte(bytes: ByteArray, offset: Int): Int =
        bytes[offset].toInt() and 0xFF

    fun extractInt24BigEndian(bytes: ByteArray, offset: Int = 0): Int =
        ((bytes[offset].toInt() and 0xFF) shl 16) or
                ((bytes[offset + 1].toInt() and 0xFF) shl 8) or
                (bytes[offset + 2].toInt() and 0xFF)
}