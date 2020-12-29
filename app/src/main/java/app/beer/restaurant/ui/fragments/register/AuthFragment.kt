package app.beer.restaurant.ui.fragments.register

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import app.beer.restaurant.R
import app.beer.restaurant.api.Api
import app.beer.restaurant.model.auth.AuthResponse
import app.beer.restaurant.ui.fragments.main.MainFragment
import app.beer.restaurant.util.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class AuthFragment : Fragment() {

    private lateinit var sharedManager: SharedManager
    private lateinit var api: Api

    private lateinit var emailInput: TextInputLayout
    private lateinit var passwordInput: TextInputLayout
    private lateinit var authBtn: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedManager = SharedManager()
        api = APP.getApi()

        emailInput = view.findViewById(R.id.email_text_input)
        passwordInput = view.findViewById(R.id.password_text_input)
        authBtn = view.findViewById(R.id.button_auth)

        progressBar = view.findViewById(R.id.auth_progress_bar)

        authBtn.setOnClickListener {
            auth(emailInput.editText?.text.toString(), passwordInput.editText?.text.toString())
        }
    }

    private fun auth(email: String, password: String) {
        authBtn.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            emailInput.error = getString(R.string.enter_correct_email_error)
            authBtn.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        } else if (password.trim().isEmpty() || password.trim().length < 5) {
            passwordInput.error = getString(R.string.enter_password_lenght_more_5_error)
            authBtn.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        } else {
            api.auth(email, password).enqueue(RetrofitCallback<AuthResponse> { _, response ->
                val data = response.body()
                if (data != null) {
                    if (data.status.contains("Вы удачно создали аккаунт") || data.status.contains("Вы удачно вошли в свой аккаунт")) {
                        sharedManager.putBoolean(IS_AUTH_KEY, true)
                        sharedManager.putInt(USER_ID_KEY, data.id)
                        replaceFragment(MainFragment(), false)
                        showToast(data.status)
                        restartActivity()
                        authBtn.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                    } else if (data.status.contains("Введите правельный пароль от вашего аккаунта")) {
                        passwordInput.error = getString(R.string.enter_correct_password_error)
                        authBtn.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                    }
                }
            })
        }
    }

}