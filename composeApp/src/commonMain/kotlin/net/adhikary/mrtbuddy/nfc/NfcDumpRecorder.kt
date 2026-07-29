package net.adhikary.mrtbuddy.nfc

import net.adhikary.mrtbuddy.nfc.parser.ByteParser

/**
 * Debug-only, in-memory recorder for raw card-read windows. A single card read is one session:
 * [startSession] clears the previous one, [record] appends each window while [enabled], and
 * [lastDumpText] renders the fixture-ready v1 text dump with the IDm anonymized to zeros — the
 * real IDm never enters this buffer. Global mutable state confined to diagnostics: the read
 * coroutine is the only writer, the share action the only reader after a session ends.
 */
object NfcDumpRecorder {
    var enabled: Boolean = false

    private var platform: String = ""
    private val windows = mutableListOf<Window>()

    data class Window(
        val serviceCode: Int,
        val startBlock: Int,
        val count: Int,
        val statusFlag1: Int,
        val statusFlag2: Int,
        val blocks: List<ByteArray>,
    )

    fun startSession(platform: String) {
        this.platform = platform
        windows.clear()
    }

    fun record(window: Window) {
        if (!enabled) return
        windows.add(window)
    }

    fun lastDumpText(): String? {
        if (windows.isEmpty()) return null
        val lines = mutableListOf<String>()
        lines += "# MRT Buddy NFC dump v1"
        lines += "platform: $platform"
        lines += "idm: 00 00 00 00 00 00 00 00 (anonymized)"
        windows.forEach { window ->
            lines +=
                "window serviceCode=0x${hex4(window.serviceCode)} " +
                "start=${window.startBlock} count=${window.count} " +
                "status=${hex2(window.statusFlag1)} ${hex2(window.statusFlag2)}"
            window.blocks.forEachIndexed { index, block ->
                lines += "block ${index.toString().padStart(2, '0')}: ${ByteParser.toHexString(block)}"
            }
        }
        return lines.joinToString("\n")
    }

    private fun hex2(value: Int): String = (value and 0xFF).toString(16).uppercase().padStart(2, '0')

    private fun hex4(value: Int): String = (value and 0xFFFF).toString(16).uppercase().padStart(4, '0')
}
