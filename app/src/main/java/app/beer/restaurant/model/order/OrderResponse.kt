package app.beer.restaurant.model.order

import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("message")
    var message: String = ""
)