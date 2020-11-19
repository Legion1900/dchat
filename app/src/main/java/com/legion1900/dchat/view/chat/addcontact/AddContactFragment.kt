package com.legion1900.dchat.view.chat.addcontact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.legion1900.dchat.databinding.FragmentAddContactBinding
import com.legion1900.dchat.view.main.ChatApplication
import com.legion1900.dchat.view.main.di.FragmentContainer
import com.legion1900.dchat.view.util.ToolbarUtil

class AddContactFragment : Fragment() {

    private lateinit var container: FragmentContainer
    private lateinit var viewModel: AddContactViewModel

    private var _binding: FragmentAddContactBinding? = null
    private val binding get() = _binding!!

    private val adapter = ContactSearchAdapter()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        binding.apply {
            searchBtn.setOnClickListener(::onSearchClick)
        }
        observeVm()
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

    private fun setupRecyclerView() {
        binding.apply {
            searchResults.adapter = adapter
            searchResults.layoutManager = LinearLayoutManager(requireContext())
        }
        viewModel.result.value?.let {
            adapter.setResult(it, viewModel.photos)
        }
    }

    private fun observeVm() {
        viewModel.apply {
            result.observe(viewLifecycleOwner) { accounts ->
                adapter.setResult(accounts)
                setIsSearching(false)
                isPlaceholderEnabled(accounts.isEmpty())
            }
            lastLoadedPhoto.observe(viewLifecycleOwner) { event ->
                event.getIfNotHandled()?.let { (userId, photo) ->
                    adapter.newAvatar(userId, photo)
                }
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onSearchClick(v: View) {
        setIsSearching(true)
        val query = binding.searchInput.text.toString()
        viewModel.searchFor(query)
    }

    private fun setIsSearching(isSearching: Boolean) {
        binding.apply {
            searchBtn.isClickable = !isSearching
            if (isSearching) isPlaceholderEnabled(false)
            searchResults.isVisible = !isSearching
            progressBar.isVisible = isSearching
        }
    }

    private fun isPlaceholderEnabled(enabled: Boolean) {
        binding.emptyPlaceholder.isVisible = enabled
    }
}
