package net.adhikary.mrtbuddy.platform

import android.content.Intent
import android.net.Uri
import net.adhikary.mrtbuddy.MainActivity

actual fun openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    MainActivity.instance?.startActivity(intent)
}
