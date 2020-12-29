package app.beer.restaurant.ui.fragments.cart;

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment

import app.beer.restaurant.R
import app.beer.restaurant.api.Api
import app.beer.restaurant.database.NODE_CHATS
import app.beer.restaurant.database.NODE_MESSAGES
import app.beer.restaurant.database.REF_DATABASE_ROOT
import app.beer.restaurant.model.basket.BasketResponse
import app.beer.restaurant.model.order.OrderResponse
import app.beer.restaurant.ui.fragments.main.MainFragment
import app.beer.restaurant.util.*
import com.google.android.material.button.MaterialButton
import java.util.*

class ContinueOrderBuyFragment(val ids: IntArray, val name: String) : Fragment() {

    private lateinit var api: Api

    private val newCalendar = Calendar.getInstance()

    private lateinit var date: String
    private lateinit var time: String

    private lateinit var selectedTimePlaceholder: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var selectDayBtn: MaterialButton
    private lateinit var selectTimeBtn: MaterialButton
    private lateinit var createOrderBtn: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        APP_ACTIVITY.toolbar.setNavigationOnClickListener { APP_ACTIVITY.supportFragmentManager.popBackStack() }
        return inflater.inflate(R.layout.fragment_continue_order_buy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        api = APP.getApi()

        selectedTimePlaceholder = view.findViewById(R.id.selected_time_placeholder)
        selectDayBtn = view.findViewById(R.id.select_day_btn)
        selectTimeBtn = view.findViewById(R.id.select_time_btn)
        createOrderBtn = view.findViewById(R.id.create_order_btn)

        progressBar = view.findViewById(R.id.progress_bar)

        selectDayBtn.setOnClickListener { createDatePicker() }
        selectTimeBtn.setOnClickListener { createTimePicker() }
        createOrderBtn.setOnClickListener { createOrder() }
    }

    private fun createDatePicker() {
        val listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val day = when (dayOfMonth) {
                newCalendar.get(Calendar.DAY_OF_MONTH) -> {
                    "Сегодня,"
                }
                newCalendar.get(Calendar.DAY_OF_MONTH) + 1 -> {
                    "Завтра,"
                }
                newCalendar.get(Calendar.DAY_OF_MONTH) + 2 -> {
                    "Послезавтра,"
                }
                else -> {
                    "$dayOfMonth, ${getMonth(month)}"
                }
            }
            date = day
        }

        val pickerDialog = DatePickerDialog(
            APP_ACTIVITY, listener,
            newCalendar.get(Calendar.YEAR),
            newCalendar.get(Calendar.MONTH),
            newCalendar.get(Calendar.DAY_OF_MONTH)
        )

        pickerDialog.show()
    }

    private fun createTimePicker() {
        val timeListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            date = "$date в $hourOfDay:$minute"
            selectedTimePlaceholder.text = date
        }
        val timePickerDialog = TimePickerDialog(APP_ACTIVITY, timeListener,
            newCalendar.get(Calendar.HOUR_OF_DAY),
            newCalendar.get(Calendar.MINUTE),
            true)
        timePickerDialog.show()
    }

    private fun createOrder() {
        progressBar.visibility = View.VISIBLE
        var idsStr = ""
        for (id in ids) {
            idsStr += "$id,"
        }
        idsStr = idsStr.substring(0, idsStr.length - 1)
        api.addOrdersFromBasket(idsStr, USER.id, date).enqueue(RetrofitCallback<OrderResponse> { _, response ->
            if (response.isSuccessful && response.code() != 404) {
                val body = response.body()
                if (body != null) {
                    showToast(body.message)
                    replaceFragment(MainFragment())
                    api.deleteAllProductsFromBasket(USER.id).enqueue(RetrofitCallback<BasketResponse> { _, response ->
                        if (response.isSuccessful && response.code() != 404) {
                            REF_DATABASE_ROOT.child(NODE_CHATS)
                                .child(USER.id.toString())
                                .child(idsStr)
                                .setValue(hashMapOf(Pair("id", idsStr), Pair("name", name), Pair("user_id", USER.id)))
                            APP_ACTIVITY.createBadgeBTN()
                        }
                    })
                    progressBar.visibility = View.GONE
                }
            }
        })
    }

    private fun getMonth(month: Int): String {
        val monthNames = arrayOf("Января", "Февраля", "Марта", "Апреля", "Мая", "Июня", "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря")
        return monthNames[month]
    }

}