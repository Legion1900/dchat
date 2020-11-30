package com.legion1900.dchat.view.chat.newchat.createchat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.legion1900.dchat.databinding.FragmentCreateChatBinding
import com.legion1900.dchat.domain.dto.Account
import com.legion1900.dchat.domain.dto.toAccount
import com.legion1900.dchat.view.util.ToolbarUtil

class CreateChatFragment : Fragment() {

    private var _binding: FragmentCreateChatBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<CreateChatFragmentArgs>()
    private lateinit var members: List<Account>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        members = args.members.map { it.toAccount() }
        Log.d("enigma", "members: $members")
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
    }
}
