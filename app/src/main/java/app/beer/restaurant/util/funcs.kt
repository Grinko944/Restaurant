package app.beer.restaurant.util

import android.content.Intent
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import app.beer.restaurant.R
import app.beer.restaurant.api.App
import app.beer.restaurant.model.User
import app.beer.restaurant.model.chat.Message
import app.beer.restaurant.model.product.Product
import app.beer.restaurant.ui.activities.MainActivity
import com.google.firebase.database.DataSnapshot
import com.squareup.picasso.Picasso

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
