package net.adhikary.mrtbuddy.database

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import net.adhikary.mrtbuddy.preferences.DATA_STORE_FILE_NAME

/**
 * Created by Pronay Sarker on 12/11/2024 (4:49 AM)
 */

fun createDataStore(): DataStore<Preferences> {
    return createDataStore {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )

        requireNotNull(documentDirectory).path + "/$DATA_STORE_FILE_NAME"
    }
}