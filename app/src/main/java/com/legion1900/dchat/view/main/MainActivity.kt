package com.legion1900.dchat.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.legion1900.dchat.R
import com.legion1900.dchat.databinding.ActivityMainBinding
import com.legion1900.dchat.view.main.di.activityContainer
import com.legion1900.dchat.view.main.navigation.DirectionProvider
import com.legion1900.dchat.view.main.navigation.FlowSelectorFragmentDirections

class MainActivity : AppCompatActivity() {

    var appBarConfig: AppBarConfiguration? = null

    private lateinit var factory: ViewModelProvider.Factory
    private val viewModel by lazy {
        ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

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
            chatContainer { ChatApplication.chatContainer }
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
