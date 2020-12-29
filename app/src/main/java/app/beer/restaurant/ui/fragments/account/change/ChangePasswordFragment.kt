package app.beer.restaurant.ui.fragments.account.change

import android.os.Bundle
import android.view.View
import app.beer.restaurant.R
import app.beer.restaurant.model.User
import app.beer.restaurant.ui.fragments.account.AccountFragment
import app.beer.restaurant.util.RetrofitCallback
import app.beer.restaurant.util.USER
import app.beer.restaurant.util.replaceFragment
import app.beer.restaurant.util.showToast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class ChangePasswordFragment : BaseChangeFragment(R.layout.fragment_change_password) {

    private lateinit var oldPasswordInput: TextInputLayout
    private lateinit var newPasswordInput: TextInputLayout
    private lateinit var changBtn: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        oldPasswordInput = view.findViewById(R.id.old_password_input)
        newPasswordInput = view.findViewById(R.id.new_password_input)
        changBtn = view.findViewById(R.id.change_btn)
    }

    override fun change() {
        changBtn.setOnClickListener {
            val oldPassword = oldPasswordInput.editText?.text.toString().trim()
            val newPassword = newPasswordInput.editText?.text.toString().trim()
            if (oldPassword == USER.password) {
                if (newPassword.isNotEmpty()) {
                    if (newPassword.length >= 5) {
                        USER.password = newPassword
                        api.updateUser(USER.id, USER.name, USER.password, USER.email).enqueue(RetrofitCallback<User> { _, response ->
                            if (response.isSuccessful && response.code() != 404) {
                                showToast("Вы удачно изменили пароль")
                                replaceFragment(AccountFragment(), false)
                            }
                        })
                    } else {
                        newPasswordInput.error = getString(R.string.password_small_error)
                    }
                } else {
                    newPasswordInput.error = getString(R.string.password_empty_error)
                }
            } else {
                oldPasswordInput.error = getString(R.string.password_no_matches_error)
            }
        }
    }

}