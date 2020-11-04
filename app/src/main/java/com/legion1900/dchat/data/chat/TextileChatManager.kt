package com.legion1900.dchat.data.chat

import com.legion1900.dchat.R
import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.domain.chat.ChatManager
import com.legion1900.dchat.domain.dto.Chat
import io.reactivex.Single
import io.textile.pb.Model
import io.textile.pb.View
import io.textile.textile.Textile
import java.util.*

class TextileChatManager(
    private val proxy: TextileProxy,
    private val schemaReader: JsonSchemaReader
) : ChatManager {
    override fun getChats(offset: String?, limit: Int): Single<List<Chat>> {
        TODO()
    }

    override fun createChat(name: String): Single<String> {
        val getChatSchema = schemaReader.readSchema(R.raw.chat_schema)
            .map { newJsonSchema(it) }
        return Single.zip(
            newAclThread(),
            newMediaThread(),
            newAvatarThread(),
            getChatSchema,
            proxy.instance
        ) { aclThread, mediaThread, avatarThread, chatSchema, textile ->
            /*
            * Chat thread key retrieval algorithm must not be modified!
            * */
            val key = "${aclThread.id};${mediaThread.id};${avatarThread.id}"
            val sharing = Model.Thread.Sharing.INVITE_ONLY
            val type = Model.Thread.Type.OPEN
            textile.newThread(chatSchema, sharing, type, key)
        }.map { it.id }
    }

    private fun newAclThread(): Single<Model.Thread> {
        return schemaReader.readSchema(R.raw.acl_schema)
            .map { newJsonSchema(it) }
            .zipWith(proxy.instance) { schema, textile ->
                textile.newThread(
                    schema,
                    Model.Thread.Sharing.INVITE_ONLY,
                    Model.Thread.Type.READ_ONLY,
                    "$ACL_KEY_PREFIX${newUUID()}"
                )
            }
    }

    private fun newMediaThread(): Single<Model.Thread> {
        return proxy.instance.map { textile ->
            val schema = newPresetSchema(View.AddThreadConfig.Schema.Preset.MEDIA)
            textile.newThread(
                schema,
                Model.Thread.Sharing.INVITE_ONLY,
                Model.Thread.Type.OPEN,
                "$MEDIA_KEY_PREFIX${newUUID()}"
            )
        }
    }

    private fun newAvatarThread(): Single<Model.Thread> {
        return schemaReader.readSchema(R.raw.avatar_schema)
            .map { newJsonSchema(it) }
            .zipWith(proxy.instance) { schema, textile ->
                textile.newThread(
                    schema,
                    Model.Thread.Sharing.INVITE_ONLY,
                    Model.Thread.Type.OPEN,
                    "$AVATAR_KEY_PREFIX${newUUID()}"
                )
            }
    }

    private fun Textile.newThread(
        schema: View.AddThreadConfig.Schema,
        sharing: Model.Thread.Sharing,
        type: Model.Thread.Type,
        key: String
    ): Model.Thread {
        val config = newThreadConfig(schema, sharing, type, key)
        return threads.add(config)
    }

    @Suppress("SameParameterValue")
    private fun newPresetSchema(
        preset: View.AddThreadConfig.Schema.Preset
    ): View.AddThreadConfig.Schema {
        return View.AddThreadConfig.Schema.newBuilder()
            .setPreset(preset)
            .build()
    }

    private fun newJsonSchema(schema: String): View.AddThreadConfig.Schema {
        return View.AddThreadConfig.Schema.newBuilder()
            .setJson(schema)
            .build()
    }

    private fun newThreadConfig(
        schema: View.AddThreadConfig.Schema,
        sharing: Model.Thread.Sharing,
        type: Model.Thread.Type,
        key: String
    ): View.AddThreadConfig {
        return View.AddThreadConfig.newBuilder()
            .setSchema(schema)
            .setSharing(sharing)
            .setType(type)
            .setKey(key)
            .build()
    }

    private fun newUUID() = UUID.randomUUID().toString()

    private companion object {
        const val AVATAR_KEY_PREFIX = "avatar: "
        const val MEDIA_KEY_PREFIX = "media: "
        const val ACL_KEY_PREFIX = "acl: "
    }
}
