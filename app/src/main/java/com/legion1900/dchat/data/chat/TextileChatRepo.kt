package com.legion1900.dchat.data.chat

import com.legion1900.dchat.R
import com.legion1900.dchat.data.chat.abs.JsonSchemaReader
import com.legion1900.dchat.data.chat.gson.AclJson
import com.legion1900.dchat.data.chat.gson.AvatarJson
import com.legion1900.dchat.data.chat.gson.MessageJson
import com.legion1900.dchat.data.message.converter.MessageModelConverter
import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.data.textile.abs.ThreadFileRepo
import com.legion1900.dchat.domain.account.ProfileManager
import com.legion1900.dchat.domain.chat.ChatRepo
import com.legion1900.dchat.domain.dto.Chat
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.textile.pb.Model
import io.textile.pb.View
import io.textile.textile.Textile
import java.util.*

class TextileChatRepo(
    private val proxy: TextileProxy,
    private val schemaReader: JsonSchemaReader,
    private val fileRepo: ThreadFileRepo,
    private val profileManager: ProfileManager
) : ChatRepo {

    private val chatKeyUtil = ChatKeyUtil()

    private val msgConverter = MessageModelConverter()

    override fun getChatCount(): Single<Int> {
        return getChatThreads().count().map { it.toInt() }
    }

    override fun getChats(offset: Int, limit: Int): Single<List<Chat>> {
        return getChatThreads()
            .skip(offset.toLong())
            .concatMapSingle { getChatModel(it) }
            .buffer(limit)
            .defaultIfEmpty(emptyList())
            .firstOrError()
    }

    override fun addNewChat(name: String): Single<String> {
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
            val key = chatKeyUtil.createChatKey(aclThread.id, mediaThread.id, avatarThread.id)
            val sharing = Model.Thread.Sharing.INVITE_ONLY
            val type = Model.Thread.Type.OPEN
            textile.newThread(chatSchema, sharing, type, key, name)
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
            }.flatMap { addChatOwner(it.id).andThen(Single.just(it)) }
    }

    private fun addChatOwner(aclId: String): Completable {
        return profileManager.getCurrentAccount()
            .map { it.id }
            .flatMapCompletable { ownerId ->
                val acl = AclJson(listOf(ownerId))
                fileRepo.insertData(acl, aclId)
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
        key: String,
        name: String = ""
    ): Model.Thread {
        val config = newThreadConfig(schema, sharing, type, key, name)
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
        key: String,
        name: String
    ): View.AddThreadConfig {
        return View.AddThreadConfig.newBuilder()
            .setSchema(schema)
            .setSharing(sharing)
            .setType(type)
            .setName(name)
            .setKey(key)
            .build()
    }

    private fun newUUID() = UUID.randomUUID().toString()

    private fun getAvatarHash(threadId: String): Single<List<AvatarJson>> {
        return fileRepo.getFiles(AvatarJson::class.java, threadId, null, 1)
            .map { it.data }
    }

    private fun getLastMessage(threadId: String): Single<List<MessageJson>> {
        return fileRepo.getFiles(MessageJson::class.java, threadId, null, 1)
            .map { it.data }
    }

    private fun getChatModel(chatThread: Model.Thread): Single<Chat> {
        val avatarId = chatKeyUtil.getAvatarId(chatThread.key)
        val avatarHash = getAvatarHash(avatarId)
        val msg = getLastMessage(chatThread.id)
        return Single.zip(avatarHash, msg) { hashList, msgList ->
            val avatar = hashList.firstOrNull()?.chatAvatar
            val lastMsg = msgList.firstOrNull()?.let { msgConverter.convert(it) }
            Chat(chatThread.id, chatThread.name, avatar, lastMsg)
        }
    }

    private fun getChatThreads(): Observable<Model.Thread> {
        return proxy.instance.map { it.threads.list().itemsList }
            .flatMapObservable { Observable.fromIterable(it) }
            .filter { !otherThreadsRegex.containsMatchIn(it.key) }
    }

    private companion object {
        const val AVATAR_KEY_PREFIX = "avatar: "
        const val MEDIA_KEY_PREFIX = "media: "
        const val ACL_KEY_PREFIX = "acl: "

        val otherThreadsRegex = Regex("^avatar:\\s|^media:\\s|^acl:\\s")
    }
}
