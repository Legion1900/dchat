package com.legion1900.dchat.view.chat.addcontact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.legion1900.dchat.databinding.FragmentAddContactBinding
import com.legion1900.dchat.view.main.ChatApplication
import com.legion1900.dchat.view.main.di.FragmentContainer
import com.legion1900.dchat.view.util.ToolbarUtil

class AddContactFragment : Fragment() {

    private lateinit var container: FragmentContainer
    private lateinit var viewModel: AddContactViewModel

    private var _binding: FragmentAddContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddContactBinding.inflate(inflater, container, false)
        ToolbarUtil(this).setupToolbar(binding.toolbar)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun inject() {
        container = ChatApplication.newFragmentContainer(AddContactViewModel::class.java)
        val factory = container.resolve(ViewModelProvider.Factory::class)!!
        viewModel = ViewModelProvider(this, factory)[AddContactViewModel::class.java]
    }
}
