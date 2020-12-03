package com.legion1900.dchat.view.auth.selectauth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.legion1900.dchat.R

class SelectAuthFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.sign_up_btn).setOnClickListener(::onSignUpClick)
        view.findViewById<Button>(R.id.sign_in_btn).setOnClickListener(::onSignInCLick)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onSignUpClick(v: View) {
        requireView().findNavController().navigate(R.id.action_selectAuth_to_createMnemonic)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onSignInCLick(v: View) {
        findNavController().navigate(R.id.action_selectAuth_to_enterMnemonic)
    }
}
