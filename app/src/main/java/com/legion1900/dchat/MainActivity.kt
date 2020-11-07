package com.legion1900.dchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repoPath = "${filesDir}/textile"
        registrationManager = TextileRegistrationManager(
            repoPath,
            isDebug = true,
            isLogToDisk = false
        )

        launch()
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
        chatRepo = TextileChatRepo(proxy, schemaReader, fileRepo)
    }

    private fun loadProfile() {
        profileManager.getCurrentAccount()
            .flatMap { account ->
                photoRepo.getPhoto(account.avatarId, PhotoWidth.SMALL)
                    .map { Triple(account.id, account.name, it) }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { (id, name, bytes) ->
                binding.run {
                    address.text = id
                    this.name.text = name
                    Glide.with(this@MainActivity)
                        .asBitmap()
                        .load(bytes)
                        .into(binding.avatar)
                }
            }
    }

    fun onAddContactClick(v: View) {
        val id = binding.nameOrId.text.toString()
        contactManager.addContact(id).subscribe { Log.d("enigma", "contact added!!") }
    }

    fun onSearchClick(v: View) {
        val id = binding.nameOrId.text.toString()
        contactManager.searchContactByName(id, 10, 10)
            .subscribe { contact -> Log.d("enigma", "found contact: $contact") }
    }

    fun onContactsClick(v: View) {
        contactManager.listContacts()
            .subscribe { contacts -> Log.d("enigma", "my contacts: $contacts") }
    }

    fun onThreadsClick(v: View) {
        chatRepo.getChats(0, 100)
            .subscribe { chats -> Log.d("enigma", "chats: $chats") }
    }

    fun onAddThreadClick(v: View) {
        chatRepo.addNewChat("Test chat")
            .subscribe { chat -> Log.d("enigma", "new chat: $chat") }
    }

    fun testWriteMsg(v: View) {
        val threadId = "12D3KooWC2E5g9XNVnM4G38w62Zhta6yXCJKMW48hdFhFqd663e8"
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
}
