package com.legion1900.dchat.view.chat.chatlist

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.legion1900.dchat.R
import com.legion1900.dchat.databinding.FragmentChatListBinding
import com.legion1900.dchat.view.main.ChatApplication
import com.legion1900.dchat.view.main.di.FragmentContainer
import com.legion1900.dchat.view.util.ToolbarUtil
import com.legion1900.dchat.view.util.ext.copyToClipboard

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
        observeVm()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        val config = AppBarConfiguration(setOf(R.id.chatListFragment), binding.drawerLayout)
        toolbarUtil.setupToolbar(binding.toolbar, config)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadChatList()
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
            drawer.getHeaderView(0).apply {
                findViewById<ImageView>(R.id.avatar).clipToOutline = true
                findViewById<TextView>(R.id.id).setOnClickListener(::onUserIdClick)
            }
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

    @SuppressLint("CutPasteId")
    private fun observeVm() {
        viewModel.apply {
            chatList.observe(this@ChatListFragment) { chats ->
                adapter.submitList(chats)
            }
            userAvatar.observe(this@ChatListFragment) { avatar ->
                _binding?.drawer?.apply {
                    findViewById<ImageView>(R.id.avatar).let {
                        Glide.with(it)
                            .asBitmap()
                            .load(avatar)
                            .into(it)
                    }
                    findViewById<TextView>(R.id.avatar_placeholder).isVisible = false
                }
            }
            userName.observe(this@ChatListFragment) { name ->
                _binding?.drawer?.apply {
                    findViewById<TextView>(R.id.name).text = name
                    findViewById<TextView>(R.id.avatar_placeholder).text = name.first().toString()
                    findViewById<ImageView>(R.id.avatar).isVisible = true
                }
            }
            userId.observe(this@ChatListFragment) { id ->
                _binding?.drawer?.findViewById<TextView>(R.id.id)?.let {
                    it.text = id
                }
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

    private fun onUserIdClick(v: View) {
        val id = (v as TextView).text.toString()
        copyToClipboard(id)
        Toast.makeText(requireContext(), "ID copied to clipboard", Toast.LENGTH_SHORT)
            .show()
    }
}
