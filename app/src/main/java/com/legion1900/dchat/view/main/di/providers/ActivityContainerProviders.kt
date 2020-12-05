package com.legion1900.dchat.view.main.di.providers

import com.legion1900.dchat.domain.app.AppStateRepo
import com.legion1900.dchat.domain.chat.ChatRepo
import com.legion1900.dchat.domain.chat.MessageManager
import com.legion1900.dchat.domain.chat.usecase.SyncChatsImpl
import com.legion1900.dchat.domain.chat.usecase.SyncChatsUseCase
import com.legion1900.dchat.view.main.MainViewModel
import com.legion1900.dchat.view.main.di.Provider

fun syncChatsUcProvider(
    repo: () -> ChatRepo,
    manager: () -> MessageManager
): Provider<SyncChatsUseCase> {
    return Provider { SyncChatsImpl(repo(), manager()) }
}

fun mainVmProvider(appStateRepo: AppStateRepo, uc: () -> SyncChatsUseCase): Provider<MainViewModel> {
    return Provider { MainViewModel(appStateRepo, uc()) }
}
