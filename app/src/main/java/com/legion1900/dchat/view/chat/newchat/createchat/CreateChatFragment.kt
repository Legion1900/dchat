package com.legion1900.dchat.view.chat.newchat.createchat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.legion1900.dchat.databinding.FragmentCreateChatBinding
import com.legion1900.dchat.domain.dto.Account
import com.legion1900.dchat.domain.dto.toAccount
import com.legion1900.dchat.view.chat.addcontact.ContactAdapter
import com.legion1900.dchat.view.main.ChatApplication
import com.legion1900.dchat.view.util.PhotoChooser
import com.legion1900.dchat.view.util.ToolbarUtil

class CreateChatFragment : Fragment() {

    private var _binding: FragmentCreateChatBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<CreateChatFragmentArgs>()
    private lateinit var members: List<Account>

    private lateinit var viewModel: CreateChatViewModel

    private val adapter = ContactAdapter()

    private val photoChooser = PhotoChooser(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        members = args.members.map { it.toAccount() }
        inject()
        observeVm()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ToolbarUtil(this).setupToolbar(binding.toolbar)
        setupRecyclerView()
        binding.selectAvatarBtn.setOnClickListener(::onSelectAvatarClick)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let {
            photoChooser.onActivityResult(requestCode, AVATAR_REQUEST, resultCode, it)
                ?.let { (stream, extension) ->
                    viewModel.setAvatar(stream, extension)
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun inject() {
        val factory = ChatApplication.newFragmentContainer(CreateChatViewModel::class.java)
            .resolve(ViewModelProvider.Factory::class)!!
        viewModel = ViewModelProvider(this, factory)[CreateChatViewModel::class.java]
    }

    private fun setupRecyclerView() {
        binding.contactsList.apply {
            adapter = this@CreateChatFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        adapter.setResult(members)
    }

    private fun observeVm() {
        viewModel.apply {
            avatarBytes.observe(this@CreateChatFragment) { avatar ->
                Glide.with(this@CreateChatFragment)
                    .asBitmap()
                    .load(avatar)
                    .into(binding.selectAvatarBtn)
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onSelectAvatarClick(v: View) {
        photoChooser.launchPhotoChoosing(AVATAR_REQUEST)
    }

    private companion object {
        const val AVATAR_REQUEST = 1
    }
}
