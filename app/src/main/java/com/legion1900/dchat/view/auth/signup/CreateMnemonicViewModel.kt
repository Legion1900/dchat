package com.legion1900.dchat.view.auth.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.legion1900.dchat.domain.account.MnemonicGenerator
import com.legion1900.dchat.domain.account.MnemonicLength
import io.reactivex.disposables.CompositeDisposable

class CreateMnemonicViewModel(private val mnemonicGenerator: MnemonicGenerator) : ViewModel() {
    private val _mnemonic = MutableLiveData<List<String>>()
    val mnemonic: LiveData<List<String>> = _mnemonic

    private val disposables = CompositeDisposable()

    fun createMnemonic(length: MnemonicLength) {
        mnemonicGenerator.generateMnemonic(length)
            .subscribe { words ->
                _mnemonic.postValue(words)
            }
            .let { disposables.add(it) }
    }

    override fun onCleared() {
        disposables.dispose()
    }
}
