package id.co.asiaraya.hris.core.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import id.co.asiaraya.hris.core.model.Employee
import id.co.asiaraya.hris.core.model.Tokens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "hris_user_session")

class UserSessionStore(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        val KEY_SERVER_URL = stringPreferencesKey("server_url")
        val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val KEY_EXPIRES_AT = stringPreferencesKey("expires_at")
        val KEY_EMPLOYEE = stringPreferencesKey("employee_json")
        val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        const val DEFAULT_SERVER_URL = "https://hris.asiaraya.co.id"
    }

    val serverUrlFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_SERVER_URL] ?: DEFAULT_SERVER_URL
    }

    val accessTokenFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_ACCESS_TOKEN]
    }

    val employeeFlow: Flow<Employee?> = context.dataStore.data.map { prefs ->
        prefs[KEY_EMPLOYEE]?.let { raw ->
            try {
                json.decodeFromString<Employee>(raw)
            } catch (e: Exception) {
                null
            }
        }
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_IS_LOGGED_IN] == true && !prefs[KEY_ACCESS_TOKEN].isNullOrBlank()
    }

    suspend fun getServerUrl(): String {
        return serverUrlFlow.first()
    }

    suspend fun getAccessToken(): String? {
        return context.dataStore.data.first()[KEY_ACCESS_TOKEN]
    }

    suspend fun getRefreshToken(): String? {
        return context.dataStore.data.first()[KEY_REFRESH_TOKEN]
    }

    suspend fun saveSession(tokens: Tokens, employee: Employee) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = tokens.accessToken
            prefs[KEY_REFRESH_TOKEN] = tokens.refreshToken
            prefs[KEY_EXPIRES_AT] = tokens.expiresAt
            prefs[KEY_EMPLOYEE] = json.encodeToString(Employee.serializer(), employee)
            prefs[KEY_IS_LOGGED_IN] = true
        }
    }

    suspend fun updateEmployee(employee: Employee) {
        context.dataStore.edit { prefs ->
            prefs[KEY_EMPLOYEE] = json.encodeToString(Employee.serializer(), employee)
        }
    }

    suspend fun saveServerUrl(url: String) {
        val normalized = normalizeUrl(url)
        context.dataStore.edit { prefs ->
            prefs[KEY_SERVER_URL] = normalized
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_ACCESS_TOKEN)
            prefs.remove(KEY_REFRESH_TOKEN)
            prefs.remove(KEY_EXPIRES_AT)
            prefs.remove(KEY_EMPLOYEE)
            prefs[KEY_IS_LOGGED_IN] = false
        }
    }

    fun normalizeUrl(input: String): String {
        var clean = input.trim()
        if (!clean.startsWith("http://") && !clean.startsWith("https://")) {
            clean = "https://$clean"
        }
        if (clean.endsWith("/")) {
            clean = clean.dropLast(1)
        }
        if (clean.endsWith("/api/v1")) {
            clean = clean.removeSuffix("/api/v1")
        }
        return clean
    }
}
