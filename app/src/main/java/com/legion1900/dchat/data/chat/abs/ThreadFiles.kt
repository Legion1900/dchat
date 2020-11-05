package com.legion1900.dchat.data.chat.abs

/**
 * @param data - data that was read from thread
 * @param offset - offset to be specified to obtain next chunk of data
 * */
data class ThreadFiles<T>(val data: List<T>, val offset: String)