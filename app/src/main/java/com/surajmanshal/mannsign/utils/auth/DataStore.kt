package com.surajmanshal.mannsign.utils.auth

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore



object DataStore {
    private const val PREFERENCES_AUTH = "auth"
    const val JWT_TOKEN = "jwt"
    val Context.preferenceDataStoreAuth : androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(
        PREFERENCES_AUTH
    )
}