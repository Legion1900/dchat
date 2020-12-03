package com.legion1900.dchat.view.chat.chatlist

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.legion1900.dchat.databinding.DialogCafeDataBinding
import kotlinx.android.parcel.Parcelize

class CafeDataDialog : DialogFragment() {

    private var _binding: DialogCafeDataBinding? = null
    private val binding get() = _binding!!

    private lateinit var call: CafeCall

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        call = requireArguments().getParcelable(KEY_CALLBACK)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCafeDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.connectBtn.setOnClickListener {
            val url = binding.url.text.toString()
            val token = binding.token.text.toString()
            call.doCafeCall(url, token)
            dismiss()
        }
    }

    @Parcelize
    open class CafeCall : Parcelable {
        open fun doCafeCall(url: String, token: String) {
            /* Nothing to do here */
        }
    }

    companion object {
        private const val KEY_CALLBACK = "CafeCallback"

        /**
         * @param callCafe - (url, token) -> Unit
         * */
        fun newInstance(callCafe: (String, String) -> Unit): CafeDataDialog {
            val call = object : CafeCall() {
                override fun doCafeCall(url: String, token: String) {
                    callCafe(url, token)
                }
            }
            return CafeDataDialog().apply {
                arguments = bundleOf(KEY_CALLBACK to call)
            }
        }
    }
}
