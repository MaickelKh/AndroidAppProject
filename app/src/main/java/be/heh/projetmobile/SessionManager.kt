package be.heh.projetmobile

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    fun userLogin(userId: String, role: String, name: String) {
        val editor = prefs.edit()
        editor.putString("USER_ID", userId)
        editor.putString("USER_ROLE", role)
        editor.putString("USER_NAME", name)
        editor.apply()
    }

    fun isUserLoggedIn(): Boolean {
        return prefs.contains("USER_ID")
    }

    fun userLogout() {
        val editor = prefs.edit()
        editor.remove("USER_ID")
        editor.remove("USER_ROLE")
        editor.remove("USER_NAME")
        editor.apply()
    }

    fun getUserId(): String? {
        return prefs.getString("USER_ID", null)
    }

    fun getUserRole(): String? {
        return prefs.getString("USER_ROLE", null)
    }

    fun getUserName(): String? {
        return prefs.getString("USER_NAME", null)
    }
}