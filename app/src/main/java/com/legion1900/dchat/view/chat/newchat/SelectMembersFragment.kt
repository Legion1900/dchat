package com.legion1900.dchat.view.chat.newchat

import android.os.Bundle
import android.text.Editable
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.getSpans
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.ChipDrawable
import com.legion1900.dchat.R
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

        private var shouldRemove = false
        private var spanStart = 0
        private var spanEnd = 0

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            if (after == 0) {
                val editable = binding.searchInput.text
                val spans = editable.getSpans<ClickableSpan>(start)
                if (spans.isNotEmpty()) {
                    shouldRemove = true
                    spanStart = editable.getSpanStart(spans.first())
                    spanEnd = editable.getSpanEnd(spans.first()) - 1
                    editable.removeSpan(spans.first())
                }
            }
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            /* Nothing to do here */
        }

        override fun afterTextChanged(s: Editable?) {
            val editable = binding.searchInput.text
            val filter = getSearchFilter()
            if (filter.isNotEmpty()) {
                setIsLoading(true)
                viewModel.filterByName(filter)
            }

            if (shouldRemove) {
                shouldRemove = false
                Log.d("enigma", "editable: '$editable'")
                Log.d("enigma", "length: ${editable.length}, start: $spanStart, end: $spanEnd")
                editable.replace(spanStart, spanEnd, "")
            }
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
    ): View {
        _binding = FragmentSelectMembersBinding.inflate(inflater, container, false)
        ToolbarUtil(this).setupToolbar(binding.toolbar)
        setupRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeVm()
        viewModel.loadContacts()
        binding.searchInput.apply {
            addTextChangedListener(searchInputWatcher)
            movementMethod = LinkMovementMethod.getInstance()
        }
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
        val index = binding.contactsList.getChildAdapterPosition(v)
        val account = adapter.getItem(index)
        ChipDrawable.Delegate { }
        val chip = ChipDrawable.createFromResource(requireContext(), R.xml.chip)
        chip.text = account.name
        chip.setBounds(0, 0, chip.intrinsicWidth, chip.intrinsicHeight)
        val specialName = "${account.name}; "
        val spanSize = specialName.length - 1
        binding.searchInput.text.replace(0, 0, specialName)
        val span = ImageSpan(chip)
        binding.searchInput.text.setSpan(
            span,
            0,
            spanSize,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val clickSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Log.d("enigma", "span clicked!")
            }

        }

        binding.searchInput.text.setSpan(
            clickSpan,
            0,
            spanSize,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun setIsLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
    }

    private fun getSearchFilter(): String {
        val input = binding.searchInput.text.toString()
        return input.split(";").last().removePrefix(" ").also { Log.d("enigma", "filter: '$it'") }
    }
}
