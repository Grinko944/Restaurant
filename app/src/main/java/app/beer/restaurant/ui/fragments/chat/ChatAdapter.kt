package app.beer.restaurant.ui.fragments.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.beer.restaurant.R
import app.beer.restaurant.model.chat.Chat
import app.beer.restaurant.model.chat.Message

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private var data = ArrayList<Chat>()
    private var listener: OnChatClickListener? = null

    interface OnChatClickListener {
        fun onClick(chatId: String)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatName: TextView = itemView.findViewById(R.id.chat_name)
    }

    fun setData(items: List<Chat>) {
        val pos = data.size
        data = items as ArrayList<Chat>
        notifyItemRangeInserted(pos, data.size)
    }

    fun setListener(listener: OnChatClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatItem = data[position]
        holder.chatName.text = chatItem.name
        holder.itemView.setOnClickListener {
            listener?.onClick(chatItem.id)
        }
    }

}