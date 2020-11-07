package com.legion1900.dchat.data.textile.abs

/**
 * @param data - data that was read from thread
 * @param offset - offset to be specified to obtain next chunk of data; null if there is no data
 * available
 * */
data class ThreadFiles<T>(val data: List<T>, val offset: String?)
