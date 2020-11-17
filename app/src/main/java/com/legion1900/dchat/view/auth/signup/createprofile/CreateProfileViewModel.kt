package com.legion1900.dchat.view.auth.signup.createprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.legion1900.dchat.domain.account.RegistrationManager
import com.legion1900.dchat.domain.app.AppStateRepo
import com.legion1900.dchat.domain.app.TmpFileRepo
import io.reactivex.disposables.CompositeDisposable

class CreateProfileViewModel(
    private val registrationManager: RegistrationManager,
    private val appStateRepo: AppStateRepo,
    private val fileRepo: TmpFileRepo
) : ViewModel() {

    private val _isCreated = MutableLiveData<Boolean>()
    val isCreated: LiveData<Boolean> = _isCreated

    private val disposables = CompositeDisposable()

    fun isNameValid(name: String): Boolean = name.isNotEmpty()

    fun createAccount(mnemonic: List<String>) {
        registrationManager.createAccount(mnemonic).subscribe {
            appStateRepo.setLoggedIn(true)
            _isCreated.postValue(true)
        }.let(disposables::add)
    }

    override fun onCleared() {
        disposables.dispose()
    }
}
