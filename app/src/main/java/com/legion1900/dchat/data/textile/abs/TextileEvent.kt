package com.legion1900.dchat.data.textile.abs

import io.textile.pb.Model

sealed class TextileEvent

data class NodeStateChanged(val isRunning: Boolean) : TextileEvent()

abstract class IdBasedEvent(val id: String) : TextileEvent()

class QueryDone(id: String) : IdBasedEvent(id)

class QueryError(id: String, val e: Exception) : IdBasedEvent(id)

class ContactQueryResult(id: String, val contact: Model.Contact) : IdBasedEvent(id)

data class NotificationReceived(val notification: Model.Notification) : TextileEvent()
