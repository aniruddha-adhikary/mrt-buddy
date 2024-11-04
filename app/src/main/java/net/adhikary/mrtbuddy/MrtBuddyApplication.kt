package net.adhikary.mrtbuddy

import android.app.Application
import androidx.multidex.MultiDexApplication

class MrtBuddyApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any application-wide dependencies here
    }
}
