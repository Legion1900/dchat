package com.legion1900.dchat.view.main.di.providers

import com.legion1900.dchat.data.account.TextileMnemonicGenerator
import com.legion1900.dchat.data.contact.TextileContactManager
import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.domain.account.MnemonicGenerator
import com.legion1900.dchat.domain.account.ProfileManager
import com.legion1900.dchat.domain.account.RegistrationManager
import com.legion1900.dchat.domain.app.AppStateRepo
import com.legion1900.dchat.domain.app.TmpFileRepo
import com.legion1900.dchat.domain.chat.ChatRepo
import com.legion1900.dchat.domain.contact.ContactManager
import com.legion1900.dchat.domain.contact.FindContactImpl
import com.legion1900.dchat.domain.contact.FindContactUseCase
import com.legion1900.dchat.view.auth.signup.createmnemonic.CreateMnemonicViewModel
import com.legion1900.dchat.view.auth.signup.createprofile.CreateProfileViewModel
import com.legion1900.dchat.view.chat.addcontact.AddContactViewModel
import com.legion1900.dchat.view.chat.chatlist.ChatListViewModel
import com.legion1900.dchat.view.main.di.Provider

/*
* Domain interface implementations provision
* */

fun mnemonicGeneratorProvider(): Provider<MnemonicGenerator> {
    return Provider { TextileMnemonicGenerator() }
}

fun contactManagerProvider(proxy: () -> TextileProxy): Provider<ContactManager> {
    return Provider { TextileContactManager(proxy()) }
}

fun findContactUcProvider(manager: () -> ContactManager): Provider<FindContactUseCase> {
    return Provider { FindContactImpl(manager()) }
}

/*
* ViewModels provision
* */

fun createMnemonicVmProvider(
    generator: () -> MnemonicGenerator
): Provider<CreateMnemonicViewModel> {
    return Provider { CreateMnemonicViewModel(generator()) }
}

fun createProfileVmProvider(
    manager: () -> RegistrationManager,
    appStateRepo: () -> AppStateRepo,
    fileRepo: () -> TmpFileRepo,
    profileManager: () -> ProfileManager
): Provider<CreateProfileViewModel> {
    return Provider {
        CreateProfileViewModel(manager(), appStateRepo(), fileRepo(), profileManager())
    }
}

fun chatListVmProvider(
    chatRepo: () -> ChatRepo
): Provider<ChatListViewModel> {
    return Provider { ChatListViewModel(chatRepo()) }
}

fun addContactVmProvider(findUc: () -> FindContactUseCase): Provider<AddContactViewModel> {
    return Provider { AddContactViewModel((findUc())) }
}
