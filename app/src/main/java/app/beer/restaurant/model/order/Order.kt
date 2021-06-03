package app.beer.restaurant.model.order

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("date")
    var date: Long = 0L,
    @SerializedName("product_ids")
    var names: String = "",
    @SerializedName("price")
    var price: Double = 0.00,
    @SerializedName("status")
    var status: String = ""
)