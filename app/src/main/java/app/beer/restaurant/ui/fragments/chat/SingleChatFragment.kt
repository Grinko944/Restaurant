package app.beer.restaurant.ui.fragments.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.beer.restaurant.R
import app.beer.restaurant.database.*
import app.beer.restaurant.model.chat.Message
import app.beer.restaurant.util.APP_ACTIVITY
import app.beer.restaurant.util.USER
import app.beer.restaurant.util.getMessageModel
import app.beer.restaurant.util.showToast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.HashMap

class SingleChatFragment : Fragment() {

    private var productIds: String = ""

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var messagesListener: ValueEventListener

    private var messages = emptyList<Message>()

    private lateinit var messageEditText: EditText
    private lateinit var sendBtn: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_single_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productIds = arguments?.getString(PRODUCT_IDS_KEY, "")!!

        messageEditText = view.findViewById(R.id.message_edit_text)
        sendBtn = view.findViewById(R.id.send_message_btn)

        recyclerView = view.findViewById(R.id.messages_recycler_view)
        adapter = MessageAdapter()
        layoutManager = LinearLayoutManager(APP_ACTIVITY)
        adapter.setHasStableIds(true)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        getMessages()

        sendBtn.setOnClickListener {
            val message = messageEditText.text.toString().trim()
            if (message != "") {
                sendMessage(message)
                messageEditText.text.clear()
            } else {
                showToast(getString(R.string.enter_message_error))
            }
        }
    }

    private fun getMessages() {
        messagesListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                messages = snapshot.children.map { it.getMessageModel() }
                adapter.setMessages(messages)
                if (messages.size > 2) {
                    recyclerView.smoothScrollToPosition(messages.size - 1)
                }
            }
        }

        REF_DATABASE_ROOT.child(NODE_MESSAGES)
            .child(USER.id.toString())
            .child(productIds)
            .addValueEventListener(messagesListener)
    }

    private fun sendMessage(message: String) {
        val messageHashMap = HashMap<String, Any>()
        messageHashMap[CHILD_FROM_ID] = USER.id.toString()
        messageHashMap[CHILD_ORDER_ID] = productIds
        messageHashMap[CHILD_TIME] = Date()
        messageHashMap[CHILD_MESSAGE] = message

        REF_DATABASE_ROOT.child(NODE_MESSAGES)
            .child(USER.id.toString())
            .child(productIds)
            .push()
            .setValue(messageHashMap)
            .addOnSuccessListener {
                recyclerView.smoothScrollToPosition(messages.size - 1)
            }
            .addOnFailureListener { showToast("Что-то пошло не так! ${it.message}") }
    }

    // от утечки памяти
    override fun onDestroy() {
        super.onDestroy()
        REF_DATABASE_ROOT.child(NODE_MESSAGES)
            .child(USER.id.toString())
            .child(productIds)
            .removeEventListener(messagesListener)
    }

    companion object {
        const val PRODUCT_IDS_KEY = "product_ids"

        fun newInstance(productIds: String): SingleChatFragment {
            val args = Bundle()
            args.putString(PRODUCT_IDS_KEY, productIds)
            val fragment = SingleChatFragment()
            fragment.arguments = args
            return fragment
        }
    }

}