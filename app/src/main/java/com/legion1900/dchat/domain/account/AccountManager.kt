package com.legion1900.dchat.domain.account

import com.legion1900.dchat.domain.dto.Account
import io.reactivex.Single

interface AccountManager {
    /**
     * @return emits current Account or onComplete if there is no account
     * */
    fun currentAccount(): Single<Account>

    /**
     * Creates new account
     * @return returns mnemonic for this account
     * */
    fun createNewAccount(name: String): Single<String>
}
