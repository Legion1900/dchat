package com.legion1900.dchat.view.util

import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController

class ToolbarUtil(private val fragment: Fragment) {
    fun setupToolbar(toolbar: Toolbar, config: AppBarConfiguration? = null) {
        val controller = fragment.findNavController()
        val barConfig = config ?: AppBarConfiguration(controller.graph)
        toolbar.setupWithNavController(controller, barConfig)
    }
}
