package app.beer.restaurant.model.auth

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("status")
    var status: String = "",
    @SerializedName("id")
    var id: Int = 0
)