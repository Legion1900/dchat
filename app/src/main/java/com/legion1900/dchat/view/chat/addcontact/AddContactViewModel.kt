package com.legion1900.dchat.view.chat.addcontact

import androidx.lifecycle.ViewModel
import com.legion1900.dchat.domain.contact.FindContactUseCase

class AddContactViewModel(
    private val findContact: FindContactUseCase,
) : ViewModel() {
}
