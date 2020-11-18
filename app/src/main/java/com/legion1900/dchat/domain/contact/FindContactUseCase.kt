package com.legion1900.dchat.domain.contact

import com.legion1900.dchat.domain.dto.Account
import io.reactivex.Flowable

interface FindContactUseCase {
    /**
     * Searches for contacts with specified name/id
     * @param nameOrId - query string with some data about user
     * @param wait - period of time in seconds to wait for result as it is a decentralized p2p net
     * @param limit - limit number of results
     * */
    fun findContact(nameOrId: String, wait: Int, limit: Int): Flowable<Account>
}
