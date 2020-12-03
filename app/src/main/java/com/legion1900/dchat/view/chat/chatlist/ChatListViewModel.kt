package com.legion1900.dchat.view.chat.chatlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.legion1900.dchat.domain.account.ProfileManager
import com.legion1900.dchat.domain.chat.usecase.ChatAvatarLoaded
import com.legion1900.dchat.domain.chat.usecase.ErrorLoadingAvatar
import com.legion1900.dchat.domain.chat.usecase.GetChatsUseCase
import com.legion1900.dchat.domain.chat.usecase.NewChat
import com.legion1900.dchat.domain.dto.chat.ChatModel
import com.legion1900.dchat.domain.inbox.InboxManager
import com.legion1900.dchat.domain.media.PhotoRepo
import com.legion1900.dchat.domain.media.PhotoWidth
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.ConcurrentHashMap

class ChatListViewModel(
    private val profileManager: ProfileManager,
    private val getChatsUseCase: GetChatsUseCase,
    private val photoRepo: PhotoRepo,
    private val inboxManager: InboxManager
) : ViewModel() {

    private val chatModels = ConcurrentHashMap<String, ChatModel>()
    private val _chatList = MutableLiveData<List<ChatModel>>()
    val chatList: LiveData<List<ChatModel>> = _chatList

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> = _userId

    private val _userAvatar = MutableLiveData<ByteArray>()
    val userAvatar: LiveData<ByteArray> = _userAvatar

    private val disposable = CompositeDisposable()

    fun loadProfileInfo() {
        profileManager.getCurrentAccount()
            .subscribe { account ->
                _userName.postValue(account.name)
                _userId.postValue(account.id)
                val avatarId = account.avatarId
                if (avatarId.isNotEmpty()) {
                    loadAvatar(avatarId)
                }
            }.let(disposable::add)
    }

    private fun loadAvatar(avatarId: String) {
        photoRepo.getPhoto(avatarId, PhotoWidth.SMALL)
            .subscribe(_userAvatar::postValue) { e ->
                Log.e("enigma", "error loading user avatar", e)
            }.let(disposable::add)
    }

    fun loadChatList() {
        getChatsUseCase.getChats()
            .subscribe { event ->
                when (event) {
                    is NewChat -> handleNewChat(event.model)
                    is ChatAvatarLoaded -> handleAvatarLoaded(event)
                    is ErrorLoadingAvatar -> handleAvatarError(event)
                }
                _chatList.postValue(chatModels.values.toList())
            }.let(disposable::add)
    }

    private fun handleNewChat(model: ChatModel) {
        chatModels[model.id] = model
    }

    private fun handleAvatarLoaded(event: ChatAvatarLoaded) {
        val newModel = chatModels.getValue(event.id).copy(avatar = event.avatar)
        chatModels[event.id] = newModel
    }

    private fun handleAvatarError(event: ErrorLoadingAvatar) {
        Log.e(
            "enigma",
            "error loading avatar (aID=${event.avatarId} for chat (chatId=${event.chatId})",
            event.e
        )
    }

    override fun onCleared() {
        disposable.dispose()
    }
}
