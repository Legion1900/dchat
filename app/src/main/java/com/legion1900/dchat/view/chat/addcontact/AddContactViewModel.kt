package com.legion1900.dchat.view.chat.addcontact

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.legion1900.dchat.domain.contact.FindContactUseCase
import com.legion1900.dchat.domain.dto.Account
import com.legion1900.dchat.domain.media.PhotoRepo
import com.legion1900.dchat.domain.media.PhotoWidth
import com.legion1900.dchat.view.util.SingleEvent
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.ConcurrentHashMap

class AddContactViewModel(
    private val findContact: FindContactUseCase,
    private val photoRepo: PhotoRepo
) : ViewModel() {
    private val _result = MutableLiveData<List<Account>>()
    val result: LiveData<List<Account>> = _result

    private val _photos = ConcurrentHashMap<String, ByteArray>()
    val photos: Map<String, ByteArray> = _photos

    private val _lastLoadedPhoto = MutableLiveData<SingleEvent<Pair<String, ByteArray>>>()
    private val lastLoadedPhoto: LiveData<SingleEvent<Pair<String, ByteArray>>> = _lastLoadedPhoto

    private val disposable = CompositeDisposable()

    fun searchFor(nameOrId: String) {
        findContact.findContact(nameOrId, WAIT, LIMIT)
            .buffer(LIMIT)
            .first(emptyList())
            .subscribe(_result::postValue) { logError("Error while searching for user", it) }
            .let(disposable::add)
    }

    private fun loadAvatars(accounts: List<Account>) {
        val accountsWithAvatars = accounts.filter { it.avatarId.isNotEmpty() }
        Observable.fromIterable(accountsWithAvatars)
            .map { it.id to it.avatarId }
            .concatMapSingle { (userId, photoId) ->
                photoRepo.getPhoto(photoId, PhotoWidth.SMALL)
                    .map { userId to it }
            }.doOnNext { _lastLoadedPhoto.postValue(SingleEvent(it)) }
            .subscribe(
                { (userId, photo) -> _photos[userId] = photo },
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
