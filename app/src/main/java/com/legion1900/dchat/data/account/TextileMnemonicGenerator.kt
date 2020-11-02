package com.legion1900.dchat.data.account

import com.legion1900.dchat.domain.account.MnemonicGenerator
import com.legion1900.dchat.domain.account.MnemonicLength
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.textile.textile.Textile

class TextileMnemonicGenerator : MnemonicGenerator {
    override fun generateMnemonic(length: MnemonicLength): Single<List<String>> {
        return Single.fromCallable { Textile.newWallet(length.wordCnt.toLong()) }
            .map { it.split(' ') }
            .subscribeOn(Schedulers.computation())
    }
}
