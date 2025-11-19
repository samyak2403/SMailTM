package com.samyak.smalitm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.samyak.smalitm.util.Message
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter : ListAdapter<Message, MessageAdapter.MessageViewHolder>(MessageDiffCallback()) {

    private var onItemClickListener: ((Message) -> Unit)? = null

    fun setOnItemClickListener(listener: (Message) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(message)
        }
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSender: TextView = itemView.findViewById(R.id.tvSender)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvSubject: TextView = itemView.findViewById(R.id.tvSubject)
        private val tvPreview: TextView = itemView.findViewById(R.id.tvPreview)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(message: Message) {
            tvSender.text = message.senderAddress
            tvSubject.text = message.subject.ifEmpty { "(No Subject)" }
            tvPreview.text = message.content.take(100).ifEmpty { "(No Content)" }
            
            // Format time
            tvTime.text = formatTime(message.createdAt)
            
            // Status
            if (message.seen) {
                tvStatus.text = "✓ Read"
                tvStatus.setTextColor(itemView.context.getColor(R.color.white))
                tvStatus.alpha = 0.5f
            } else {
                tvStatus.text = "● Unread"
                tvStatus.setTextColor(itemView.context.getColor(R.color.gradient_3_start))
                tvStatus.alpha = 1.0f
            }
        }

        private fun formatTime(timestamp: String): String {
            return try {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'", Locale.getDefault())
                val date = sdf.parse(timestamp)
                val now = Date()
                val diff = now.time - (date?.time ?: 0)
                
                when {
                    diff < 60000 -> "Just now"
                    diff < 3600000 -> "${diff / 60000}m ago"
                    diff < 86400000 -> "${diff / 3600000}h ago"
                    else -> "${diff / 86400000}d ago"
                }
            } catch (e: Exception) {
                "Recently"
            }
        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}
