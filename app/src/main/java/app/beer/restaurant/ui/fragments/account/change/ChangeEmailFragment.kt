package app.beer.restaurant.ui.fragments.account.change

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import app.beer.restaurant.R
import app.beer.restaurant.model.User
import app.beer.restaurant.ui.fragments.account.AccountFragment
import app.beer.restaurant.util.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class ChangeEmailFragment : BaseChangeFragment(R.layout.fragment_change_email) {

    private lateinit var emailInput: TextInputLayout
    private lateinit var changeBtn: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailInput = view.findViewById(R.id.email_input)
        changeBtn = view.findViewById(R.id.change_btn)
        progressBar = view.findViewById(R.id.progress_bar)
    }

    override fun change() {
        changeBtn.setOnClickListener {
            val email = emailInput.editText?.text.toString().trim()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.error = getString(R.string.email_error_text)
            } else if (email.isEmpty()) {
                emailInput.error = APP_ACTIVITY.getString(R.string.email_hint)
            }  else {
                progressBar.visibility = View.VISIBLE
                USER.email = email
                api.updateUser(USER.id, USER.name, USER.password, USER.email).enqueue(RetrofitCallback<User> { _, response ->
                        if (response.isSuccessful && response.code() != 404) {
                            showToast("Вы удачно изменили email")
                            replaceFragment(AccountFragment(), false)
                        }
                        progressBar.visibility = View.GONE
                    })
            }
        }
    }

}