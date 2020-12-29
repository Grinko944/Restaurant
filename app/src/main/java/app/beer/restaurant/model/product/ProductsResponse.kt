package app.beer.restaurant.model.product

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProductsResponse(
    @SerializedName("results")
    @Expose
    var results: List<Product> = ArrayList()
)