package com.legion1900.dchat.view.chat.messagelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.legion1900.dchat.databinding.FragmentMessageListBinding

import com.legion1900.dchat.view.main.ChatApplication
import com.legion1900.dchat.view.util.ToolbarUtil

class MessageListFragment : Fragment() {

    private lateinit var viewModel: MessageListViewModel

    private val args by navArgs<MessageListFragmentArgs>()

    private var _binding: FragmentMessageListBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        viewModel.loadMessages(args.chatId)
    }

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
        binding.sendBtn.setOnClickListener(::onSendClick)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun inject() {
        val container = ChatApplication.newFragmentContainer(MessageListViewModel::class.java)
        val factory = container.resolve(ViewModelProvider.Factory::class)!!
        viewModel = ViewModelProvider(this, factory)[MessageListViewModel::class.java]
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onSendClick(v: View) {
        val text = binding.msgInput.text.toString()
        viewModel.sendMessage(args.chatId, text)
    }
}
