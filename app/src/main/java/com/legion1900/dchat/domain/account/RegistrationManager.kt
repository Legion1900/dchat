package com.legion1900.dchat.domain.account

import io.reactivex.Completable

interface RegistrationManager {
    /**
     * Create and initialize new account for specified mnemonic and optional account index
     * @param mnemonic - mnemonic phrase for new account
     * */
    fun createAccount(mnemonic: List<String>): Completable
}
