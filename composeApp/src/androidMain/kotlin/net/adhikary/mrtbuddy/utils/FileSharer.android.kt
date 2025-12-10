package net.adhikary.mrtbuddy.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import io.github.aakira.napier.Napier
import java.io.File
import java.io.IOException

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class FileSharer(private val context: Context) {
    actual fun share(content: String, filename: String, mimeType: String) {
        try {
            val file = File(context.cacheDir, filename)
            file.parentFile?.mkdirs()
            file.writeText(content)
            file.setReadable(true, false)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val shareIntent = Intent.createChooser(intent, "Export Transactions")
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(shareIntent)
        } catch (e: IOException) {
            Napier.e("Failed to write CSV file for sharing", e)
        } catch (e: Exception) {
            Napier.e("Failed to share CSV file", e)
        }
    }
}
