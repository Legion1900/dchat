package com.legion1900.dchat.view.auth.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.legion1900.dchat.domain.account.usecase.CreateProfileUseCase
import io.reactivex.disposables.CompositeDisposable

class EnterMnemonicViewModel(private val createProfile: CreateProfileUseCase) : ViewModel() {

    private val _isCreated = MutableLiveData<Boolean>()
    val isCreated: LiveData<Boolean> = _isCreated

    private val disposable = CompositeDisposable()

    fun createProfile(mnemonic: List<String>) {
        createProfile.createProfile(mnemonic)
            .subscribe { _isCreated.postValue(true) }
            .let(disposable::add)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}
