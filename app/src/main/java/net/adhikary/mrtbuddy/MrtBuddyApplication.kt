package net.adhikary.mrtbuddy

import android.app.Application
import android.content.Context
import net.adhikary.mrtbuddy.data.AppDatabase
import org.osmdroid.config.Configuration
import java.io.File

class MrtBuddyApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize osmdroid configuration
        Configuration.getInstance().let { config ->
            config.load(this, getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
            config.userAgentValue = packageName
            config.osmdroidBasePath = File(cacheDir, "osmdroid")
            config.osmdroidTileCache = File(config.osmdroidBasePath, "tiles")
            // Ensure cache directories exist
            config.osmdroidBasePath.mkdirs()
            config.osmdroidTileCache.mkdirs()
        }
    }

    companion object {
        lateinit var instance: MrtBuddyApplication
            private set
    }
}
