package app.beer.restaurant.ui.fragments.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.beer.restaurant.R
import app.beer.restaurant.database.NODE_CHATS
import app.beer.restaurant.database.NODE_MESSAGES
import app.beer.restaurant.database.REF_DATABASE_ROOT
import app.beer.restaurant.model.chat.Chat
import app.beer.restaurant.util.APP_ACTIVITY
import app.beer.restaurant.util.USER
import app.beer.restaurant.util.replaceFragment
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ChatFragment : Fragment(), ChatAdapter.OnChatClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.chat_recycler_view)
        adapter = ChatAdapter()
        adapter.setListener(this)
        layoutManager = LinearLayoutManager(APP_ACTIVITY)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        getChats()
    }

    private fun getChats() {
        REF_DATABASE_ROOT.child(NODE_CHATS)
            .child(USER.id.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = ArrayList<Chat>()
                    for (dataSnapshot in snapshot.children) {
                        val item = dataSnapshot.getValue(Chat::class.java)
                        if (item != null) {
                            data.add(item)
                        }
                    }
                    adapter.setData(data)
                }
            })
    }

    override fun onClick(chatId: String) {
        replaceFragment(SingleChatFragment.newInstance(chatId))
    }

}