package com.legion1900.dchat.data.textile.impl

import com.legion1900.dchat.data.textile.abs.*
import io.reactivex.subjects.PublishSubject
import io.textile.pb.Model
import io.textile.textile.FeedItemData
import io.textile.textile.TextileEventListener
import java.lang.Exception
import kotlin.reflect.KClass

class TextileEventBusImpl(
    private val globalListener: TextileEventListener = MockListener
) : TextileEventBus, TextileEventListener by globalListener {

    private val subjects = mapOf(
        newSubject<NodeStateChanged>(),
        newSubject<QueryDone>(),
        newSubject<QueryError>(),
        newSubject<ContactQueryResult>(),
        newSubject<NotificationReceived>(),
        newSubject<ThreadUpdateReceived>()
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : TextileEvent> getEventSubject(eventClass: KClass<T>): PublishSubject<T> {
        return (subjects[eventClass]
            ?: throw IllegalArgumentException("Event ${eventClass.simpleName} is not supported"))
            .let { it as PublishSubject<T> }
    }

    override fun nodeOnline() {
        globalListener.nodeOnline()
        routeEvent(NodeStateChanged(true))
    }

    override fun nodeStopped() {
        globalListener.nodeStopped()
        routeEvent(NodeStateChanged(false))
    }

    override fun queryDone(queryId: String) {
        globalListener.queryDone(queryId)
        routeEvent(QueryDone(queryId))
    }

    override fun queryError(queryId: String, e: Exception) {
        globalListener.queryError(queryId, e)
        routeEvent(QueryError(queryId, e))
    }

    override fun contactQueryResult(queryId: String, contact: Model.Contact) {
        globalListener.contactQueryResult(queryId, contact)
        routeEvent(ContactQueryResult(queryId, contact))
    }

    override fun notificationReceived(notification: Model.Notification) {
        globalListener.notificationReceived(notification)
        routeEvent(NotificationReceived(notification))
    }

    override fun threadUpdateReceived(threadId: String, feedItemData: FeedItemData) {
        globalListener.threadUpdateReceived(threadId, feedItemData)
        routeEvent(ThreadUpdateReceived(threadId, feedItemData))
    }

    private inline fun <reified T : TextileEvent> newSubject(): Pair<KClass<T>, PublishSubject<T>> {
        return T::class to PublishSubject.create()
    }

    private inline fun <reified T : TextileEvent> routeEvent(event: T) {
        getEventSubject(T::class).onNext(event)
    }
}
