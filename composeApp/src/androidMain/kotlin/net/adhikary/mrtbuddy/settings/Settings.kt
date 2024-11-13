package net.adhikary.mrtbuddy.settings

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.java.KoinJavaComponent.get

actual fun createSettings(): Settings {
    val context: Context = get(Context::class.java)
    val sharedPreferences = context.getSharedPreferences("mrt_buddy_settings", Context.MODE_PRIVATE)
    return SharedPreferencesSettings(sharedPreferences)
}
