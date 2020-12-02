package com.legion1900.dchat.view.chat.messagelist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.legion1900.dchat.domain.chat.usecase.GetMessagesUseCase
import com.legion1900.dchat.domain.chat.usecase.SendMessageUseCase
import com.legion1900.dchat.domain.dto.message.MessageModel
import com.legion1900.dchat.view.util.SingleEvent
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.Comparator

class MessageListViewModel(
    private val sendMsg: SendMessageUseCase,
    private val getMsg: GetMessagesUseCase
) : ViewModel() {

    private var isLoadingMsg = false
    private val msgLiveData = MutableLiveData<List<MessageModel>>()
    private val messages = ConcurrentSkipListSet<MessageModel> { o1, o2 ->
        val t1 = o1.timestamp
        val t2 = o2.timestamp
        when {
            t1 > t2 -> -1
            t1 == t2 -> 0
            else -> 1
        }
    }

    private val _isMsgSent = MutableLiveData<SingleEvent<Boolean>>()
    val isMsgSent: LiveData<SingleEvent<Boolean>> = _isMsgSent

    private val disposable = CompositeDisposable()

    fun sendMessage(chatId: String, text: String) {
        sendMsg.sendMessage(chatId, text, null)
            .subscribe { _isMsgSent.postValue(SingleEvent(true)) }
            .let(disposable::add)
    }

    fun loadMessages(chatId: String): LiveData<List<MessageModel>> {
        if (!isLoadingMsg) {
            isLoadingMsg = true
            getMsg.getMessages(chatId).subscribe { model ->
                messages.add(model)
                Log.d("enigma", "messages: $messages")
                msgLiveData.postValue(messages.toList())
            }.let(disposable::add)
        }
        return msgLiveData
    }


}
