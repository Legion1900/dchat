package com.legion1900.dchat.view.chat.chatlist

import android.os.Bundle
import android.util.Base64
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import com.legion1900.dchat.R
import com.legion1900.dchat.databinding.FragmentChatListBinding
import com.legion1900.dchat.view.main.ChatApplication
import com.legion1900.dchat.view.main.di.FragmentContainer
import com.legion1900.dchat.view.util.ToolbarUtil

class ChatListFragment : Fragment() {

    private lateinit var container: FragmentContainer
    private lateinit var factory: ViewModelProvider.Factory
    private val viewModel by lazy {
        ViewModelProvider(this, factory)[ChatListViewModel::class.java]
    }

    private val toolbarUtil = ToolbarUtil(this)

    private val adapter = ChatAdapter(::onChatClick)

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        container = ChatApplication.newFragmentContainer(ChatListViewModel::class.java)
        factory = container.resolve(ViewModelProvider.Factory::class)!!
        setHasOptionsMenu(true)
        viewModel.loadProfileInfo()
        viewModel.loadChatList()
        observeVm()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        toolbarUtil.setupToolbar(binding.toolbar, AppBarConfiguration(setOf(R.id.chatListFragment)))
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.chat_list_menu, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            createChatBtn.setOnClickListener(::onAddChatClick)
            chatList.layoutManager = LinearLayoutManager(requireContext())
            chatList.adapter = adapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.add_user) {
            findNavController().navigate(R.id.action_chatList_to_addContact)
            true
        } else false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeVm() {
        viewModel.apply {
            chatList.observe(this@ChatListFragment) { chats ->
                adapter.submitList(chats)
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onAddChatClick(v: View) {
        findNavController().navigate(R.id.action_chatList_to_selectMembers)
    }

    private fun onChatClick(v: View) {
        val position = binding.chatList.getChildAdapterPosition(v)
        val (id, name, avatarBytes) = adapter.getChat(position)
        val avatar = avatarBytes?.let { Base64.encodeToString(it, Base64.DEFAULT) }
        val directions = ChatListFragmentDirections.actionChatListToMessageList(id, name, avatar)
        findNavController().navigate(directions)
    }
}
