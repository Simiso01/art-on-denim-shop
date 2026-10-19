package za.co.artondenim.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SessionStore(context: Context) {
    private val prefs = EncryptedSharedPreferences.create(
        "aod_session",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun token(): String? = prefs.getString("token", null)?.takeIf { it.isNotBlank() }

    fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    fun clear() {
        prefs.edit().remove("token").apply()
    }

    fun compactCards(): Boolean = prefs.getBoolean("compact_cards", false)

    fun setCompactCards(value: Boolean) {
        prefs.edit().putBoolean("compact_cards", value).apply()
    }

    fun cookieHeader(): String = prefs.getString("cookie", "").orEmpty()

    fun saveCookie(header: String) {
        prefs.edit().putString("cookie", header).apply()
    }
}
