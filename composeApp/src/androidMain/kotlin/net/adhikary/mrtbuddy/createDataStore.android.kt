package net.adhikary.mrtbuddy

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import net.adhikary.mrtbuddy.preferences.DATA_STORE_FILE_NAME
import net.adhikary.mrtbuddy.preferences.createDataStore

/**
 * Created by Pronay Sarker on 12/11/2024 (4:39 AM)
 */
fun createDataStore(context: Context) : DataStore<Preferences> {
    return createDataStore{
        context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
    }
}