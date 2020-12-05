package com.legion1900.dchat.view.chat.messagelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.legion1900.dchat.databinding.ItemMessageBinding
import com.legion1900.dchat.domain.dto.message.NewMessageModelEvent
import com.legion1900.dchat.domain.dto.message.TextModel
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter : ListAdapter<NewMessageModelEvent, MessageAdapter.MessageHolder>(diffCallback) {

    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val inflater = LayoutInflater.from(parent.context)
        val b = ItemMessageBinding.inflate(inflater, parent, false)
        return MessageHolder(b)
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        bind(getItem(position), holder.binding)
    }

    fun bind(msg: NewMessageModelEvent, binding: ItemMessageBinding) {
        binding.apply {
            name.text = msg.senderName
            avatarPlaceholder.text = msg.senderName.first().toString()
            time.text = dateFormat.format(Date(msg.timestamp))
            val text = (msg.content as? TextModel)?.text ?: "Photos are not supported"
            msgText.text = text
        }
    }

    class MessageHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)

    private companion object {
        val diffCallback = object : DiffUtil.ItemCallback<NewMessageModelEvent>() {
            override fun areItemsTheSame(oldItem: NewMessageModelEvent, newItem: NewMessageModelEvent): Boolean {
                return oldItem.timestamp == newItem.timestamp
            }

            override fun areContentsTheSame(oldItem: NewMessageModelEvent, newItem: NewMessageModelEvent): Boolean {
                return oldItem == newItem
            }
        }
    }
}
