package com.budi.setiawan.storyappbudisetiawan.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.budi.setiawan.storyappbudisetiawan.data.item.UserItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SharedPrefUserLogin private constructor(private val dataStore: DataStore<Preferences>) {
    fun getUser(): Flow<UserItems> {
        return dataStore.data.map { preferences ->
            UserItems(
                id = preferences[ID_KEY],
                email = preferences[EMAIL_KEY],
                name = preferences[NAME_KEY],
                token = preferences[TOKEN_KEY],
                password = preferences[PASSWORD_KEY],
                isLoggedIn = preferences[STATE_KEY]
            )
        }
    }

    suspend fun updateUser(user: UserItems) {
        dataStore.edit { preferences ->
            user.id?.let { preferences[ID_KEY] = it }
            user.email?.let { preferences[EMAIL_KEY] = it }
            user.name?.let { preferences[NAME_KEY] = it }
            user.token?.let { preferences[TOKEN_KEY] = it }
            user.password?.let { preferences[PASSWORD_KEY] = it }
            user.isLoggedIn?.let { preferences[STATE_KEY] = it }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SharedPrefUserLogin? = null

        private val ID_KEY = stringPreferencesKey("id")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val NAME_KEY = stringPreferencesKey("name")
        private val PASSWORD_KEY = stringPreferencesKey("password")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val STATE_KEY = booleanPreferencesKey("state")

        fun getInstance(dataStore: DataStore<Preferences>): SharedPrefUserLogin {
            return INSTANCE ?: synchronized(this) {
                val instance = SharedPrefUserLogin(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}