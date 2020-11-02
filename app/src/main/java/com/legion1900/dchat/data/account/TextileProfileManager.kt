package com.legion1900.dchat.data.account

import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.domain.account.ProfileManager
import com.legion1900.dchat.domain.dto.Account
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.Single
import io.textile.pb.Model
import io.textile.textile.Handlers

class TextileProfileManager(private val proxy: TextileProxy) : ProfileManager {
    override fun getCurrentAccount(): Single<Account> {
        return proxy.instance
            .map { it.account.contact() }
            .map { Account(it.address, it.name, it.avatar) }
    }

    override fun setName(newName: String): Completable {
        return proxy.instance
            .flatMapCompletable { textile ->
                Completable.fromRunnable { textile.profile.setName(newName) }
            }
    }

    override fun setAvatar(imgPath: String): Completable {
        return proxy.instance
            .flatMapCompletable { textile ->
                Completable.create { emitter ->
                    val handler = newBlockHandler(emitter)
                    textile.profile.setAvatar(imgPath, handler)
                }
            }
    }

    private fun newBlockHandler(emitter: CompletableEmitter): Handlers.BlockHandler {
        return object : Handlers.BlockHandler {
            override fun onComplete(block: Model.Block?) {
                if (!emitter.isDisposed)
                    emitter.onComplete()
            }

            override fun onError(e: Exception) {
                if (!emitter.isDisposed)
                    emitter.onError(e)
            }
        }
    }
}
