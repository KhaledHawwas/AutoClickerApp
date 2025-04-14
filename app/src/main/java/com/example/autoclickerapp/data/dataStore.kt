package com.example.autoclickerapp.data

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.autoclickerapp.model.Pos
import com.example.autoclickerapp.model.toPos
import com.example.autoclickerapp.model.toStringPos
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class DataStoreManager(context: Context) {

    private val dataStore = context.dataStore

    // Define keys for each field
    companion object {
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val EMAIL_KEY = stringPreferencesKey("email")
        val PHONE_NUMBER_KEY = stringPreferencesKey("phone_number")
        val CLICK_POSITION_KEY = stringPreferencesKey("click_position")
    }

    // Save data to DataStore
    suspend fun saveUserData(userName: String, email: String, phoneNumber: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = userName
            preferences[EMAIL_KEY] = email
            preferences[PHONE_NUMBER_KEY] = phoneNumber
        }
    }
    suspend fun saveClickPosition(pos: Pos) {
        dataStore.edit { preferences ->
            preferences[CLICK_POSITION_KEY] = pos.toStringPos()
        }
    }

    // Retrieve the stored user data
    val userNameFlow: Flow<String?> = dataStore.data
        .map { preferences -> preferences[USER_NAME_KEY] }

    val emailFlow: Flow<String?> = dataStore.data
        .map { preferences -> preferences[EMAIL_KEY] }

    val phoneNumberFlow: Flow<String?> = dataStore.data
        .map { preferences -> preferences[PHONE_NUMBER_KEY] }
    val clickPositionFlow: Flow<Pos?> = dataStore.data
        .map { preferences -> preferences[CLICK_POSITION_KEY]?.toPos() }

}
