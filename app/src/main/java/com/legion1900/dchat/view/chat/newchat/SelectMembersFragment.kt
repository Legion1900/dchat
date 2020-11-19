package com.legion1900.dchat.view.chat.newchat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.legion1900.dchat.databinding.FragmentSelectMembersBinding
import com.legion1900.dchat.view.main.ChatApplication
import com.legion1900.dchat.view.util.ToolbarUtil

class SelectMembersFragment : Fragment() {
    private lateinit var viewModel: SelectMembersViewModel

    private var _binding: FragmentSelectMembersBinding? = null
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
        _binding = FragmentSelectMembersBinding.inflate(inflater, container, false)
        ToolbarUtil(this).setupToolbar(binding.toolbar)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun inject() {
        val container = ChatApplication.newFragmentContainer(SelectMembersViewModel::class.java)
        val factory = container.resolve(ViewModelProvider.Factory::class)!!
        viewModel = ViewModelProvider(this, factory)[SelectMembersViewModel::class.java]
    }
}
