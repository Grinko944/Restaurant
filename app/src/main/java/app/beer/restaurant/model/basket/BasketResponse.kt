package app.beer.restaurant.model.basket

import com.google.gson.annotations.SerializedName

data class BasketResponse(
    @SerializedName("results")
    var results: List<Basket>? = ArrayList(),
    @SerializedName("message")
    var message: String? = ""
)