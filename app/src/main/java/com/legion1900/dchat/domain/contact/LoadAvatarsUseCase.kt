package com.legion1900.dchat.domain.contact

import com.legion1900.dchat.domain.dto.Account
import io.reactivex.Observable

interface LoadAvatarsUseCase {
    /**
     * Loads user avatars in parallel in no particular order (i.e. first loaded = first emitted)
     * @return stream of pairs <user id, photo bytes>
     * */
    fun loadAvatars(contacts: List<Account>): Observable<Pair<String, ByteArray>>
}
