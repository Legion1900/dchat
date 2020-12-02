package com.legion1900.dchat.view.chat.messagelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.legion1900.dchat.databinding.FragmentMessageListBinding
import com.legion1900.dchat.view.main.ChatApplication
import com.legion1900.dchat.view.util.ToolbarUtil

class MessageListFragment : Fragment() {

    private lateinit var viewModel: MessageListViewModel

    private val args by navArgs<MessageListFragmentArgs>()

    private var _binding: FragmentMessageListBinding? = null
    private val binding get() = _binding!!

    private val adapter = MessageAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        viewModel.loadMessages(args.chatId).observe(this) {
            adapter.submitList(it)
        }
        viewModel.isMsgSent.observe(this) { event ->
            event.getIfNotHandled()?.run {
                setIsSending(false)
                binding.msgInput.setText("")
            }
        }
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
        binding.apply {
            toolbar.title = args.chatName
            sendBtn.setOnClickListener(::onSendClick)
            messageList.adapter = adapter
            val manager = LinearLayoutManager(requireContext())
            manager.reverseLayout = true
//            manager.stackFromEnd = true
            messageList.layoutManager = manager
        }
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
        binding.apply {
            setIsSending(true)
        }
        val text = binding.msgInput.text.toString()
        viewModel.sendMessage(args.chatId, text)
    }

    private fun setIsSending(isSending: Boolean) {
        binding.apply {
            sendBtn.isEnabled = !isSending
            sendBtn.visibility = if (isSending) View.INVISIBLE else View.VISIBLE
            progressBar.isVisible = isSending
        }
    }
}
