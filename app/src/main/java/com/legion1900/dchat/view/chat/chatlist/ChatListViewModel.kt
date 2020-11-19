package com.legion1900.dchat.view.chat.chatlist

import android.util.Log
import androidx.lifecycle.ViewModel
import com.legion1900.dchat.domain.account.ProfileManager
import com.legion1900.dchat.domain.chat.ChatRepo
import io.reactivex.disposables.CompositeDisposable

class ChatListViewModel(
    private val chatRepo: ChatRepo,
    private val profileManager: ProfileManager
) : ViewModel() {

    private val disposable = CompositeDisposable()

    fun loadProfileInfo() {
        profileManager.getCurrentAccount()
            .subscribe { account -> Log.d("enigma", "Me: $account") }
            .let(disposable::add)
    }

    override fun onCleared() {
        disposable.dispose()
    }
}
