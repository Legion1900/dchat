package com.legion1900.dchat.view.main.di.providers

import com.legion1900.dchat.data.account.TextileMnemonicGenerator
import com.legion1900.dchat.domain.account.MnemonicGenerator
import com.legion1900.dchat.domain.account.ProfileManager
import com.legion1900.dchat.domain.account.RegistrationManager
import com.legion1900.dchat.domain.app.AppStateRepo
import com.legion1900.dchat.domain.app.TmpFileRepo
import com.legion1900.dchat.domain.chat.ChatRepo
import com.legion1900.dchat.view.auth.signup.createmnemonic.CreateMnemonicViewModel
import com.legion1900.dchat.view.auth.signup.createprofile.CreateProfileViewModel
import com.legion1900.dchat.view.chat.ChatListViewModel
import com.legion1900.dchat.view.main.di.Provider

fun mnemonicGeneratorProvider(): Provider<MnemonicGenerator> {
    return Provider { TextileMnemonicGenerator() }
}

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
