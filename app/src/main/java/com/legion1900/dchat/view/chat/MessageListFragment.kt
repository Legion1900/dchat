package com.legion1900.dchat.view.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.legion1900.dchat.databinding.FragmentMessageListBinding
import com.legion1900.dchat.view.util.ToolbarUtil

class MessageListFragment : Fragment() {

    private val args by navArgs<MessageListFragmentArgs>()

    private var _binding: FragmentMessageListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ToolbarUtil(this).setupToolbar(binding.toolbar)
        binding.toolbar.title = args.chatName
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
