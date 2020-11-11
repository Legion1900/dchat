package com.legion1900.dchat.view.main

import androidx.lifecycle.ViewModel
import com.legion1900.dchat.domain.app.AppStateRepo

class MainViewModel(private val appStateRepo: AppStateRepo) : ViewModel() {
    fun isLoggedIn() = appStateRepo.isLoggedIn()
}
