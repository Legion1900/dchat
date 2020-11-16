package com.legion1900.dchat.view.auth.signup.createprofile

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.legion1900.dchat.R
import com.legion1900.dchat.databinding.FragmentCreateProfileBinding
import com.legion1900.dchat.view.main.ChatApplication
import com.legion1900.dchat.view.main.di.FragmentContainer
import com.legion1900.dchat.view.util.ToolbarUtil

class CreateProfileFragment : Fragment() {

    private lateinit var container: FragmentContainer
    private lateinit var factory: ViewModelProvider.Factory

    private var _binding: FragmentCreateProfileBinding? = null
    private val binding get() = _binding!!

    private val toolbarUtil = ToolbarUtil(this)

    private val args by navArgs<CreateProfileFragmentArgs>()

    private val viewModel by lazy {
        ViewModelProvider(this, factory)[CreateProfileViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateProfileBinding.inflate(inflater, container, false)
        toolbarUtil.setupToolbar(binding.toolbar)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.finishBtn.setOnClickListener(::onFinishClick)
        viewModel.isCreated.observe(viewLifecycleOwner) {
            navigateToChat()
        }
    }

    override fun onStart() {
        super.onStart()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onStop() {
        super.onStop()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun inject() {
        container = ChatApplication.newFragmentContainer(CreateProfileViewModel::class.java)
        factory = container.resolve(ViewModelProvider.Factory::class)!!
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onFinishClick(v: View) {
        val isValidName = viewModel.isNameValid(binding.userName.text.toString())
        if (isValidName) {
            showProgressBar()
            viewModel.createAccount(args.mnemonic.asList())
        } else {
            binding.nameInput.error = "Enter proper name"
        }
    }

    private fun showProgressBar() {
        binding.apply {
            root.children.forEach { it.isVisible = false }
            progressBar.isVisible = true
        }
    }

    private fun navigateToChat() {
        findNavController().navigate(R.id.action_createProfile_to_chat)
    }
}
