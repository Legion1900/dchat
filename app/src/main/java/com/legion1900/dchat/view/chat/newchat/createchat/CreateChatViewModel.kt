package com.legion1900.dchat.view.chat.newchat.createchat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.legion1900.dchat.domain.chat.usecase.CreateChatUseCase
import com.legion1900.dchat.domain.chat.usecase.SetChatAvatarUseCase
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

    private val disposables = CompositeDisposable()

    fun setAvatar(avatar: InputStream, avatarExtension: String) {
        selectedAvatar = avatar
        this.avatarExtension = avatarExtension
        Single.fromCallable { avatar.readBytes() }
            .observeOn(Schedulers.io())
            .subscribe(_avatarBytes::postValue)
            .let(disposables::add)
    }
}
