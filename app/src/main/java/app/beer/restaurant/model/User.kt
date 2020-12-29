package app.beer.restaurant.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("email")
    var email: String = "",
    @SerializedName("role_id")
    var role: Int = 4,
    @SerializedName("password_encrypted")
    var password: String = ""
)