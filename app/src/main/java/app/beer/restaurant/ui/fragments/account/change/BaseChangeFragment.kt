package app.beer.restaurant.ui.fragments.account.change

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import app.beer.restaurant.R
import app.beer.restaurant.api.Api
import app.beer.restaurant.util.APP
import app.beer.restaurant.util.APP_ACTIVITY

open class BaseChangeFragment(@LayoutRes itemId: Int) : Fragment(itemId) {

    lateinit var api: Api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api = APP.getApi()
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        APP_ACTIVITY.toolbar.setNavigationOnClickListener { APP_ACTIVITY.supportFragmentManager.popBackStack() }
    }

    override fun onStart() {
        super.onStart()
        change()
    }

    open fun change() {

    }

}