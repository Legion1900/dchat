package com.legion1900.dchat.view.chat.addcontact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.legion1900.dchat.databinding.FragmentAddContactBinding
import com.legion1900.dchat.domain.contact.AddContactResult
import com.legion1900.dchat.view.main.ChatApplication
import com.legion1900.dchat.view.main.di.FragmentContainer
import com.legion1900.dchat.view.util.ToolbarUtil

class AddContactFragment : Fragment() {

    private lateinit var container: FragmentContainer
    private lateinit var viewModel: AddContactViewModel

    private var _binding: FragmentAddContactBinding? = null
    private val binding get() = _binding!!

    private val adapter = ContactAdapter(::onContactClick)

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
                setIsWaiting(false)
                isPlaceholderEnabled(accounts.isEmpty())
            }
            lastLoadedPhoto.observe(viewLifecycleOwner) { event ->
                event.getIfNotHandled()?.let { (userId, photo) ->
                    adapter.newAvatar(userId, photo)
                }
            }
            addStatus.observe(viewLifecycleOwner) { event ->
                event.getIfNotHandled()?.let { (name, status) ->
                    setIsWaiting(false)
                    val msg = if (status == AddContactResult.ADDED) {
                        "$name added to your contacts"
                    } else "$name is already in your contacts"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onSearchClick(v: View) {
        setIsWaiting(true)
        val query = binding.searchInput.text.toString()
        viewModel.searchFor(query)
    }

    private fun onContactClick(v: View) {
        val index = binding.searchResults.getChildAdapterPosition(v)
        val contact = adapter.getItem(index)
        viewModel.addContact(contact.id)
        setIsWaiting(true)
    }

    private fun setIsWaiting(isWaiting: Boolean) {
        binding.apply {
            searchBtn.isClickable = !isWaiting
            if (isWaiting) isPlaceholderEnabled(false)
            searchResults.isVisible = !isWaiting
            progressBar.isVisible = isWaiting
        }
    }

    private fun isPlaceholderEnabled(enabled: Boolean) {
        binding.emptyPlaceholder.isVisible = enabled
    }
}
