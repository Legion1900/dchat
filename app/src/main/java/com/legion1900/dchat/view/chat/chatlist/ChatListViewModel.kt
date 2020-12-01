package com.legion1900.dchat.view.chat.chatlist

import android.util.Log
import androidx.lifecycle.ViewModel
import com.legion1900.dchat.domain.account.ProfileManager
import com.legion1900.dchat.domain.chat.usecase.ChatAvatarLoaded
import com.legion1900.dchat.domain.chat.usecase.ErrorLoadingAvatar
import com.legion1900.dchat.domain.chat.usecase.GetChatsUseCase
import com.legion1900.dchat.domain.chat.usecase.NewChat
import com.legion1900.dchat.domain.dto.Chat
import com.legion1900.dchat.domain.dto.chat.ChatModel
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.ConcurrentHashMap

class ChatListViewModel(
    private val profileManager: ProfileManager,
    private val getChatsUseCase: GetChatsUseCase
) : ViewModel() {

    private val chatModels = ConcurrentHashMap<String, ChatModel>()

    private val disposable = CompositeDisposable()

    fun loadChatList() {
        getChatsUseCase.getChats()
            .subscribe { event ->
                when (event) {
                    is NewChat -> handleNewChat(event.model)
                    is ChatAvatarLoaded -> handleAvatarLoaded(event)
                    is ErrorLoadingAvatar -> handleAvatarError(event)
                }
                Log.d("enigma", "New chat event: $event")
                Log.d("enigma", "Current chat list: ${chatModels.values}")
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

    fun loadProfileInfo() {
        profileManager.getCurrentAccount()
            .subscribe { account -> Log.d("enigma", "Me: $account") }
            .let(disposable::add)
    }

    override fun onCleared() {
        disposable.dispose()
    }
}
