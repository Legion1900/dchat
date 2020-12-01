package com.legion1900.dchat.view.chat.chatlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.legion1900.dchat.databinding.ItemChatBinding
import com.legion1900.dchat.domain.dto.chat.ChatModel

class ChatAdapter(
    private val onClick: (View) -> Unit
) : ListAdapter<ChatModel, ChatAdapter.ChatHolder>(diffCallback) {

    private lateinit var requestManager: RequestManager

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        requestManager = Glide.with(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChatBinding.inflate(inflater, parent, false)
        binding.root.setOnClickListener(onClick)
        binding.chatAvatar.clipToOutline = true
        return ChatHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatHolder, position: Int) {
        val chat = getItem(position)
        holder.bind(chat, requestManager)
    }

    class ChatHolder(val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: ChatModel, manager: RequestManager) {
            binding.apply {
                val msgString = model.lastMsg?.run { "$senderName: $msgText" } ?: "No messages yet"
                chatName.text = model.name
                lastMsg.text = msgString
                avatarPlaceholder.text = model.name.first().toString()
                model.avatar?.let {
                    avatarPlaceholder.isVisible = false
                    manager.asBitmap()
                        .load(it)
                        .into(chatAvatar)
                } ?: kotlin.run { avatarPlaceholder.isVisible = true }
            }
        }
    }

    private companion object {
        val diffCallback = object : DiffUtil.ItemCallback<ChatModel>() {
            override fun areItemsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
