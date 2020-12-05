package com.legion1900.dchat.view.chat.newchat.createchat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.legion1900.dchat.domain.chat.usecase.CreateChatUseCase
import com.legion1900.dchat.domain.chat.usecase.SetChatAvatarUseCase
import com.legion1900.dchat.view.util.SingleEvent
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.InputStream

class CreateChatViewModel(
    private val createChat: CreateChatUseCase,
    private val setChatAvatarUseCase: SetChatAvatarUseCase
) : ViewModel() {
    private var selectedAvatar: InputStream? = null
    private var avatarExtension: String? = null

    private val _avatarBytes = MutableLiveData<ByteArray>()
    val avatarBytes: LiveData<ByteArray> = _avatarBytes

    private val _isFinished = MutableLiveData<SingleEvent<Unit>>()
    val isFinished: LiveData<SingleEvent<Unit>> = _isFinished

    private val disposables = CompositeDisposable()

    fun setAvatar(avatar: InputStream, avatarExtension: String) {
        selectedAvatar = avatar
        this.avatarExtension = avatarExtension
        Single.fromCallable { avatar.readBytes() }
            .observeOn(Schedulers.io())
            .subscribe(_avatarBytes::postValue)
            .let(disposables::add)
    }

    fun createChat(name: String, memberIds: List<String>) {
        createChat.createChat(name, memberIds).flatMapCompletable { chatId ->
            _avatarBytes.value?.let {
                setChatAvatarUseCase.setAvatar(chatId, it, avatarExtension!!)
            } ?: Completable.complete()
        }.subscribe { _isFinished.postValue(SingleEvent(Unit)) }
            .let(disposables::add)
    }
}
