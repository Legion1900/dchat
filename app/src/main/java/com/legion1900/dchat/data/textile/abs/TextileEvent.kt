package com.legion1900.dchat.data.textile.abs

import io.textile.pb.Model

sealed class TextileEvent

data class NodeStateChanged(val isRunning: Boolean) : TextileEvent()

data class QueryDone(val id: String) : TextileEvent()

data class QueryError(val id: String, val e: Exception) : TextileEvent()

data class ContactQueryResult(val id: String, val contact: Model.Contact) : TextileEvent()
