package com.legion1900.dchat.data.contact

import com.legion1900.dchat.data.textile.abs.ContactQueryResult
import com.legion1900.dchat.data.textile.abs.QueryDone
import com.legion1900.dchat.data.textile.abs.QueryError
import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.domain.contact.ContactManager
import com.legion1900.dchat.domain.dto.Account
import io.reactivex.*
import io.textile.pb.Model
import io.textile.pb.QueryOuterClass
import mobile.SearchHandle

class TextileContactManager(
    private val proxy: TextileProxy
) : ContactManager {

    override fun searchContactByName(name: String, limit: Int, wait: Int): Flowable<Account> {
        return searchInternal(wait, limit, name = name)
            .toObservableModel()
            .map { toAccount(it) }
            .toFlowable(BackpressureStrategy.BUFFER)
    }

    override fun searchContactById(id: String, wait: Int): Single<Account> {
        return searchInternal(wait, 1, address = id)
            .toSingleModel()
            .map { toAccount(it) }
    }

    override fun addContact(id: String): Completable {
        return searchInternal(DEFAULT_WAIT, 1, address = id)
            .toSingleModel()
            .zipWith(proxy.instance) { contact, textile ->
                textile.contacts.add(contact)
            }.flatMapCompletable { Completable.complete() }
    }

    override fun listContacts(): Single<List<Account>> {
        return proxy.instance
            .map { it.contacts.list().itemsList }
            .flatMap { contacts ->
                if (contacts.isNotEmpty())
                    Observable.fromIterable(contacts)
                        .map { toAccount(it) }
                        .buffer(contacts.size)
                        .firstOrError()
                else Single.just(emptyList())
            }
    }

    override fun getContact(id: String): Maybe<Account> {
        return proxy.instance
            .filter { it.contacts.list().itemsCount > 0 }
            .flatMap { textile ->
                try {
                    Maybe.just(textile.contacts[id])
                } catch (e: NullPointerException) {
                    Maybe.empty()
                }
            }
            .map { Account(it.address, it.name, it.avatar) }
    }

    private fun newQueryOptions(wait: Int, limit: Int): QueryOuterClass.QueryOptions {
        return QueryOuterClass.QueryOptions.newBuilder()
            .setWait(wait)
            .setLimit(limit)
            .build()
    }

    private fun newContactQuery(
        name: String? = null,
        address: String? = null
    ): QueryOuterClass.ContactQuery {
        val builder = QueryOuterClass.ContactQuery.newBuilder()
        name?.let { builder.name = it }
        address?.let { builder.address = it }
        return builder.build()
    }

    private fun searchInternal(
        wait: Int,
        limit: Int,
        name: String? = null,
        address: String? = null
    ): Single<SearchHandle> {
        val options = newQueryOptions(wait, limit)
        val query = newContactQuery(name = name, address = address)
        return proxy.instance.map { it.contacts.search(query, options) }
    }

    private fun Single<SearchHandle>.toObservableModel(): Observable<Model.Contact> {
        return flatMapObservable { handle ->
            val resultEvents = proxy.eventBus.getEventSubject(ContactQueryResult::class)
                .filter { it.id == handle.id }
            val completeEvent = proxy.eventBus.getEventSubject(QueryDone::class)
                .filter { it.id == handle.id }
            val errorEvent = proxy.eventBus.getEventSubject(QueryError::class)
                .filter { it.id == handle.id }
            Observable.merge(resultEvents, completeEvent, errorEvent)
                .flatMap { if (it is QueryError) Observable.error(it.e) else Observable.just(it) }
                .flatMap { if (it is QueryDone) Observable.empty() else Observable.just(it) }
                .map { it as ContactQueryResult }
                .map { it.contact }
        }
    }

    private fun Single<SearchHandle>.toSingleModel(): Single<Model.Contact> {
        return flatMap { handle ->
            val successEvent = proxy.eventBus.getEventSubject(ContactQueryResult::class)
                .filter { it.id == handle.id }
                .firstOrError()
            val errorEvent = proxy.eventBus.getEventSubject(QueryError::class)
                .filter { it.id == handle.id }
                .firstOrError()
            Single.ambArray(successEvent, errorEvent)
                .flatMap { if (it is QueryError) Single.error(it.e) else Single.just(it) }
                .map { it as ContactQueryResult }
                .map { it.contact }
        }
    }

    private fun toAccount(c: Model.Contact) = Account(c.address, c.name, c.avatar)

    private companion object {
        const val DEFAULT_WAIT = 10
    }
}
