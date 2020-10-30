package com.legion1900.dchat.domain.contact

import com.legion1900.dchat.domain.dto.Account
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface ContactManager {
    fun searchContactByName(name: String, limit: Int, wait: Int): Flowable<Account>

    fun searchContactById(id: String, wait: Int): Single<Account>

    fun addContact(id: String): Completable

    fun listContacts(): Single<List<Account>>
}
