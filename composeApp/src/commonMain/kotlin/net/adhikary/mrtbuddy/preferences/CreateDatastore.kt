package net.adhikary.mrtbuddy.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

/**
 * Created by Pronay Sarker on 12/11/2024 (4:34 AM)
 */
 fun createDataStore (producePath : () -> String) : DataStore<Preferences>{
     return PreferenceDataStoreFactory.createWithPath(
         produceFile = { producePath().toPath() }
     )
 }

internal const val DATA_STORE_FILE_NAME = "prefs.preferences_pb"
