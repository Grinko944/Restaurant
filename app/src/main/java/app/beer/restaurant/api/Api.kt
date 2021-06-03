package app.beer.restaurant.api

import app.beer.restaurant.model.User
import app.beer.restaurant.model.auth.AuthResponse
import app.beer.restaurant.model.basket.Basket
import app.beer.restaurant.model.basket.BasketResponse
import app.beer.restaurant.model.order.Order
import app.beer.restaurant.model.order.OrderListResponse
import app.beer.restaurant.model.order.OrderResponse
import app.beer.restaurant.model.product.Product
import app.beer.restaurant.model.product.ProductsResponse
import retrofit2.Call
import retrofit2.http.*

interface Api {

    @GET("products")
    fun getProducts(): Call<ProductsResponse>

    @GET("auth")
    fun auth(@Query("email") email: String, @Query("password") password: String = "", @Query("social_user_id") socialAuthId: String? = null): Call<AuthResponse>

    @GET("products/show/{id}")
    fun getProductById(@Path("id") id: Int): Call<Product>

    @GET("users/get/{id}")
    fun getUser(@Path("id") id: Int): Call<User>

    @GET("user/edit/{id}")
    fun updateUser(
        @Path("id") id: Int, @Query("name") name: String, @Query("password") password: String = "",
        @Query("email") email: String
    ): Call<User>

    @GET("basket/{id}")
    fun getBasketItems(@Path("id") id: Int): Call<BasketResponse>

    @GET("basket/add/product")
    fun addProductToBasket(
        @Query("product_id") productId: Int, @Query("user_id") user_id: Int,
        @Query("product_count") productCount: Int
    ): Call<BasketResponse>

    @GET("basket/delete/product")
    fun deleteProductFromBasket(@Query("product_id") productId: Int): Call<BasketResponse>

    @GET("basket/delete/all/{id}")
    fun deleteAllProductsFromBasket(@Path("id") id: Int): Call<BasketResponse>

    @GET("basket/update/product/{id}")
    fun updateProductFromBasket(
        @Path("id") id: Int,
        @Query("product_count") productCount: Int
    ): Call<BasketResponse>

    @GET("basket/orders/add")
    fun addOrdersFromBasket(
        @Query("ids") ids: String,
        @Query("user_id") user_id: Int,
        @Query("delivery_time") deliveryTime: String
    ): Call<OrderResponse>

    @GET("orders/get")
    fun getOrders(@Query("user_id") userId: Int, @Query("lang") lang: String = "ru"): Call<OrderListResponse>

}