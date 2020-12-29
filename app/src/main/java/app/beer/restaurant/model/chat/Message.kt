package app.beer.restaurant.model.chat

import java.util.*

data class Message(
    var message: String = "",
    var from_id: String = "",
    var order_id: String = "",
    var time: Date = Date()
)