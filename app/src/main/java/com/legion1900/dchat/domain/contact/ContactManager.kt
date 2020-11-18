package com.legion1900.dchat.domain.contact

import com.legion1900.dchat.domain.dto.Account
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

interface ContactManager {
    // TODO: move this method to separate interface; this must be a repo
    fun searchContactByName(name: String, limit: Int, wait: Int): Flowable<Account>

    fun searchContactById(id: String, wait: Int): Maybe<Account>

    fun addContact(id: String): Completable

    fun listContacts(): Single<List<Account>>

    fun getContact(id: String): Maybe<Account>
}
