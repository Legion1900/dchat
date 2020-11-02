package com.legion1900.dchat.domain.account

import io.reactivex.Single

interface MnemonicGenerator {
    fun generateMnemonic(length: MnemonicLength): Single<List<String>>
}

enum class MnemonicLength(val wordCnt: Int) {
    SHORT(12), MEDIUM(24)
}
