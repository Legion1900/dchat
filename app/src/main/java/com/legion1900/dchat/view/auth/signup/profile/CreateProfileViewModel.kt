package com.legion1900.dchat.view.auth.signup.profile

import androidx.lifecycle.ViewModel
import com.legion1900.dchat.domain.account.RegistrationManager
import com.legion1900.dchat.domain.app.AppStateRepo

class CreateProfileViewModel(
    private val registrationManager: RegistrationManager,
    private val appStateRepo: AppStateRepo
) : ViewModel() {

}
