package com.legion1900.dchat.view.chat.messagelist

import android.util.Log
import androidx.lifecycle.ViewModel
import com.legion1900.dchat.domain.chat.usecase.SendMessageUseCase

class MessageListViewModel(private val sendMsg: SendMessageUseCase) : ViewModel() {
    fun sendMessage(chatId: String, text: String) {
        sendMsg.sendMessage(chatId, text, null)
            .subscribe { Log.d("enigma", "Message sent!!") }
    }
}
