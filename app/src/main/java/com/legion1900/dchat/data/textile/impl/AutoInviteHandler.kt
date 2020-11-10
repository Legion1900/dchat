package com.legion1900.dchat.data.textile.impl

import io.textile.pb.Model
import io.textile.textile.BaseTextileEventListener
import io.textile.textile.Textile

class AutoInviteHandler : BaseTextileEventListener() {
    override fun notificationReceived(notification: Model.Notification) {
        if (notification.type == Model.Notification.Type.INVITE_RECEIVED) {
            Textile.instance().invites.accept(notification.block)
        }
    }
}
