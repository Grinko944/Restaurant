package app.beer.restaurant.ui.fragments.show

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import app.beer.restaurant.R
import app.beer.restaurant.api.Api
import app.beer.restaurant.api.BASE_URL
import app.beer.restaurant.model.basket.BasketResponse
import app.beer.restaurant.model.product.Product
import app.beer.restaurant.model.product.ProductsResponse
import app.beer.restaurant.util.*
import com.google.android.material.button.MaterialButton

class ShowFragment : Fragment() {

    private lateinit var api: Api
    private lateinit var product: Product

    private lateinit var progressBar: ProgressBar
    private lateinit var productImage: ImageView
    private lateinit var productName: TextView
    private lateinit var productPrice: TextView
    private lateinit var productDescription: TextView
    private lateinit var addToCartBtn: MaterialButton

    private var productId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        APP_ACTIVITY.toolbar.setNavigationOnClickListener { APP_ACTIVITY.supportFragmentManager.popBackStack() }
        return inflater.inflate(R.layout.fragment_show, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        APP_ACTIVITY.supportActionBar?.setDisplayShowHomeEnabled(true)
        productId = arguments?.getInt(PRODUCT_ID_KEY)!!

        progressBar = view.findViewById(R.id.show_progress_bar)

        productImage = view.findViewById(R.id.product_image)
        productName = view.findViewById(R.id.product_name)
        productPrice = view.findViewById(R.id.product_price)
        productDescription = view.findViewById(R.id.product_description)
        addToCartBtn = view.findViewById(R.id.add_to_cart_btn)
        addToCartBtn.alpha = 0f

        api = APP.getApi()
        getProduct()
    }

    private fun getProduct() {
        progressBar.visibility = View.VISIBLE
        api.getProductById(productId).enqueue(RetrofitCallback<Product> { _, response ->
            val body = response.body()
            if (body != null) {
                product = body
                init()
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun init() {
        addToCartBtn.animate().alpha(1f).duration = 500
        productImage.loadAndSetImage(BASE_URL + product.photoUrl)

        when {
            APP_ACTIVITY.sharedManager.getString(LANGUAGE_KEY) == LANGUAGE_ENG -> {
                productName.text = product.name
                productPrice.text = getString(R.string.current_carrency, product.price_USD)
                productDescription.text = product.description
            }
            APP_ACTIVITY.sharedManager.getString(LANGUAGE_KEY) == LANGUAGE_RUS -> {
                productPrice.text = getString(R.string.current_carrency, product.price)
                productName.text = product.name_RU
                productDescription.text = product.description_RU
            }
            APP_ACTIVITY.sharedManager.getString(LANGUAGE_KEY) == LANGUAGE_DOT -> {
                productPrice.text = getString(R.string.current_carrency, product.price_EURO)
                productName.text = product.name_DE
                productDescription.text = product.description_DE
            }
            else -> {
                productPrice.text = getString(R.string.current_carrency, product.price_USD)
                productName.text = product.name_BG
                productDescription.text = product.description_BG
            }
        }

        addToCartBtn.setOnClickListener {
            addProductToCart()
        }
    }

    private fun addProductToCart() {
        api.addProductToBasket(product.id, USER.id, 1).enqueue(RetrofitCallback<BasketResponse> { _, response ->
            if (response.isSuccessful && response.code() != 404) {
                val body = response.body()
                if (body != null) {
                    showToast(body.message!!)
                    APP_ACTIVITY.createBadgeBTN()
                }
            }
        })
    }

    companion object {
        const val PRODUCT_ID_KEY = "product_id"

        fun newInstance(id: Int): Fragment {
            val args = Bundle()
            args.putInt(PRODUCT_ID_KEY, id)
            val fragment = ShowFragment()
            fragment.arguments = args
            return fragment
        }
    }

}