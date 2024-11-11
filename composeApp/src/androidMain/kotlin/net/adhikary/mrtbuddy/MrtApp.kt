package net.adhikary.mrtbuddy
import android.app.Application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier


class MRTApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (isDebug) {
            Napier.base(DebugAntilog())

        }
    }


}