package com.legion1900.dchat.view.chat.addcontact

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.legion1900.dchat.domain.contact.AddContactResult
import com.legion1900.dchat.domain.contact.AddContactUseCase
import com.legion1900.dchat.domain.contact.FindContactUseCase
import com.legion1900.dchat.domain.contact.LoadAvatarsUseCase
import com.legion1900.dchat.domain.dto.Account
import com.legion1900.dchat.view.util.SingleEvent
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.ConcurrentHashMap

class AddContactViewModel(
    private val findContact: FindContactUseCase,
    private val loadAvatars: LoadAvatarsUseCase,
    private val addContact: AddContactUseCase
) : ViewModel() {
    private val _result = MutableLiveData<List<Account>>()
    val result: LiveData<List<Account>> = _result

    private val _photos = ConcurrentHashMap<String, ByteArray>()
    val photos: Map<String, ByteArray> = _photos

    private val _lastLoadedPhoto = MutableLiveData<SingleEvent<Pair<String, ByteArray>>>()
    val lastLoadedPhoto: LiveData<SingleEvent<Pair<String, ByteArray>>> = _lastLoadedPhoto

    private val _addStatus = MutableLiveData<SingleEvent<Pair<String, AddContactResult>>>()
    val addStatus: LiveData<SingleEvent<Pair<String, AddContactResult>>> = _addStatus

    private val disposable = CompositeDisposable()

    fun searchFor(nameOrId: String) {
        findContact.findContact(nameOrId, WAIT, LIMIT)
            .buffer(LIMIT)
            .first(emptyList())
            .subscribe(
                {
                    _result.postValue(it)
                    loadAvatars(it)
                },
                { logError("Error while searching for user", it) }
            )
            .let(disposable::add)
    }

    fun addContact(id: String) {
        addContact.addToContacts(id)
            .subscribe(
                { result ->
                    val name = _result.value!!.first { it.id == id }.name
                    _addStatus.postValue(SingleEvent(name to result))
                },
                { logError("Error while adding to contacts uid=$id", it) }
            ).let(disposable::add)
    }

    private fun loadAvatars(accounts: List<Account>) {
        loadAvatars.loadAvatars(accounts)
            .subscribe(
                { idPhoto ->
                    _lastLoadedPhoto.postValue(SingleEvent(idPhoto))
                    _photos[idPhoto.first] = idPhoto.second
                },
                { logError("Error while loading photo", it) }
            )
            .let(disposable::add)
    }

    private fun logError(msg: String, e: Throwable) {
        Log.e("enigma", msg, e)
    }

    override fun onCleared() {
        disposable.dispose()
    }

    companion object {
        const val WAIT = 5
        const val LIMIT = 100
    }
}
