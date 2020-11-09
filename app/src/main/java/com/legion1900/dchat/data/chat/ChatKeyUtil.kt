package com.legion1900.dchat.data.chat

class ChatKeyUtil {
    fun createChatKey(aclId: String, mediaId: String, avatarId: String): String {
        return "$aclId;$mediaId;$avatarId"
    }

    fun getMediaId(chatKey: String) = getIds(chatKey).second

    fun getAclId(chatKey: String) = getIds(chatKey).first

    fun getAvatarId(chatKey: String) = getIds(chatKey).third

    private fun getIds(chatKey: String): Triple<String, String, String> {
        return chatKey.split(";").let { (aclId, mediaId, avatarId) ->
            Triple(aclId, mediaId, avatarId)
        }
    }
}
