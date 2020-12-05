package com.legion1900.dchat.view.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.legion1900.dchat.domain.app.AppStateRepo
import com.legion1900.dchat.domain.chat.usecase.SyncChatsUseCase
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainViewModel(
    private val appStateRepo: AppStateRepo,
    private val syncChats: SyncChatsUseCase
) : ViewModel() {
    fun isLoggedIn() = appStateRepo.isLoggedIn()

    private val disposable = CompositeDisposable()

    init {
        if (isLoggedIn()) {
            scheduleSync()
        }
    }

    private fun scheduleSync() {
        val delay = 30L
        val timeUnit = TimeUnit.SECONDS
        Observable.interval(delay, timeUnit, Schedulers.io())
            .doOnNext { Log.d("enigma", "SYNC DONE; next in $delay $timeUnit") }
            .flatMapCompletable { syncChats.syncChats() }
            .subscribe()
    }

    override fun onCleared() {
        disposable.dispose()
    }
}
