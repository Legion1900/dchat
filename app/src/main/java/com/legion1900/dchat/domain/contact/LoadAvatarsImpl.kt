package com.legion1900.dchat.domain.contact

import com.legion1900.dchat.domain.dto.Account
import com.legion1900.dchat.domain.media.PhotoRepo
import com.legion1900.dchat.domain.media.PhotoWidth
import io.reactivex.Observable

class LoadAvatarsImpl(private val photoRepo: PhotoRepo) : LoadAvatarsUseCase {
    override fun loadAvatars(contacts: List<Account>): Observable<Pair<String, ByteArray>> {
        val accountsWithAvatars = contacts.filter { it.avatarId.isNotEmpty() }
        val tasks = Array(accountsWithAvatars.size) { loadAvatar(accountsWithAvatars[it]) }
        return Observable.merge(tasks.toList())
    }

    private fun loadAvatar(forUser: Account): Observable<Pair<String, ByteArray>> {
        return photoRepo.getPhoto(forUser.avatarId, PhotoWidth.SMALL)
            .map { forUser.id to it }
            .toObservable()
            .onErrorReturn { forUser.id to ByteArray(0) }
            .takeWhile { (_, avatar) -> avatar.isNotEmpty() }
    }
}
