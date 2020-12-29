package app.beer.restaurant.ui.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.beer.restaurant.R
import app.beer.restaurant.api.App
import app.beer.restaurant.model.product.Product
import app.beer.restaurant.model.product.ProductsResponse
import app.beer.restaurant.util.APP
import app.beer.restaurant.util.APP_ACTIVITY
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainFragment : Fragment() {

    private lateinit var progressBar: ProgressBar

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MainAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var data = ArrayList<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.products_recycler_view)
        progressBar = view.findViewById(R.id.progress_bar)

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
        progressBar.visibility = View.VISIBLE
        APP.getApi()
            .getProducts()
            .enqueue(object : Callback<ProductsResponse> {
                override fun onFailure(call: Call<ProductsResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
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
                            progressBar.visibility = View.GONE
                        }
                    }
                }

            })
    }

}