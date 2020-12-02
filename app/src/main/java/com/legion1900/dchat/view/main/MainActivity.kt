package com.legion1900.dchat.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.legion1900.dchat.R
import com.legion1900.dchat.data.account.TextileMnemonicGenerator
import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.data.textile.abs.ThreadFileRepo
import com.legion1900.dchat.databinding.ActivityMainBinding
import com.legion1900.dchat.domain.account.ProfileManager
import com.legion1900.dchat.domain.account.RegistrationManager
import com.legion1900.dchat.domain.chat.AclManager
import com.legion1900.dchat.domain.chat.ChatManager
import com.legion1900.dchat.domain.chat.ChatRepo
import com.legion1900.dchat.domain.contact.ContactManager
import com.legion1900.dchat.domain.media.PhotoRepo
import com.legion1900.dchat.view.main.di.activityContainer
import com.legion1900.dchat.view.main.navigation.DirectionProvider
import com.legion1900.dchat.view.main.navigation.FlowSelectorFragmentDirections

class MainActivity : AppCompatActivity() {

    var appBarConfig: AppBarConfiguration? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        initContainer()
        inject()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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

    override fun onDestroy() {
        super.onDestroy()
        ChatApplication.activityContainer = null
    }

    override fun onSupportNavigateUp(): Boolean {
        val controller = findNavController(R.id.nav_host_fragment)
        return appBarConfig?.let { controller.navigateUp(it) } ?: controller.navigateUp()
    }
}
