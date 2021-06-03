package app.beer.restaurant.ui.fragments.register

import android.content.Intent
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import java.lang.Exception

class AuthFragment : Fragment() {

    private val RC_SIGN_IN = 124

    private lateinit var sharedManager: SharedManager
    private lateinit var api: Api

    private lateinit var emailInput: TextInputLayout
    private lateinit var passwordInput: TextInputLayout
    private lateinit var authBtn: MaterialButton
    private lateinit var progressBar: ProgressBar

    private lateinit var gso: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var googleAuthBtn: MaterialButton

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

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        emailInput = view.findViewById(R.id.email_text_input)
        passwordInput = view.findViewById(R.id.password_text_input)
        authBtn = view.findViewById(R.id.button_auth)
        googleAuthBtn = view.findViewById(R.id.google_auth_btn)

        progressBar = view.findViewById(R.id.auth_progress_bar)

        authBtn.setOnClickListener {
            val email = emailInput.editText?.text.toString()
            val password = passwordInput.editText?.text.toString()
            if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                emailInput.error = getString(R.string.enter_correct_email_error)
                authBtn.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            } else if (password.trim().isEmpty() || password.trim().length < 5) {
                passwordInput.error = getString(R.string.enter_password_lenght_more_5_error)
                authBtn.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            } else {
                auth(email, password)
            }
        }

        googleAuthBtn.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                handleGoogleSignIn(account)
            } catch (e: ApiException) {
                e.printStackTrace()
                showToast(resources.getString(R.string.some_went_wrong))
            }
        }
    }

    private fun handleGoogleSignIn(account: GoogleSignInAccount?) {
        if (account != null) {
            auth(account.email.toString(), "", account.id.toString(), "google")
        } else {
            showToast(resources.getString(R.string.some_went_wrong))
        }
    }

    private fun auth(email: String, password: String, socialAuthId: String = "", provider: String = "web") {
        authBtn.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        api.auth(email, password, socialAuthId)
            .enqueue(RetrofitCallback<AuthResponse> { _, response ->
                val data = response.body()
                if (data != null) {
                    if (data.status.contains("Вы удачно создали аккаунт") || data.status.contains("Вы удачно вошли в свой аккаунт")) {
                        sharedManager.putBoolean(IS_AUTH_KEY, true)
                        sharedManager.putInt(USER_ID_KEY, data.id)
                        sharedManager.putString("provider", provider)
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