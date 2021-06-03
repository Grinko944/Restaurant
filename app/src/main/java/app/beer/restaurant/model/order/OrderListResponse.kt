package app.beer.restaurant.model.order

import com.google.gson.annotations.SerializedName

data class OrderListResponse(
    @SerializedName("results")
    var orders: List<Order> = arrayListOf(),
    @SerializedName("usd")
    var usd: String = "",
    @SerializedName("euro")
    var euro: String = ""
)
