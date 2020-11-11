package com.legion1900.dchat.view.main.di

fun interface Provider<T> {
    fun provide(): T
}
