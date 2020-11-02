package com.legion1900.dchat.data.account

import com.legion1900.dchat.domain.account.RegistrationManager
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import io.textile.textile.Textile

class TextileRegistrationManager(
    private val repoPath: String,
    private val isDebug: Boolean,
    private val isLogToDisk: Boolean
) : RegistrationManager {
    override fun createAccount(mnemonic: List<String>): Completable {
        return Completable.fromRunnable {
            val phrase = mnemonic.joinToString(" ")
            val account = Textile.walletAccountAt(phrase, 0, "")
            Textile.initialize(repoPath, account.seed, isDebug, isLogToDisk)
        }.subscribeOn(Schedulers.io())
    }
}
