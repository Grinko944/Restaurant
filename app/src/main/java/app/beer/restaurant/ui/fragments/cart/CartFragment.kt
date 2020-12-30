package app.beer.restaurant.ui.fragments.cart

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.beer.restaurant.R
import app.beer.restaurant.api.Api
import app.beer.restaurant.model.basket.Basket
import app.beer.restaurant.model.basket.BasketResponse
import app.beer.restaurant.util.*
import app.beer.restaurant.viewmodel.MainViewModel
import com.google.android.material.button.MaterialButton
import java.math.BigDecimal

class CartFragment : Fragment(), CartAdapter.OnClickListener {

    private lateinit var api: Api

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CartAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var data: ArrayList<Basket>

    private var totalPrice = BigDecimal(0)
    private lateinit var priceTotal: TextView

    private lateinit var continueBuyBtn: MaterialButton
    private lateinit var continueBuyBtnContainer: LinearLayout

    private lateinit var progressBar: ProgressBar

    private lateinit var noHaveItemsTitle: TextView

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(APP_ACTIVITY).get(MainViewModel::class.java)
        api = APP.getApi()
        data = ArrayList()

        recyclerView = view.findViewById(R.id.cart_recycler_view)
        continueBuyBtn = view.findViewById(R.id.continue_buy_btn)

        continueBuyBtnContainer = view.findViewById(R.id.continue_buy_btn_container)
        continueBuyBtnContainer.visibility = View.GONE

        progressBar = view.findViewById(R.id.progress_bar)
        noHaveItemsTitle = view.findViewById(R.id.text_no_have_items_in_cart)
        noHaveItemsTitle.visibility = View.GONE
        priceTotal = view.findViewById(R.id.price_total)

        initRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        getProducts()
    }

    private fun initRecyclerView() {
        adapter = CartAdapter()
        adapter.setListener(this)
        layoutManager = LinearLayoutManager(APP_ACTIVITY)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
    }

    private fun getProducts() {
        progressBar.visibility = View.VISIBLE
        api.getBasketItems(USER.id).enqueue(RetrofitCallback<BasketResponse> { _, response ->
            if (response.isSuccessful && response.code() != 404) {
                val body = response.body()
                if (body?.results != null) {
                    data = body.results as ArrayList<Basket>
                    adapter.setData(data)
                    if (adapter.getData().size == 0) {
                        noHaveItemsTitle.visibility = View.VISIBLE
                        continueBuyBtnContainer.visibility = View.GONE
                        priceTotal.text = getString(R.string.current_carrency, "0")
                    } else {
                        continueBuyBtnContainer.visibility = View.VISIBLE
                        noHaveItemsTitle.visibility = View.GONE
                        adapter.getData().forEach {
                            val price = getPriceForBasket(it)
                            totalPrice += if (it.productCount > 1) {
                                price * it.productCount.toBigDecimal()
                            } else {
                                price
                            }
                        }
                        priceTotal.text = getString(R.string.current_carrency, totalPrice)

                        val ids: ArrayList<Int> = ArrayList()
                        var name = ""
                        for (item in adapter.getData()) {
                            ids.add(item.productId)
                            name += item.name + ", "
                        }
                        name = name.substring(0, name.length - 1)
                        continueBuyBtn.setOnClickListener {
                            replaceFragment(
                                ContinueOrderBuyFragment(ids.toIntArray(), name)
                            )
                        }
                    }
                    progressBar.visibility = View.GONE
                }
            }
        })
    }

    override fun onClickDelete(product: Basket) {
        api.deleteProductFromBasket(product.b_id)
            .enqueue(RetrofitCallback<BasketResponse> { _, response ->
                if (response.isSuccessful && response.code() != 404) {
                    val body = response.body()
                    if (body?.message != null) {
                        if (data.size > 1) {
                            var price = getPriceForBasket(product)
                            if (product.productCount > 1) {
                                price *= product.productCount.toBigDecimal()
                            }
                            totalPrice -= price
                        } else if (data.size == 1) {
                            totalPrice = "0".toBigDecimal()
                        }
                        priceTotal.text =
                            getString(R.string.current_carrency, totalPrice.toString())
                        adapter.deleteItem(adapter.getProductById(product.b_id))
                        showToast(body.message!!)
                        if (adapter.getData().size == 0) {
                            continueBuyBtnContainer.animate().alpha(0f).duration = 500
                            // continueBuyBtnContainer.visibility = View.GONE
                        }
                        APP_ACTIVITY.createBadgeBTN()
                    }
                }
            })
    }

//    private var isStopped = false

    override fun onClickPlus(product: Basket) {
//        if (isStopped) {
//            api.updateProductFromBasket(product.b_id, product.productCount + 1)
//                .enqueue(RetrofitCallback<BasketResponse> { _, response ->
//                    if (response.isSuccessful && response.code() != 404) {
//
//                    }
//                })
//        }
    }

    override fun onClickMinus(product: Basket) {

    }

}