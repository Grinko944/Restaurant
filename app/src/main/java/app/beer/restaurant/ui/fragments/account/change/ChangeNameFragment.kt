package app.beer.restaurant.ui.fragments.account.change

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import app.beer.restaurant.R
import app.beer.restaurant.model.User
import app.beer.restaurant.ui.fragments.account.AccountFragment
import app.beer.restaurant.util.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class ChangeNameFragment : BaseChangeFragment(R.layout.fragment_change_name) {

    private lateinit var nameInput: TextInputLayout
    private lateinit var changeBtn: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameInput = view.findViewById(R.id.name_input)
        changeBtn = view.findViewById(R.id.change_btn)
        progressBar = view.findViewById(R.id.progress_bar)
    }

    override fun change() {
        changeBtn.setOnClickListener {
            if (nameInput.editText?.text.toString().trim().isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                USER.name = nameInput.editText?.text.toString()
                api.updateUser(USER.id, USER.name, USER.password, USER.email).enqueue(RetrofitCallback<User> { _, response ->
                    if (response.isSuccessful && response.code() != 404) {
                        showToast("Вы удачно изменили имя")
                        replaceFragment(AccountFragment(), false)
                    }
                    progressBar.visibility = View.GONE
                })
            } else {
                nameInput.error = getString(R.string.name_error_label)
            }
        }
    }

}