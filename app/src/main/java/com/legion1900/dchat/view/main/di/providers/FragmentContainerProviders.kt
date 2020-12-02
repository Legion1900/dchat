package com.legion1900.dchat.view.main.di.providers

import com.legion1900.dchat.data.account.TextileMnemonicGenerator
import com.legion1900.dchat.domain.account.MnemonicGenerator
import com.legion1900.dchat.domain.account.ProfileManager
import com.legion1900.dchat.domain.account.RegistrationManager
import com.legion1900.dchat.domain.app.AppStateRepo
import com.legion1900.dchat.domain.app.TmpFileRepo
import com.legion1900.dchat.domain.chat.*
import com.legion1900.dchat.domain.chat.usecase.*
import com.legion1900.dchat.domain.contact.*
import com.legion1900.dchat.domain.media.PhotoRepo
import com.legion1900.dchat.view.auth.signup.createmnemonic.CreateMnemonicViewModel
import com.legion1900.dchat.view.auth.signup.createprofile.CreateProfileViewModel
import com.legion1900.dchat.view.chat.addcontact.AddContactViewModel
import com.legion1900.dchat.view.chat.chatlist.ChatListViewModel
import com.legion1900.dchat.view.chat.messagelist.MessageListViewModel
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

fun getChatUcProvider(
    chatRepo: () -> ChatRepo,
    contactManager: () -> ContactManager,
    photoRepo: () -> PhotoRepo
): Provider<GetChatsUseCase> {
    return Provider { GetChatsImpl(chatRepo(), photoRepo(), contactManager()) }
}

fun sendMessageUcProvider(msgManager: () -> MessageManager): Provider<SendMessageUseCase> {
    return Provider { SendMessageImpl(msgManager()) }
}

fun getMessagesUcProvider(
    msgManager: () -> MessageManager,
    msgBus: () -> MessageEventBus,
    contactManager: () -> ContactManager
): Provider<GetMessagesUseCase> {
    return Provider { GetMessageImpl(msgBus(), msgManager(), contactManager()) }
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
    getChat: () -> GetChatsUseCase,
    profileManager: () -> ProfileManager,
): Provider<ChatListViewModel> {
    return Provider { ChatListViewModel(profileManager(), getChat()) }
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

fun messageListVmProvider(
    sendMsgUc: () -> SendMessageUseCase,
    getMsg: () -> GetMessagesUseCase
): Provider<MessageListViewModel> {
    return Provider { MessageListViewModel(sendMsgUc(), getMsg()) }
}
