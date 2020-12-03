package com.legion1900.dchat.domain.account.usecase

import com.legion1900.dchat.domain.account.RegistrationManager
import com.legion1900.dchat.domain.app.AppStateRepo
import io.reactivex.Completable

class CreateProfileImpl(
    private val registrationManager: RegistrationManager,
    private val appStateRepo: AppStateRepo
) : CreateProfileUseCase {
    override fun createProfile(mnemonic: List<String>): Completable {
        return registrationManager.createAccount(mnemonic)
            .doOnComplete { appStateRepo.setLoggedIn(true) }
    }
}
