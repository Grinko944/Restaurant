package app.beer.restaurant.ui.fragments.account.orders

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import app.beer.restaurant.R
import app.beer.restaurant.databinding.FragmentOrdersBinding
import app.beer.restaurant.model.order.Order
import app.beer.restaurant.model.order.OrderListResponse
import app.beer.restaurant.model.order.OrderResponse
import app.beer.restaurant.util.*

class OrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding

    private lateinit var sharedManager: SharedManager

    private var euro = 90.00
    private var usd = 75.00
    private var orders = arrayListOf<Order>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        APP_ACTIVITY.toolbar.setNavigationOnClickListener { APP_ACTIVITY.supportFragmentManager.popBackStack() }
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_orders, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedManager = SharedManager()

        getOrders()
    }

    private fun getOrders() {
        APP.getApi().getOrders(sharedManager.getInt(USER_ID_KEY), getLanguageCode(sharedManager))
            .enqueue(RetrofitCallback<OrderListResponse> { _, response ->
                val orderResponse = response.body()
                if ((response.code() !in 400..404 && response.code() !in 500..504) && response.isSuccessful && orderResponse != null) {
                    orders.clear()
                    orders.addAll(orderResponse.orders)
                    euro = orderResponse.euro.toDouble()
                    usd = orderResponse.usd.toDouble()
                    insertOrdersInTable()
                }
            })
    }

    private fun insertOrdersInTable() {
        val localStr = getLanguageCode(sharedManager)
        val rate = when (localStr) {
            "ru" -> 1.0
            "en" -> usd
            "de" -> euro
            "bg" -> usd
            else -> 1.0
        }
        orders.map {
            val idOfOrder = getTextView("№${it.id}")
            val products =
                getTextView(if (it.names.endsWith(",")) it.names.substring(0, -1) else it.names)
            val status = getTextView(
                when (it.status) {
                    "Новый" -> resources.getString(R.string.status_accepted)
                    "New" -> resources.getString(R.string.status_accepted)
                    "Готовиться" -> resources.getString(R.string.status_prepare)
                    "Доставляется" -> resources.getString(R.string.status_delivered)
                    "Выполнен" -> resources.getString(R.string.status_completed)
                    else -> resources.getString(R.string.status_accepted)
                }, if (it.status == "Выполнен")
                    resources.getColor(android.R.color.holo_green_light)
                else
                    resources.getColor(R.color.primary_text_color)
            )
            val timeOfSentOrder = getTextView(it.date.getDateTime())
            val price = getTextView(
                resources.getString(
                    R.string.current_carrency,
                    (it.price / rate).toString()
                )
            )

            val newTableRow = TableRow(requireContext())
            newTableRow.addView(idOfOrder)
            newTableRow.addView(products)
            newTableRow.addView(status)
            newTableRow.addView(timeOfSentOrder)
            newTableRow.addView(price)
            binding.tableOfOrders.addView(newTableRow)
        }
        binding.progressBar.visibility = View.GONE
    }

    private fun getTextView(
        textS: String,
        textColor: Int = resources.getColor(R.color.primary_text_color)
    ): TextView {
        return TextView(requireContext()).apply {
            text = textS
            setPadding(32, 32, 32, 32)
            setTextColor(textColor)
            textSize = 14f
            layoutParams = TableRow.LayoutParams(1)
            gravity = Gravity.CENTER
            minWidth = 60
        }
    }

}