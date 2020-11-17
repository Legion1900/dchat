package com.legion1900.dchat.view.auth.signup.createprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.legion1900.dchat.domain.account.ProfileManager
import com.legion1900.dchat.domain.account.RegistrationManager
import com.legion1900.dchat.domain.app.AppStateRepo
import com.legion1900.dchat.domain.app.TmpFileRepo
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.InputStream

class CreateProfileViewModel(
    private val registrationManager: RegistrationManager,
    private val appStateRepo: AppStateRepo,
    private val fileRepo: TmpFileRepo,
    private val profileManager: ProfileManager
) : ViewModel() {

    private var avatarExt: String? = null
    private var avatarStream: InputStream? = null

    private val _avatar = MutableLiveData<ByteArray>()
    val avatar: LiveData<ByteArray> = _avatar

    private val _isCreated = MutableLiveData<Boolean>()
    val isCreated: LiveData<Boolean> = _isCreated

    private val disposables = CompositeDisposable()

    fun isNameValid(name: String): Boolean = name.isNotEmpty()

    fun createAccount(mnemonic: List<String>, name: String) {
        registrationManager.createAccount(mnemonic)
            .andThen(setAvatar())
            .subscribe {
                val acc = profileManager.getCurrentAccount().blockingGet()
                appStateRepo.setLoggedIn(true)
                _isCreated.postValue(true)
            }.let(disposables::add)
    }

    fun openAvatar(stream: InputStream, extension: String) {
        avatarStream = stream
        avatarExt = extension
        Single.fromCallable { stream.readBytes() }
            .observeOn(Schedulers.io())
            .subscribe { bytes -> _avatar.postValue(bytes) }
            .let(disposables::add)
    }

    override fun onCleared() {
        disposables.dispose()
    }

    private fun setAvatar(): Completable {
        val avatarFile = avatarStream?.let {
            fileRepo.writeFile(avatar.value!!, "$TMP_AVATAR.$avatarExt")
        } ?: Single.just(File(""))
        return avatarFile
            .flatMapCompletable { profileManager.setAvatar(it.absolutePath) }
    }

    companion object {
        const val TMP_AVATAR = "avatar"
    }
}
