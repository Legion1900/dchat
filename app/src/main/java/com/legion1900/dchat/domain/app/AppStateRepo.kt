package com.legion1900.dchat.domain.app

interface AppStateRepo {
    fun isLoggedIn(): Boolean
    fun setLoggedIn(isLoggedIn: Boolean)
}
