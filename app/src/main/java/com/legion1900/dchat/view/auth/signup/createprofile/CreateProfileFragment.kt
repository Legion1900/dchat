package com.legion1900.dchat.view.auth.signup.createprofile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
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
        Log.d("enigma", "ViewModel: $viewModel")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun inject() {
        container = ChatApplication.newFragmentContainer(CreateProfileViewModel::class.java)
        factory = container.resolve(ViewModelProvider.Factory::class)!!
    }
}
