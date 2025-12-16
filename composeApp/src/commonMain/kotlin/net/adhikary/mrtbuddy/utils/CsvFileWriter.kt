package net.adhikary.mrtbuddy.utils

/**
 * Platform-specific CSV file writer that streams data to a temporary file.
 * This avoids loading the entire CSV content into memory.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class CsvFileWriter {
    /**
     * Creates a new temporary file for writing CSV data.
     * @param filename The desired filename
     * @return The path to the created file
     */
    fun createFile(filename: String): String

    /**
     * Appends a line to the current file.
     * @param line The line to append (newline will be added automatically)
     */
    fun appendLine(line: String)

    /**
     * Closes the file and returns the path for sharing.
     * @return The file path
     */
    fun close(): String

    /**
     * Shares the written file via platform share sheet.
     * @param mimeType The MIME type for sharing
     */
    fun share(mimeType: String)
}
