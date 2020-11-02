package com.legion1900.dchat.domain.account

import com.legion1900.dchat.domain.dto.Account
import io.reactivex.Completable
import io.reactivex.Single

interface ProfileManager {
    fun getCurrentAccount(): Single<Account>

    fun setName(newName: String): Completable

    fun setAvatar(imgPath: String): Completable
}
