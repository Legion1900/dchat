package com.legion1900.dchat.view.auth.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.legion1900.dchat.R
import com.legion1900.dchat.databinding.FragmentEnterMnemonicBinding
import com.legion1900.dchat.view.main.ChatApplication
import com.legion1900.dchat.view.util.ToolbarUtil

class EnterMnemonicFragment : Fragment() {

    private lateinit var viewModel: EnterMnemonicViewModel

    private var _binding: FragmentEnterMnemonicBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterMnemonicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeVm()
        ToolbarUtil(this).setupToolbar(binding.toolbar)
        binding.finishBtn.setOnClickListener(::onFinishClick)
    }

    private fun inject() {
        val container = ChatApplication.newFragmentContainer(EnterMnemonicViewModel::class.java)
        val factory = container.resolve(ViewModelProvider.Factory::class)!!
        viewModel = ViewModelProvider(this, factory)[EnterMnemonicViewModel::class.java]
    }

    private fun observeVm() {
        viewModel.isCreated.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_enterMnemonic_to_chat_nav_graph)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onFinishClick(v: View) {
        val string = binding.mnemonic.editText!!.text.toString()
        val mnemonic = string.split(" ")
        viewModel.createProfile(mnemonic)
        binding.apply {
            root.children.forEach { it.isVisible = false }
            progressBar.isVisible = true
        }
    }
}
