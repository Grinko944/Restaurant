package app.beer.restaurant.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.beer.restaurant.model.basket.Basket
import app.beer.restaurant.model.basket.BasketResponse
import app.beer.restaurant.model.product.Product
import app.beer.restaurant.util.APP
import app.beer.restaurant.util.RetrofitCallback
import app.beer.restaurant.util.USER
import retrofit2.Response
import java.math.BigDecimal

class MainViewModel : ViewModel() {

    var productLiveData = MutableLiveData<List<Product>>()
    var cartProductsLiveData = MutableLiveData<List<Basket>>()
    var priceLiveData = MutableLiveData<BigDecimal>()

    fun cartProduct(onSuccess: (response: Response<BasketResponse>) -> Unit) {
        APP.getApi().getBasketItems(USER.id).enqueue(RetrofitCallback<BasketResponse> { _, response ->
            if (response.isSuccessful && response.code() != 404) {
                if (response.body() != null) {
                    cartProductsLiveData.value = response.body()!!.results
                    var totalPrice = BigDecimal(0)
                    response.body()!!.results?.forEach {
                         totalPrice += if (it.productCount > 1) {
                            it.price.toBigDecimal() * it.productCount.toBigDecimal()
                        } else {
                            it.price.toBigDecimal()
                        }
                    }
                    priceLiveData.value = totalPrice
                    onSuccess(response)
                }
            }
        })
    }

    interface OnSuccess {
        fun onSuccess(any: Any)
    }

}