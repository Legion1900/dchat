package com.legion1900.dchat.view.util

data class SingleEvent<T>(private val data: T) {
    private var isConsumed = false

    fun getIfNotHandled() = if (!isConsumed) data else null
}
