package com.legion1900.dchat.view.main.navigation

import androidx.navigation.NavDirections

fun interface DirectionProvider {
    fun provideDirection(): NavDirections
}
