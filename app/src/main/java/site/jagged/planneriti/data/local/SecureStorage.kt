package site.jagged.planneriti.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class SecureStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "planneriti_secure",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveAuthToken(token: String) = prefs.edit { putString("auth_token", token) }
    fun getAuthToken(): String? = prefs.getString("auth_token", null)
    fun clearAuthToken() = prefs.edit { remove("auth_token") }

    fun saveCredentials(email: String, password: String) {
        prefs.edit { putString("email", email).putString("password", password) }
    }
    fun getEmail(): String? = prefs.getString("email", null)
    fun getPassword(): String? = prefs.getString("password", null)
    fun clearCredentials() = prefs.edit { remove("email").remove("password") }

    fun clearAll() = prefs.edit { clear() }
}