package com.legion1900.dchat.data.textile.impl

import io.textile.pb.Model
import io.textile.textile.BaseTextileEventListener
import io.textile.textile.FeedItemData
import io.textile.textile.TextileEventListener

object MockListener : BaseTextileEventListener()

private typealias E = TextileEventListener

class CompositeListener(private vararg val delegates: TextileEventListener) : TextileEventListener {

    override fun nodeStarted() {
        routeEvent(E::nodeStarted)
    }

    override fun nodeFailedToStart(e: Exception) {
        routeEvent(E::nodeFailedToStart, e)
    }

    override fun nodeStopped() {
        routeEvent(E::nodeStopped)
    }

    override fun nodeFailedToStop(e: Exception) {
        routeEvent(E::nodeFailedToStop, e)
    }

    override fun nodeOnline() {
        routeEvent(E::nodeOnline)
    }

    override fun willStopNodeInBackgroundAfterDelay(seconds: Int) {
        routeEvent(E::willStopNodeInBackgroundAfterDelay, seconds)
    }

    override fun canceledPendingNodeStop() {
        routeEvent(E::canceledPendingNodeStop)
    }

    override fun notificationReceived(notification: Model.Notification) {
        routeEvent(E::notificationReceived, notification)
    }

    override fun threadUpdateReceived(threadId: String, feedItemData: FeedItemData) {
        routeEvent(E::threadUpdateReceived, threadId, feedItemData)
    }

    override fun threadAdded(threadId: String) {
        routeEvent(E::threadAdded, threadId)
    }

    override fun threadRemoved(threadId: String) {
        routeEvent(E::threadRemoved, threadId)
    }

    override fun accountPeerAdded(peerId: String) {
        routeEvent(E::accountPeerAdded, peerId)
    }

    override fun accountPeerRemoved(peerId: String) {
        routeEvent(E::accountPeerAdded, peerId)
    }

    override fun queryDone(queryId: String) {
        routeEvent(E::queryDone, queryId)
    }

    override fun queryError(queryId: String, e: Exception) {
        routeEvent(E::queryError, queryId, e)
    }

    override fun pubsubQueryResult(queryId: String, message: String, messageId: String) {
        routeEvent(E::pubsubQueryResult, queryId, message, messageId)
    }

    override fun clientThreadQueryResult(queryId: String, thread: Model.Thread) {
        routeEvent(E::clientThreadQueryResult, queryId, thread)
    }

    override fun contactQueryResult(queryId: String, contact: Model.Contact) {
        routeEvent(E::contactQueryResult, queryId, contact)
    }

    override fun syncUpdate(status: Model.CafeSyncGroupStatus) {
        routeEvent(E::syncUpdate, status)
    }

    override fun syncComplete(status: Model.CafeSyncGroupStatus) {
        routeEvent(E::syncComplete, status)
    }

    override fun syncFailed(status: Model.CafeSyncGroupStatus) {
        routeEvent(E::syncFailed, status)
    }

    private inline fun routeEvent(eventMethod: TextileEventListener.() -> Unit) {
        delegates.forEach { it.eventMethod() }
    }

    private inline fun <P1> routeEvent(
        eventMethod: TextileEventListener.(P1) -> Unit,
        p1: P1,
    ) {
        delegates.forEach { it.eventMethod(p1) }
    }

    private inline fun <P1, P2> routeEvent(
        eventMethod: TextileEventListener.(P1, P2) -> Unit,
        p1: P1,
        p2: P2
    ) {
        delegates.forEach { it.eventMethod(p1, p2) }
    }

    private inline fun <P1, P2, P3> routeEvent(
        eventMethod: TextileEventListener.(P1, P2, P3) -> Unit,
        p1: P1,
        p2: P2,
        p3: P3
    ) {
        delegates.forEach { it.eventMethod(p1, p2, p3) }
    }
}
