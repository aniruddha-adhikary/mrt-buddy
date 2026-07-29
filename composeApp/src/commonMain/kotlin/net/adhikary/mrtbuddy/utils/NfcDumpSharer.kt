package net.adhikary.mrtbuddy.utils

/**
 * Seam over the platform share flow for NFC dumps, so the developer ViewModel stays JVM-testable
 * (the [CsvFileWriter] `expect class` cannot be constructed in commonTest).
 */
interface NfcDumpSharer {
    fun share(dumpText: String)
}

/**
 * Writes the dump text through [CsvFileWriter] (one line at a time) and shares it as `text/plain`,
 * reusing the same streaming writer the CSV export uses.
 */
class CsvNfcDumpSharer(
    private val csvFileWriter: CsvFileWriter,
) : NfcDumpSharer {
    override fun share(dumpText: String) {
        csvFileWriter.createFile("mrtbuddy-nfc-dump.txt")
        dumpText.split("\n").forEach { csvFileWriter.appendLine(it) }
        csvFileWriter.close()
        csvFileWriter.share("text/plain")
    }
}
