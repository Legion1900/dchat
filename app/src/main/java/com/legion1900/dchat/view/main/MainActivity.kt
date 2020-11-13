package com.legion1900.dchat.view.main

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.legion1900.dchat.data.account.TextileMnemonicGenerator
import com.legion1900.dchat.data.account.TextileProfileManager
import com.legion1900.dchat.data.chat.TextileAclManager
import com.legion1900.dchat.data.chat.TextileChatManager
import com.legion1900.dchat.data.chat.TextileChatRepo
import com.legion1900.dchat.data.chat.gson.ContentJson
import com.legion1900.dchat.data.chat.gson.ContentTypeJson
import com.legion1900.dchat.data.chat.gson.MessageJson
import com.legion1900.dchat.data.chat.impl.JsonSchemaReaderImpl
import com.legion1900.dchat.data.contact.TextileContactManager
import com.legion1900.dchat.data.media.TextilePhotoRepo
import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.data.textile.abs.ThreadFileRepo
import com.legion1900.dchat.data.textile.impl.TextileEventBusImpl
import com.legion1900.dchat.data.textile.impl.TextileProxyImpl
import com.legion1900.dchat.data.textile.impl.ThreadFileRepoImpl
import com.legion1900.dchat.databinding.ActivityMainBinding
import com.legion1900.dchat.domain.account.MnemonicLength
import com.legion1900.dchat.domain.account.ProfileManager
import com.legion1900.dchat.domain.account.RegistrationManager
import com.legion1900.dchat.domain.chat.AclManager
import com.legion1900.dchat.domain.chat.ChatManager
import com.legion1900.dchat.domain.chat.ChatRepo
import com.legion1900.dchat.domain.contact.ContactManager
import com.legion1900.dchat.domain.media.PhotoRepo
import com.legion1900.dchat.domain.media.PhotoWidth
import com.legion1900.dchat.view.main.di.activityContainer
import com.legion1900.dchat.view.main.navigation.DirectionProvider
import com.legion1900.dchat.view.main.navigation.FlowSelectorFragmentDirections
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.textile.pb.Model
import io.textile.textile.BaseTextileEventListener
import io.textile.textile.Textile
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var factory: ViewModelProvider.Factory
    private val viewModel by lazy {
        ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private lateinit var repoPath: String

    private lateinit var proxy: TextileProxy
    private lateinit var contactManager: ContactManager
    private val mnemonicGenerator = TextileMnemonicGenerator()
    private lateinit var registrationManager: RegistrationManager
    private lateinit var profileManager: ProfileManager
    private lateinit var photoRepo: PhotoRepo
    private lateinit var chatRepo: ChatRepo
    private lateinit var fileRepo: ThreadFileRepo
    private lateinit var chatManager: ChatManager
    private lateinit var aclManager: AclManager

    private lateinit var binding: ActivityMainBinding

    private val logger = object : BaseTextileEventListener() {
        override fun nodeStarted() {
            log("node started")
        }

        override fun notificationReceived(notification: Model.Notification) {
            if (notification.type == Model.Notification.Type.INVITE_RECEIVED) {
                // In this case notification.block is invite.id
                Log.d("enigma", "Invite received: $notification")
                Textile.instance().invites.accept(notification.block)
            }
        }

        override fun nodeOnline() {
            log("node online")
        }

        override fun nodeStopped() {
            log("node stopped")
        }

        override fun nodeFailedToStart(e: Exception) {
            log("node failed to start", e)
        }

        override fun nodeFailedToStop(e: Exception) {
            log("node failed to stop", e)
        }

        override fun willStopNodeInBackgroundAfterDelay(seconds: Int) {
            log("will stop node in background in $seconds seconds")
        }

        private fun log(msg: String, e: Throwable? = null) {
            e?.let { Log.e("enigma", msg, it) }
                ?: Log.d("enigma", msg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initContainer()
        inject()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        repoPath = "${filesDir}/textile"
//        registrationManager = TextileRegistrationManager(
//            repoPath,
//            isDebug = true,
//            isLogToDisk = false
//        )

//        launch()
    }

    private fun initContainer() {
        ChatApplication.activityContainer = activityContainer {
            appContainer { ChatApplication.appContainer }
            directionProvider {
                DirectionProvider {
                    if (viewModel.isLoggedIn()) {
                        FlowSelectorFragmentDirections.actionToChatNavGraph()
                    } else FlowSelectorFragmentDirections.actionToAuthNavGraph()
                }
            }
        }
    }

    private fun inject() {
        factory = ChatApplication.activityContainer!!.resolve(ViewModelProvider.Factory::class)!!
    }

    private fun launch() {
        val shouldInit = !Textile.isInitialized(repoPath)
        if (shouldInit) {
            mnemonicGenerator.generateMnemonic(MnemonicLength.MEDIUM)
                .flatMapCompletable { mnemonic ->
                    registrationManager.createAccount(mnemonic)
                }.doOnComplete { initTextile() }
                .andThen {
                    val deviceInfo = "${Build.BRAND} ${Build.DEVICE} ${Build.MODEL}"
                    val setName = profileManager.setName(deviceInfo)
                    val setAvatar = profileManager.setAvatar("$filesDir/avatar.png")
                    setName.mergeWith(setAvatar).subscribe(it)
                }.subscribe { loadProfile() }
        } else {
            initTextile()
            loadProfile()
        }
    }

    private fun initTextile() {
        val bus = TextileEventBusImpl(logger)
        proxy = TextileProxyImpl(bus, application, repoPath, true)
        contactManager = TextileContactManager(proxy)
        profileManager = TextileProfileManager(proxy)
        photoRepo = TextilePhotoRepo(proxy)
        val schemaReader = JsonSchemaReaderImpl(this)
        fileRepo = ThreadFileRepoImpl(proxy, Gson())
        chatRepo = TextileChatRepo(proxy, schemaReader, fileRepo, profileManager)
        chatManager = TextileChatManager(proxy, photoRepo, fileRepo)
        aclManager = TextileAclManager(proxy, fileRepo)
    }

    private fun loadProfile() {
        profileManager.getCurrentAccount()
            .flatMap { account ->
                photoRepo.getPhoto(account.avatarId, PhotoWidth.MEDIUM)
                    .map { Triple(account.id, account.name, it) }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { (id, name, bytes) ->
                binding.run {
//                    address.text = id
//                    this.name.text = name
//                    Glide.with(this@MainActivity)
//                        .asBitmap()
//                        .load(bytes)
//                        .into(binding.avatar)
                }
            }
    }

    fun onAddContactClick(v: View) {
//        val id = binding.nameOrId.text.toString()
//        contactManager.addContact(id).subscribe { Log.d("enigma", "contact added!!") }
    }

    fun onSearchClick(v: View) {
//        val id = binding.nameOrId.text.toString()
//        contactManager.searchContactByName(id, 10, 10)
//            .subscribe { contact -> Log.d("enigma", "found contact: $contact") }
    }

    fun onContactsClick(v: View) {
        contactManager.listContacts()
            .subscribe { contacts -> Log.d("enigma", "my contacts: $contacts") }
    }

    fun onThreadsClick(v: View) {
        chatRepo.getChats(0, 1)
            .doOnSuccess { Log.d("enigma", "chat: $it") }
            .map { it.first() }
            .doOnSuccess {
                Log.d(
                    "enigma",
                    "users in chat: ${aclManager.getChatMembers(it.id).blockingGet()}"
                )
            }
            .flatMap { photoRepo.getPhoto(it.avatarId!!, PhotoWidth.LARGE) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { photoBytes ->
//                Glide.with(this)
//                    .asBitmap()
//                    .load(photoBytes)
//                    .into(binding.avatar)
            }
    }

    fun onAddThreadClick(v: View) {
        val chatAvatar = File(filesDir, "thread_avatar.jpg")
        chatRepo.addNewChat("Test chat")
            .doOnSuccess { Log.d("enigma", "New chat created: $it") }
            .flatMapCompletable { chatManager.setAvatar(it, chatAvatar) }
            .subscribe { Log.d("enigma", "Chat avatar added!") }
    }

    fun testWriteMsg(v: View) {
        val threadId = "12D3KooWMAbUALgAkeh64dWDRHeXwSbRN3xjieQZxP1tv3hoBvea"
        val msgs = listOf(
            MessageJson(ContentTypeJson.TEXT, ContentJson("First Message", null), "Me"),
            MessageJson(ContentTypeJson.PHOTO, ContentJson("Second message", "fileID"), "Me")
        )
        val tasks = mutableListOf<Completable>()
        msgs.forEach {
            tasks += fileRepo.insertData(
                it,
                threadId
            )
        }
        Completable.concat(tasks)
            .doOnComplete { Log.d("enigma", "messages were written!") }
            .andThen(fileRepo.getFiles(MessageJson::class.java, threadId, null, 100))
            .subscribe { data -> Log.d("enigma", "data: $data") }
    }

    fun onInviteUserClick(v: View) {
        val chatId = "12D3KooWMAbUALgAkeh64dWDRHeXwSbRN3xjieQZxP1tv3hoBvea"
//        val id = binding.nameOrId.text.toString()
//        aclManager.inviteToChat(id, chatId).subscribe {
//            Log.d("enigma", "Invite sent")
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ChatApplication.activityContainer = null
    }
}
