package com.legion1900.dchat.data.app

import android.content.SharedPreferences
import com.legion1900.dchat.domain.app.AppStateRepo

class SharedPreferAppStateRepo(
    private val sharedPref: SharedPreferences
) : AppStateRepo {
    override fun isLoggedIn(): Boolean {
        return sharedPref.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    override fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPref.edit()
            .putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
            .apply()
    }

    private companion object {
        const val KEY_IS_LOGGED_IN = "IsLoggedIn"
    }
}
