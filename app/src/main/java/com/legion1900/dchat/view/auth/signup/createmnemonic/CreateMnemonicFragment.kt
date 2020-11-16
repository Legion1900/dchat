package com.legion1900.dchat.view.auth.signup.createmnemonic

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.legion1900.dchat.R
import com.legion1900.dchat.databinding.FragmentCreateMnemonicBinding
import com.legion1900.dchat.view.auth.signup.util.MnemonicChipFactory
import com.legion1900.dchat.view.main.ChatApplication
import com.legion1900.dchat.view.main.di.FragmentContainer
import com.legion1900.dchat.view.util.ToolbarUtil

class CreateMnemonicFragment : Fragment() {

    private var _binding: FragmentCreateMnemonicBinding? = null
    private val binding get() = _binding!!

    private lateinit var fragmentContainer: FragmentContainer

    private val toolbarUtil = ToolbarUtil(this)
    private val chipFactory = MnemonicChipFactory(this)

    private lateinit var menu: Menu

    private lateinit var factory: ViewModelProvider.Factory
    private val viewModel by lazy {
        ViewModelProvider(this, factory)[CreateMnemonicViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        viewModel.createMnemonic()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateMnemonicBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        toolbarUtil.setupToolbar(binding.toolbar)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        inflater.inflate(R.menu.mnemonic_length, menu)
        restoreMenu()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isMnemonicReady.observe(viewLifecycleOwner) { isReady ->
            if (isReady) {
                updateMnemonic()
            }
        }
        binding.continueBtn.setOnClickListener(::onContinueClick)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.length_small -> onSmallClick(item)
            R.id.length_medium -> onMediumClick(item)
            else -> false
        }.also { updateMnemonic() }
    }

    private fun onSmallClick(item: MenuItem): Boolean {
        item.isVisible = false
        menu.findItem(R.id.length_medium).isVisible = true
        viewModel.currentLength = CurrentLength.WORDS_24
        return true
    }

    private fun onMediumClick(item: MenuItem): Boolean {
        item.isVisible = false
        menu.findItem(R.id.length_small).isVisible = true
        viewModel.currentLength = CurrentLength.WORDS_12
        return true
    }

    private fun restoreMenu() {
        val isShort = viewModel.currentLength == CurrentLength.WORDS_12
        menu.findItem(R.id.length_small).isVisible = isShort
        menu.findItem(R.id.length_medium).isVisible = !isShort
    }

    private fun inject() {
        fragmentContainer = FragmentContainer(
            ChatApplication.activityContainer!!,
            CreateMnemonicViewModel::class.java
        )
        factory = fragmentContainer.resolve(ViewModelProvider.Factory::class)!!
    }

    private fun updateMnemonic() {
        val words = getCurrentMnemonic()
        if (binding.words.childCount > 0) {
            binding.words.removeAllViews()
        }
        populateWordsList(words)
    }

    private fun populateWordsList(wordsList: List<String>) {
        chipFactory.createChips(wordsList).forEach { chip ->
            binding.words.addView(chip)
        }
    }

    private fun getCurrentMnemonic(): List<String> {
        return if (viewModel.currentLength == CurrentLength.WORDS_12) {
            viewModel.shortMnemonic.value!!
        } else viewModel.mediumMnemonic.value!!
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onContinueClick(v: View) {
        val words = getCurrentMnemonic().toTypedArray()
        val direction = CreateMnemonicFragmentDirections.actionCreateMnemonicToCheckMnemonic(words)
        findNavController().navigate(direction)
    }
}
