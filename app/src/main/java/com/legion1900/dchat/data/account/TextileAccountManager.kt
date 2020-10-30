package com.legion1900.dchat.data.account

import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.domain.account.AccountManager
import com.legion1900.dchat.domain.dto.Account
import io.reactivex.Single
import io.textile.textile.Textile

/**
 * @param rootPath - root directory to store accounts data
 * */
class TextileAccountManager(
    private val proxy: TextileProxy,
    private val rootPath: String,
    private val isDebug: Boolean = false,
    private val logToDisk: Boolean = false,
) : AccountManager {
    override fun currentAccount(): Single<Account> {
        return proxy.instance.map { it.account.contact() }
            .map { Account(it.address, it.name) }
    }

    override fun createNewAccount(name: String): Single<String> {
        return Single.fromCallable {
            val path = "$rootPath/$name"
            Textile.initializeCreatingNewWalletAndAccount(path, isDebug, logToDisk)
        }
    }
}
