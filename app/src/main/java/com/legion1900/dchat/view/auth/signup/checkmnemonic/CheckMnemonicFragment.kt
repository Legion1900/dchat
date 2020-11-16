package com.legion1900.dchat.view.auth.signup.checkmnemonic

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.legion1900.dchat.R
import com.legion1900.dchat.databinding.FragmentCheckMnemonicBinding
import com.legion1900.dchat.view.auth.signup.util.MnemonicChipFactory
import com.legion1900.dchat.view.util.ToolbarUtil
import com.legion1900.dchat.view.util.dialog.BottomDialogFragment
import com.legion1900.dchat.view.util.dialog.action

class CheckMnemonicFragment : Fragment() {

    private var _binding: FragmentCheckMnemonicBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<CheckMnemonicFragmentArgs>()
    private val toolbarUtil = ToolbarUtil(this)
    private val chipFactory = MnemonicChipFactory(this)

    private lateinit var chips: List<Chip>

    private val skipDialog by lazy { newDialogFragment() }

    private val viewModel by lazy {
        ViewModelProviders.of(this)[CheckMnemonicViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.shuffleWords(args.mnemonic)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckMnemonicBinding.inflate(inflater, container, false)
        toolbarUtil.setupToolbar(binding.toolbar)
        setupHint()
        initWords()
        binding.continueBtn.setOnClickListener(::onContinueClick)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.skip_mnemonic_check, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.skip_check -> {
                skipDialog.showNow(childFragmentManager, DIALOG_TAG)
                true
            }
            else -> false
        }
    }

    override fun onStart() {
        super.onStart()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onStop() {
        super.onStop()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun newDialogFragment(): BottomDialogFragment {
        val title = getString(R.string.check_mnemonic_skip_dialog_title)
        val positiveTitle = getString(R.string.check_mnemonic_skip_positive_ans)
        val negativeTitle = getString(R.string.check_mnemonic_skip_negative_ans)
        val positiveAction = action {
            navigateNext()
            skipDialog.dismiss()
        }
        val negativeAction = action { skipDialog.dismiss() }
        return BottomDialogFragment
            .create(title, positiveTitle, negativeTitle, positiveAction, negativeAction)
    }

    private fun setupHint() {
        val hint = getString(R.string.check_mnemonic_hint, CheckMnemonicViewModel.MIN_WORDS)
        binding.hint.text = hint
    }

    private fun initWords() {
        viewModel.shuffleWords(args.mnemonic)
        val words = viewModel.getNotSelectedWords()
        initChips(words)
        binding.apply {
            shuffledWords.removeAllViews()
            chips.forEach { shuffledWords.addView(it) }
        }
    }

    private fun initChips(shuffledWords: List<String>) {
        chips = chipFactory.createChips(shuffledWords.toList(), false)
            .apply {
                forEach {
                    it.setOnClickListener(::onChipClick)
                    it.layoutParams
                }
            }
    }

    private fun onChipClick(v: View) {
        val chip = v as Chip
        val word = chip.text.toString()
        val wasSelected = viewModel.isWordSelected(word)
        if (wasSelected) {
            viewModel.deselectWord(word)
            deselectChip(chip)
        } else {
            viewModel.selectWord(word)
            selectChip(chip)
        }
    }

    private fun deselectChip(chip: Chip) {
        binding.apply {
            selectedWords.removeView(chip)
            shuffledWords.addView(chip)
        }
    }

    private fun selectChip(chip: Chip) {
        binding.apply {
            shuffledWords.removeView(chip)
            selectedWords.addView(chip)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onContinueClick(v: View) {
        val isCorrect = viewModel.isSelectedCorrect(args.mnemonic)
        if (isCorrect) {
            navigateNext()
        } else {
            Toast.makeText(requireContext(), "Wrong!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateNext() {
        val directions =
            CheckMnemonicFragmentDirections.actionCheckMnemonicToCreateProfile(args.mnemonic)
        findNavController().navigate(directions)
    }

    companion object {
        const val DIALOG_TAG = "SkipMnemonicCheckDialog"
    }
}
