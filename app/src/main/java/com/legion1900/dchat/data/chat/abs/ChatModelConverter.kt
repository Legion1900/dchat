package com.legion1900.dchat.data.chat.abs

import com.legion1900.dchat.domain.dto.Chat
import io.reactivex.Single
import io.textile.pb.Model

interface ChatModelConverter {
    fun convert(chatThread: Model.Thread): Single<Chat>
}
