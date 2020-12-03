package com.legion1900.dchat.domain.account.usecase

import io.reactivex.Completable

interface CreateProfileUseCase {
    fun createProfile(mnemonic: List<String>): Completable
}
