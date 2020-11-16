package com.legion1900.dchat.view.util.dialog

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.legion1900.dchat.databinding.FragmentBottomDialogBinding
import kotlinx.android.parcel.Parcelize

/**
 * Two-action bottom dialog
 * */
class BottomDialogFragment : BottomSheetDialogFragment() {

    private lateinit var title: String
    private lateinit var positiveTitle: String
    private lateinit var negativeTitle: String

    private lateinit var onPositiveClick: Action
    private lateinit var onNegativeClick: Action

    private var _binding: FragmentBottomDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        obtainArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBottomDialogBinding.inflate(inflater, container, false)
        setupData()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun obtainArgs() {
        with(requireArguments()) {
            title = getString(TITLE)!!
            positiveTitle = getString(POSITIVE_TITLE)!!
            negativeTitle = getString(NEGATIVE_TITLE)!!
            onPositiveClick = getParcelable(POSITIVE_ACTION)!!
            onNegativeClick = getParcelable(NEGATIVE_ACTION)!!
        }
    }

    private fun setupData() {
        binding.apply {
            title.text = this@BottomDialogFragment.title

            positiveBtn.text = positiveTitle
            positiveBtn.setOnClickListener(onPositiveClick)

            negativeBtn.text = negativeTitle
            negativeBtn.setOnClickListener(onNegativeClick)
        }
    }

    @Parcelize
    open class Action : Parcelable, View.OnClickListener {
        override fun onClick(v: View) {}
    }

    companion object {

        private const val TITLE = "BottomDialog.Title"
        private const val POSITIVE_TITLE = "BottomDialog.PositiveButton"
        private const val NEGATIVE_TITLE = "BottomDialog.NegativeButton"
        private const val POSITIVE_ACTION = "BottomDialog.PositiveAction"
        private const val NEGATIVE_ACTION = "BottomDialog.NegativeAction"

        fun create(
            title: String,
            positiveBtn: String,
            negativeBtn: String,
            onPositiveClick: Action,
            onNegativeClick: Action
        ): BottomDialogFragment {
            val dialog = BottomDialogFragment()
            dialog.arguments = bundleOf(
                TITLE to title,
                POSITIVE_TITLE to positiveBtn,
                NEGATIVE_TITLE to negativeBtn,
                POSITIVE_ACTION to onPositiveClick,
                NEGATIVE_ACTION to onNegativeClick
            )
            return dialog
        }
    }
}

inline fun action(crossinline block: () -> Unit): BottomDialogFragment.Action {
    return object : BottomDialogFragment.Action() {
        override fun onClick(v: View) {
            block()
        }
    }
}
