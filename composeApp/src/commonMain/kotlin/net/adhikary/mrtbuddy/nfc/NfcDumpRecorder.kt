package net.adhikary.mrtbuddy.nfc

import net.adhikary.mrtbuddy.nfc.parser.ByteParser

/**
 * Debug-only, in-memory recorder for raw card-read windows. A single card read is one session:
 * [startSession] marks a new session pending, and the previous dump is discarded only when the
 * first window of the new session is actually recorded — so a capture-off read or a read that
 * fails before recording anything leaves the last good dump intact. [record] appends each window
 * while [enabled]; [lastDumpText] renders the fixture-ready v1 text dump with the IDm anonymized
 * to zeros (the real IDm never enters this buffer) and always reports the platform of the session
 * whose windows it currently holds. Global mutable state confined to diagnostics: the read
 * coroutine is the only writer, the share action the only reader after a session ends.
 */
object NfcDumpRecorder {
    var enabled: Boolean = false

    private var platform: String = ""
    private var pendingPlatform: String? = null
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
        pendingPlatform = platform
    }

    fun record(window: Window) {
        if (!enabled) return
        val pending = pendingPlatform
        if (pending != null) {
            windows.clear()
            platform = pending
            pendingPlatform = null
        }
        windows.add(window)
    }

    /** Test-only hard reset of the shared recorder state. */
    internal fun resetForTests() {
        enabled = false
        platform = ""
        pendingPlatform = null
        windows.clear()
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
