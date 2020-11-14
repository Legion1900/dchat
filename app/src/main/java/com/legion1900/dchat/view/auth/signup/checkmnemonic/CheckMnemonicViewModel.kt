package com.legion1900.dchat.view.auth.signup.checkmnemonic

import androidx.lifecycle.ViewModel

class CheckMnemonicViewModel : ViewModel() {
    private val shuffledWords = mutableListOf<String>()
    private val selectedWords = mutableListOf<String>()

    private var shouldShuffle = true

    fun getSelectedWords(): List<String> = selectedWords
    fun getNotSelectedWords(): List<String> = shuffledWords

    fun shuffleWords(words: Array<String>) {
        if (shouldShuffle) {
            shuffledWords += words.slice(0 until USE_WORDS).shuffled()
            shouldShuffle = false
        }
    }

    fun selectWord(word: String) {
        shuffledWords -= word
        selectedWords += word
    }

    fun deselectWord(word: String) {
        selectedWords -= word
        shuffledWords += word
    }

    fun isWordSelected(word: String) = selectedWords.contains(word)

    fun isSelectedCorrect(mnemonic: Array<String>): Boolean {
        return if (selectedWords.size >= MIN_WORDS) {
            var ans = true
            for ((i, word) in selectedWords.withIndex()) {
                ans = word == mnemonic[i]
                if (!ans) break
            }
            ans
        } else false
    }

    companion object {
        const val USE_WORDS = 10
        const val MIN_WORDS = 6
    }
}
