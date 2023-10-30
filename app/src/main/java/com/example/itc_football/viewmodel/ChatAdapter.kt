package com.example.itc_football.viewmodel

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.itc_football.data.Chat
import com.example.itc_football.databinding.ItemChatOtherBinding
import com.example.itc_football.databinding.ItemChatSelfBinding

class ChatAdapter(private var userName: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_SELF = 1
    private val ITEM_OTHER = 2

    private val diffcallback = object : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.text == newItem.text && oldItem.username == newItem.username
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }


    private val differ = AsyncListDiffer(this, diffcallback)

    fun submitChat(chats: List<Chat>) {
        differ.submitList(chats)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SELF) {
            val binding =
                ItemChatSelfBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SelfChatItemViewHolder(binding)
        } else {
            val binding =
                ItemChatOtherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            OtherChatItemViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = differ.currentList[position]
        when (holder) {
            is SelfChatItemViewHolder -> {
                if (chat.isSelf) {
                    holder.bind(chat)
                }
            }

            is OtherChatItemViewHolder -> {
                if (!chat.isSelf) {
                    holder.bind(chat)
                }
            }
        }
    }


    inner class OtherChatItemViewHolder(val binding: ItemChatOtherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.apply {
                name.text = chat.username
                msg.text = chat.text
            }
        }

    }

    inner class SelfChatItemViewHolder(val binding: ItemChatSelfBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.apply {
                name.text = chat.username
                msg.text = chat.text
            }
        }

    }


    override fun getItemViewType(position: Int): Int {
        val chat = differ.currentList[position]
        val viewType = if (chat.isSelf) ITEM_SELF else ITEM_OTHER
        Log.d(
            "ChatAdapter",
            "getItemViewType: position = $position, viewType = $viewType, chat = $chat"
        )
        return viewType
    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}