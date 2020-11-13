package com.legion1900.dchat.view.auth.signup

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.legion1900.dchat.R
import com.legion1900.dchat.databinding.FragmentCreateMnemonicBinding
import com.legion1900.dchat.domain.account.MnemonicLength
import com.legion1900.dchat.view.main.ChatApplication
import com.legion1900.dchat.view.main.di.FragmentContainer
import com.legion1900.dchat.view.util.ToolbarUtil
import com.legion1900.dchat.view.util.views.ChipTextDrawable
import com.legion1900.dchat.view.util.views.TextParameters

class CreateMnemonicFragment : Fragment() {

    private var _binding: FragmentCreateMnemonicBinding? = null
    private val binding get() = _binding!!

    private val toolbarUtil = ToolbarUtil(this)

    private lateinit var factory: ViewModelProvider.Factory
    private val viewModel by lazy {
        ViewModelProvider(this, factory)[CreateMnemonicViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        viewModel.createMnemonic(MnemonicLength.MEDIUM)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateMnemonicBinding.inflate(inflater, container, false)
        toolbarUtil.setupToolbar(binding.toolbar)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.mnemonic.observe(viewLifecycleOwner, ::populateWordsList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        ChatApplication.fragmentContainer = null
    }

    private fun inject() {
        ChatApplication.fragmentContainer = FragmentContainer(
            ChatApplication.activityContainer!!,
            null,
            CreateMnemonicViewModel::class.java
        )
        factory = ChatApplication.fragmentContainer!!.resolve(ViewModelProvider.Factory::class)!!
    }

    private fun populateWordsList(wordsList: List<String>) {
        createChips(wordsList).forEach { chip ->
            binding.words.addView(chip)
        }
    }

    private fun createChips(wordsList: List<String>): List<Chip> {
        val chips = mutableListOf<Chip>()
        val bgIcon = getChipBgDrawable()
        val numberSize = resources.getDimension(R.dimen.default_text) * 0.75f
        val color = ResourcesCompat.getColor(
            resources,
            R.color.design_default_color_on_primary,
            requireContext().theme
        )
        val params = TextParameters.defaultTypeface(color, false, numberSize)
        for ((i, word) in wordsList.withIndex()) {
            chips += Chip(requireContext()).apply {
                val numberDrawable = ChipTextDrawable((i + 1).toString(), params, bgIcon)
                text = word
                setTextAppearanceResource(R.style.DefaultText)
                chipIcon = numberDrawable
                isClickable = false
            }
        }
        return chips
    }

    private fun getChipBgDrawable(): Drawable {
        return ResourcesCompat.getDrawable(
            resources,
            R.drawable.chip_icon_bg,
            requireContext().theme
        )!!
    }
}
