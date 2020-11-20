package com.legion1900.dchat.view.chat.newchat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.legion1900.dchat.databinding.FragmentSelectMembersBinding
import com.legion1900.dchat.domain.dto.Account
import com.legion1900.dchat.view.chat.addcontact.ContactAdapter
import com.legion1900.dchat.view.main.ChatApplication
import com.legion1900.dchat.view.util.ToolbarUtil

class SelectMembersFragment : Fragment() {
    private lateinit var viewModel: SelectMembersViewModel

    private var _binding: FragmentSelectMembersBinding? = null
    private val binding get() = _binding!!

    private val adapter = ContactAdapter(::onContactClick)

    private val searchInputWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            /* Nothing to do here */
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            setIsLoading(true)
            val name = s!!.toString()
            viewModel.filterByName(name)
        }
    }

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
        setupRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeVm()
        viewModel.loadContacts()
        binding.searchInput.addTextChangedListener(searchInputWatcher)
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

    private fun setupRecyclerView() {
        binding.contactsList.adapter = adapter
        binding.contactsList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeVm() {
        viewModel.apply {
            observeContacts()
            observeAvatars()
            observeFilteredContacts()
        }
    }

    private fun SelectMembersViewModel.observeContacts() {
        getContacts().observe(viewLifecycleOwner, ::updateContactsList)
    }

    private fun SelectMembersViewModel.observeAvatars() {
        getLastLoadedAvatar().observe(viewLifecycleOwner) { event ->
            event.getIfNotHandled()?.let { (uid, avatar) ->
                adapter.newAvatar(uid, avatar)
            }
        }
    }

    private fun SelectMembersViewModel.observeFilteredContacts() {
        getFilteredContacts().observe(viewLifecycleOwner, ::updateContactsList)
    }

    private fun updateContactsList(contacts: List<Account>) {
        val cachedAvatars = viewModel.getAvatarsCache()
            .let { if (it.isNotEmpty()) it else null }
        setIsLoading(false)
        adapter.setResult(contacts, cachedAvatars)
    }

    private fun onContactClick(v: View) {

    }

    private fun setIsLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
    }
}
