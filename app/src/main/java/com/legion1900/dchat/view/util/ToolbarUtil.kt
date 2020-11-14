package com.legion1900.dchat.view.util

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController

class ToolbarUtil(private val fragment: Fragment) {
    fun setupToolbar(toolbar: Toolbar, config: AppBarConfiguration? = null) {
        (fragment.requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        val controller = fragment.findNavController()
        val barConfig = config ?: AppBarConfiguration(controller.graph)
        /*
        * Use AppCompatActivity.setupActionBarWithNavController if you need toolbar as action bar
        * (e.g. for options menu);
        * Otherwise use Toolbar.setupWithNavController
        * */
        (fragment.requireActivity() as AppCompatActivity)
            .setupActionBarWithNavController(controller, barConfig)
    }
}
