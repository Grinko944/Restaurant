package app.beer.restaurant.ui.fragments.main

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.beer.restaurant.R
import app.beer.restaurant.api.App
import app.beer.restaurant.model.product.Product
import app.beer.restaurant.model.product.ProductsResponse
import app.beer.restaurant.util.APP
import app.beer.restaurant.util.APP_ACTIVITY
import app.beer.restaurant.util.SharedManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainFragment : Fragment() {

    private lateinit var noHaveProductsLabel: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MainAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var data = ArrayList<Product>()

    private lateinit var sharedManager: SharedManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedManager = SharedManager()

        recyclerView = view.findViewById(R.id.products_recycler_view)
        noHaveProductsLabel = view.findViewById(R.id.no_have_products_label)
        swipeRefresh = view.findViewById(R.id.swipe_refresh)

        swipeRefresh.run {
            isRefreshing = false
            setColorSchemeColors(Color.YELLOW, Color.CYAN)
            setOnRefreshListener {
                getProducts()
                isRefreshing = false
            }
        }

        initRecyclerView()
        getProducts()
    }

    private fun initRecyclerView() {
        adapter = MainAdapter(data)
        layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
    }

    private fun getProducts() {
        swipeRefresh.isRefreshing = true
        APP.getApi()
            .getProducts()
            .enqueue(object : Callback<ProductsResponse> {
                override fun onFailure(call: Call<ProductsResponse>, t: Throwable) {
                    swipeRefresh.isRefreshing = false
                }

                override fun onResponse(
                    call: Call<ProductsResponse>,
                    response: Response<ProductsResponse>
                ) {
                    if (response.isSuccessful && response.code() != 404) {
                        val items = response.body()
                        if (items != null) {
                            data = items.results as ArrayList<Product>
                            adapter.setData(data)
                            swipeRefresh.isRefreshing = false
                            if (items.results.isNotEmpty()) {
                                noHaveProductsLabel.visibility = View.GONE
                            } else {
                                noHaveProductsLabel.visibility = View.VISIBLE
                            }
                        }
                    }
                }

            })
    }

}