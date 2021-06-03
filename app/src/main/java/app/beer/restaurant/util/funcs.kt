package app.beer.restaurant.util

import android.content.Intent
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import app.beer.restaurant.R
import app.beer.restaurant.api.App
import app.beer.restaurant.model.User
import app.beer.restaurant.model.basket.Basket
import app.beer.restaurant.model.chat.Message
import app.beer.restaurant.model.product.Product
import app.beer.restaurant.ui.activities.MainActivity
import com.google.firebase.database.DataSnapshot
import com.squareup.picasso.Picasso
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

lateinit var APP_ACTIVITY: MainActivity
lateinit var APP: App

lateinit var USER: User

const val IS_AUTH_KEY = "is_auth"
const val USER_ID_KEY = "user_id"

const val LANGUAGE_KEY = "language"
const val LANGUAGE_RUS = "Русский"
const val LANGUAGE_ENG = "English"
const val LANGUAGE_DOT = "Deutsche"
const val LANGUAGE_BOL = "български"

fun ImageView.loadAndSetImage(url: String) {
    if (url != "") {
        Picasso.get()
            .load(url)
            .fit()
            .centerCrop()
            .into(this)
    }
}

fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
    if (addToBackStack) {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.data_container, fragment)
            .commit()
    } else {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .replace(R.id.data_container, fragment)
            .commit()
    }
}

fun restartActivity() {
    val intent = Intent(APP_ACTIVITY, MainActivity::class.java)
    APP_ACTIVITY.finish()
    APP_ACTIVITY.startActivity(intent)
}

fun showToast(message: String) {
    Toast.makeText(APP_ACTIVITY, message, Toast.LENGTH_SHORT).show()
}

fun DataSnapshot.getMessageModel(): Message = this.getValue(Message::class.java) ?: Message()

fun getPriceForBasket(product: Basket): BigDecimal {
    return when {
        APP_ACTIVITY.sharedManager.getString(LANGUAGE_KEY) == LANGUAGE_ENG -> {
            product.price_USD.toBigDecimal()
        }
        APP_ACTIVITY.sharedManager.getString(LANGUAGE_KEY) == LANGUAGE_RUS -> {
            product.price.toBigDecimal()
        }
        APP_ACTIVITY.sharedManager.getString(LANGUAGE_KEY) == LANGUAGE_DOT -> {
            product.price_EURO.toBigDecimal()
        }
        else -> {
            product.price_USD.toBigDecimal()
        }
    }
}

fun getLanguageCode(sharedManager: SharedManager) = when (sharedManager.getString(LANGUAGE_KEY)) {
    LANGUAGE_RUS -> "ru"
    LANGUAGE_ENG -> "en"
    LANGUAGE_DOT -> "de"
    LANGUAGE_BOL -> "bg"
    else -> "en"
}

fun String.timestampToDate() = this.substring(0, 10).reversed().replace("-", ".")

fun Long.getDateTime(): String {
     return try {
         val sdf = SimpleDateFormat("MM.dd.yyyy", Locale.getDefault())
         val netDate = Date(this * 1000)
         sdf.format(netDate)
     } catch (e: Exception) {
         e.toString()
     }
}
