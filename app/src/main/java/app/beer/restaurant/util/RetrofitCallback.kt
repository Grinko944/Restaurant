package app.beer.restaurant.util

import android.util.Log
import app.beer.restaurant.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val RETROFIT_ERROR_KEY = "retrofit_error"

class RetrofitCallback<T>(val onSuccess: (call: Call<T>, response: Response<T>) -> Unit) : Callback<T> {
    override fun onFailure(call: Call<T>, t: Throwable) {
        Log.d(RETROFIT_ERROR_KEY, "${t.message}")
        showToast(APP_ACTIVITY.getString(R.string.some_went_wrong))
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful && response.code() != 404) {
            if (response.body() != null) {
                onSuccess(call, response)
            }
        }
    }

}