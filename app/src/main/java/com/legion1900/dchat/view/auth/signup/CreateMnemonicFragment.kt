package com.legion1900.dchat.view.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.legion1900.dchat.databinding.FragmentCreateMnemonicBinding
import com.legion1900.dchat.view.util.ToolbarUtil

class CreateMnemonicFragment : Fragment() {

    private var _binding: FragmentCreateMnemonicBinding? = null
    private val binding get() = _binding!!

    private val toolbarUtil = ToolbarUtil(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateMnemonicBinding.inflate(inflater, container, false)
        toolbarUtil.setupToolbar(binding.toolbar)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
