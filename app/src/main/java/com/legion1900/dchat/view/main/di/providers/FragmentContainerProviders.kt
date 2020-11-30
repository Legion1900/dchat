package com.legion1900.dchat.view.main.di.providers

import com.legion1900.dchat.data.account.TextileMnemonicGenerator
import com.legion1900.dchat.domain.account.MnemonicGenerator
import com.legion1900.dchat.domain.account.ProfileManager
import com.legion1900.dchat.domain.account.RegistrationManager
import com.legion1900.dchat.domain.app.AppStateRepo
import com.legion1900.dchat.domain.app.TmpFileRepo
import com.legion1900.dchat.domain.chat.AclManager
import com.legion1900.dchat.domain.chat.ChatManager
import com.legion1900.dchat.domain.chat.ChatRepo
import com.legion1900.dchat.domain.chat.usecase.CreateChatImpl
import com.legion1900.dchat.domain.chat.usecase.CreateChatUseCase
import com.legion1900.dchat.domain.chat.usecase.SetChatAvatarImpl
import com.legion1900.dchat.domain.chat.usecase.SetChatAvatarUseCase
import com.legion1900.dchat.domain.contact.*
import com.legion1900.dchat.domain.media.PhotoRepo
import com.legion1900.dchat.view.auth.signup.createmnemonic.CreateMnemonicViewModel
import com.legion1900.dchat.view.auth.signup.createprofile.CreateProfileViewModel
import com.legion1900.dchat.view.chat.addcontact.AddContactViewModel
import com.legion1900.dchat.view.chat.chatlist.ChatListViewModel
import com.legion1900.dchat.view.chat.newchat.createchat.CreateChatViewModel
import com.legion1900.dchat.view.chat.newchat.selectmembers.SelectMembersViewModel
import com.legion1900.dchat.view.main.di.Provider

/*
* Domain interface implementations provision
* */

fun mnemonicGeneratorProvider(): Provider<MnemonicGenerator> {
    return Provider { TextileMnemonicGenerator() }
}

fun findContactUcProvider(manager: () -> ContactManager): Provider<FindContactUseCase> {
    return Provider { FindContactImpl(manager()) }
}

fun loadAvatarsUcProvider(photoRepo: () -> PhotoRepo): Provider<LoadAvatarsUseCase> {
    return Provider { LoadAvatarsImpl(photoRepo()) }
}

fun addContactUcProvider(manager: () -> ContactManager): Provider<AddContactUseCase> {
    return Provider { AddContactImpl(manager()) }
}

fun createChatUcProvider(
    chatRepo: () -> ChatRepo,
    aclManager: () -> AclManager
): Provider<CreateChatUseCase> {
    return Provider { CreateChatImpl(chatRepo(), aclManager()) }
}

fun setChatAvatarUcProvider(
    tmpFileRepo: () -> TmpFileRepo,
    chatManager: () -> ChatManager
): Provider<SetChatAvatarUseCase> {
    return Provider { SetChatAvatarImpl(tmpFileRepo(), chatManager()) }
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
    chatRepo: () -> ChatRepo,
    profileManager: () -> ProfileManager
): Provider<ChatListViewModel> {
    return Provider { ChatListViewModel(chatRepo(), profileManager()) }
}

fun addContactVmProvider(
    findUc: () -> FindContactUseCase,
    loadAvatars: () -> LoadAvatarsUseCase,
    addContactUc: () -> AddContactUseCase
): Provider<AddContactViewModel> {
    return Provider { AddContactViewModel(findUc(), loadAvatars(), addContactUc()) }
}

fun selectMembersVmProvider(
    manager: () -> ContactManager,
    loadAvatars: () -> LoadAvatarsUseCase
): Provider<SelectMembersViewModel> {
    return Provider { SelectMembersViewModel(manager(), loadAvatars()) }
}

fun createChatVmProvider(
    createChat: () -> CreateChatUseCase,
    setChatAvatar: () -> SetChatAvatarUseCase
): Provider<CreateChatViewModel> {
    return Provider { CreateChatViewModel(createChat(), setChatAvatar()) }
}
