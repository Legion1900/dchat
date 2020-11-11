package com.legion1900.dchat.view.main.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.legion1900.dchat.R
import com.legion1900.dchat.view.main.ChatApplication

class FlowSelectorFragment : Fragment() {

    private lateinit var directionProvider: DirectionProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        directionProvider = ChatApplication.activityContainer!!.resolve(DirectionProvider::class)!!
        requireActivity()
            .findNavController(R.id.nav_host_fragment)
            .navigate(directionProvider.provideDirection())
    }
}
