package com.legion1900.dchat.view.chat.newchat.createchat

import androidx.lifecycle.ViewModel
import com.legion1900.dchat.domain.chat.usecase.CreateChatUseCase
import com.legion1900.dchat.domain.chat.usecase.SetChatAvatarUseCase

class CreateChatViewModel(
    private val createChat: CreateChatUseCase,
    private val setChatAvatarUseCase: SetChatAvatarUseCase
) : ViewModel() {

}
