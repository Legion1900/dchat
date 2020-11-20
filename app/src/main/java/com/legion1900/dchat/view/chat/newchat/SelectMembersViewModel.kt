package com.legion1900.dchat.view.chat.newchat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.legion1900.dchat.domain.contact.ContactManager
import com.legion1900.dchat.domain.contact.LoadAvatarsUseCase
import com.legion1900.dchat.domain.dto.Account
import com.legion1900.dchat.view.util.SingleEvent
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.ConcurrentHashMap

class SelectMembersViewModel(
    private val contactManager: ContactManager,
    private val loadAvatarsUseCase: LoadAvatarsUseCase
) : ViewModel() {
    private val disposable = CompositeDisposable()

    private val contacts = MutableLiveData<List<Account>>()

    // <userId, avatarBytes>
    private val avatars = ConcurrentHashMap<String, ByteArray>()
    private val lastLoadedAvatar = MutableLiveData<SingleEvent<Pair<String, ByteArray>>>()

    /**
     * Returns already loaded avatars
     * */
    fun getContacts(): LiveData<List<Account>> = contacts

    fun getAvatarsCache(): Map<String, ByteArray> = avatars

    fun getLastLoadedAvatar(): LiveData<SingleEvent<Pair<String, ByteArray>>> = lastLoadedAvatar

    fun loadContacts() {
        contactManager.listContacts()
            .subscribe { contacts ->
                this.contacts.postValue(contacts)
                startLoadingAvatars(contacts)
            }.let(disposable::add)
    }

    private fun startLoadingAvatars(contacts: List<Account>) {
        loadAvatarsUseCase.loadAvatars(contacts)
            .subscribe(
                { userIdPhoto ->
                    lastLoadedAvatar.postValue(SingleEvent(userIdPhoto))
                    avatars[userIdPhoto.first] = userIdPhoto.second
                },
                { Log.e("enigma", "Error while loading avatars", it) }
            ).let(disposable::add)
    }
}
