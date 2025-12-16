package net.adhikary.mrtbuddy.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class CsvFileWriter(private val context: Context) {
    private var file: File? = null
    private var writer: BufferedWriter? = null

    actual fun createFile(filename: String): String {
        file = File(context.cacheDir, filename).also {
            it.parentFile?.mkdirs()
            writer = BufferedWriter(FileWriter(it))
        }
        return file!!.absolutePath
    }

    actual fun appendLine(line: String) {
        writer?.apply {
            write(line)
            newLine()
        }
    }

    actual fun close(): String {
        writer?.close()
        writer = null
        return file?.absolutePath ?: ""
    }

    actual fun share(mimeType: String) {
        val currentFile = file ?: return

        currentFile.setReadable(true, false)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            currentFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val shareIntent = Intent.createChooser(intent, "Export Transactions")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }
}
