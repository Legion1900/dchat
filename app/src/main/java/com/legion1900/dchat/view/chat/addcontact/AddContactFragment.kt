package com.legion1900.dchat.view.chat.addcontact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.legion1900.dchat.databinding.FragmentAddContactBinding
import com.legion1900.dchat.view.util.ToolbarUtil

class AddContactFragment : Fragment() {

    private var _binding: FragmentAddContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddContactBinding.inflate(inflater, container, false)
        ToolbarUtil(this).setupToolbar(binding.toolbar)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
