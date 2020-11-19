package com.legion1900.dchat.domain.contact

import io.reactivex.Single

interface AddContactUseCase {
    fun contactId(id: String): Single<AddContactResult>
}

enum class AddContactResult {
    ADDED, ALREADY_KNOWN
}
