package com.legion1900.dchat.domain.contact

import io.reactivex.Single

class AddContactImpl(private val contactManager: ContactManager) : AddContactUseCase {
    override fun contactId(id: String): Single<AddContactResult> {
        return contactManager.listContacts()
            .map { it.map { contact -> contact.id } }
            .map { it.contains(id) }
            .addIfNeeded(id)
    }

    private fun Single<Boolean>.addIfNeeded(id: String): Single<AddContactResult> {
        return flatMap { isKnown ->
            if (isKnown) Single.just(AddContactResult.ALREADY_KNOWN)
            else contactManager.addContact(id).andThen(Single.just(AddContactResult.ADDED))
        }
    }
}
