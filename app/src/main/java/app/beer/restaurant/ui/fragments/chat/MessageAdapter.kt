package app.beer.restaurant.ui.fragments.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import app.beer.restaurant.R
import app.beer.restaurant.model.chat.Message
import app.beer.restaurant.util.USER
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    private var messages = ArrayList<Message>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val blockMyMessage: CardView = itemView.findViewById(R.id.my_message)
        val myMessageText: TextView = itemView.findViewById(R.id.my_message_text)
        val myMessageTime: TextView = itemView.findViewById(R.id.my_message_time)

        val blockReceiverMessage: CardView = itemView.findViewById(R.id.receiver_message)
        val receiverMessageText: TextView = itemView.findViewById(R.id.receiver_message_text)
        val receiverMessageTime: TextView = itemView.findViewById(R.id.receiver_message_time)
    }

    fun setMessages(messages: List<Message>) {
        val pos = this.messages.size
        this.messages = messages as ArrayList<Message>
        notifyItemRangeInserted(pos, this.messages.size)
    }

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size)
    }

    fun isExists(message: Message): Boolean {
        return messages.indexOf(message) == -1
    }

    fun getMessages(): ArrayList<Message> {
        return messages
    }

    override fun getItemViewType(position: Int): Int = position

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        if (message.from_id == USER.id.toString()) {
            holder.blockMyMessage.visibility = View.VISIBLE
            holder.blockReceiverMessage.visibility = View.GONE
            holder.myMessageText.text = message.message
            holder.myMessageTime.text = asTime(message.time)
        } else {
            holder.blockMyMessage.visibility = View.GONE
            holder.blockReceiverMessage.visibility = View.VISIBLE
            holder.receiverMessageText.text = message.message
            holder.receiverMessageTime.text = asTime(message.time)
        }
    }

    fun asTime(time: Date): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(time)
    }

    companion object {
        const val MY_MESSAGE = 1
        const val RECEIVER_MESSAGE = 2
    }

}