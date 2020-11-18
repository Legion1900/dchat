package com.legion1900.dchat.domain.contact

import com.legion1900.dchat.domain.dto.Account
import io.reactivex.Flowable

class FindContactImpl(private val manager: ContactManager) : FindContactUseCase {
    override fun findContact(nameOrId: String, wait: Int, limit: Int): Flowable<Account> {
        val byName = manager.searchContactByName(nameOrId, limit, wait)
        val byId = searchById(nameOrId, wait)
        return Flowable.concat(byName, byId)
    }

    private fun searchById(id: String, wait: Int): Flowable<Account> {
        return manager.searchContactById(id, wait)
            .toFlowable()
    }
}
