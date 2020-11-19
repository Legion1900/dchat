package com.legion1900.dchat.view.chat.addcontact

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.legion1900.dchat.databinding.ItemContactSearchBinding
import com.legion1900.dchat.domain.dto.Account

class ContactSearchAdapter : RecyclerView.Adapter<ContactSearchAdapter.ContactHolder>() {

    private val contacts = mutableListOf<Account>()

    // Map of <account ID, account position> to store indices of account in list
    private val positions = mutableMapOf<String, Int>()
    private val avatars = mutableMapOf<String, ByteArray>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemContactSearchBinding.inflate(inflater)
        return ContactHolder(binding).apply { avatar.clipToOutline = true }
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        val account = contacts[position]
        val avatarBytes = avatars[account.id]
        holder.apply {
            avatarBytes?.let { loadAvatar(it, avatar) }
            placeholder.text = account.name.first().toString()
            name.text = account.name
            id.text = account.id
        }
    }

    private fun loadAvatar(avatarBytes: ByteArray, avatar: ImageView) {
        Glide.with(avatar)
            .asBitmap()
            .load(avatarBytes)
            .into(avatar)
    }

    override fun getItemCount(): Int = contacts.size

    fun setResult(result: List<Account>, avatars: Map<String, ByteArray>? = null) {
        contacts += result
        avatars?.let { this.avatars += avatars }
        result.withIndex()
            .forEach { (i, account) -> positions[account.id] = i }
        notifyDataSetChanged()
    }

    fun newAvatar(userId: String, avatar: ByteArray) {
        avatars[userId] = avatar
        val position = positions.getValue(userId)
        notifyItemChanged(position)
    }

    class ContactHolder(binding: ItemContactSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        val avatar = binding.contactAvatar
        val placeholder = binding.avatarPlaceholder
        val name = binding.name
        val id = binding.id
    }
}
