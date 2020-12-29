package app.beer.restaurant.database

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

lateinit var REF_DATABASE_ROOT: DatabaseReference

const val NODE_MESSAGES = "messages"
const val NODE_CHATS = "chats"

const val CHILD_FROM_ID = "from_id"
const val CHILD_ORDER_ID = "order_id"
const val CHILD_MESSAGE = "message"
const val CHILD_TIME = "time"

fun initFirebase() {
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
}
