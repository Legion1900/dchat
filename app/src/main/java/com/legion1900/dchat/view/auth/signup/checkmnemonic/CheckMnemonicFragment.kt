package com.legion1900.dchat.view.auth.signup.checkmnemonic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.legion1900.dchat.databinding.FragmentCheckMnemonicBinding
import com.legion1900.dchat.view.util.ToolbarUtil

class CheckMnemonicFragment : Fragment() {

    private var _binding: FragmentCheckMnemonicBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<CheckMnemonicFragmentArgs>()
    private val toolbarUtil = ToolbarUtil(this)

    private val viewModel by lazy {
        ViewModelProviders.of(this)[CheckMnemonicViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckMnemonicBinding.inflate(inflater, container, false)
        toolbarUtil.setupToolbar(binding.toolbar)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
