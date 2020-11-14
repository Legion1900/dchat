package com.legion1900.dchat.view.auth.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.legion1900.dchat.domain.account.MnemonicGenerator
import com.legion1900.dchat.domain.account.MnemonicLength
import io.reactivex.disposables.CompositeDisposable

class CreateMnemonicViewModel(private val mnemonicGenerator: MnemonicGenerator) : ViewModel() {

    var currentLength = CurrentLength.WORDS_12

    private val _shortMnemonic = MutableLiveData<List<String>>()
    val shortMnemonic: LiveData<List<String>> = _shortMnemonic
    private val _mediumMnemonic = MutableLiveData<List<String>>()
    val mediumMnemonic: LiveData<List<String>> = _mediumMnemonic
    private val _isMnemonicReady = MutableLiveData<Boolean>()
    val isMnemonicReady: LiveData<Boolean> = _isMnemonicReady

    private val disposables = CompositeDisposable()

    private var shouldCreateMnemonic = true

    fun createMnemonic() {
        if (shouldCreateMnemonic) {
            mnemonicGenerator.generateMnemonic(MnemonicLength.SHORT)
                .zipWith(mnemonicGenerator.generateMnemonic(MnemonicLength.MEDIUM))
                { short, medium -> short to medium }
                .subscribe { (short, medium) ->
                    _shortMnemonic.postValue(short)
                    _mediumMnemonic.postValue(medium)
                    _isMnemonicReady.postValue(true)
                }.also { disposables.add(it) }
            shouldCreateMnemonic = false
        }
    }

    override fun onCleared() {
        disposables.dispose()
    }
}

enum class CurrentLength {
    WORDS_12, WORDS_24
}
