package app.beer.restaurant.ui.fragments.account

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import app.beer.restaurant.R
import app.beer.restaurant.databinding.FragmentAccountBinding
import app.beer.restaurant.ui.fragments.account.change.ChangeEmailFragment
import app.beer.restaurant.ui.fragments.account.change.ChangeNameFragment
import app.beer.restaurant.ui.fragments.account.change.ChangePasswordFragment
import app.beer.restaurant.ui.fragments.account.orders.OrdersFragment
import app.beer.restaurant.util.*
import java.util.*

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding

    private lateinit var accountUserNameChangeBtn: LinearLayout
    private lateinit var accountEmailChangeBtn: LinearLayout
    private lateinit var accountPasswordChangeBtn: LinearLayout
    private lateinit var changeLanguageBtn: LinearLayout
    private lateinit var ordersBtn: LinearLayout

    private lateinit var appLanguageText: TextView

    private lateinit var changeThemeSwitch: Switch
    private lateinit var appThemeText: TextView

    private lateinit var sharedManager: SharedManager

    private var isNightMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_account, container, false)
        binding.user = USER
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedManager = SharedManager()
        changeThemeSwitch = view.findViewById(R.id.night_mode_switch)
        appThemeText = view.findViewById(R.id.app_theme_text)
        isNightMode = sharedManager.getBoolean("is_night_mode")

        changeThemeSwitch.isChecked = isNightMode
        appThemeText.text =
            if (isNightMode) getString(R.string.enable_light_theme) else getString(R.string.enable_night_theme)

        changeThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            isNightMode = if (isChecked) {
                APP_ACTIVITY.changeTheme(AppCompatDelegate.MODE_NIGHT_YES)
                true
            } else {
                APP_ACTIVITY.changeTheme(AppCompatDelegate.MODE_NIGHT_NO)
                false
            }
            APP_ACTIVITY.bottomNavigationView.selectedItemId = R.id.nav_home
            sharedManager.putBoolean("is_night_mode", isNightMode)
        }

        accountUserNameChangeBtn = view.findViewById(R.id.account_user_name_change_btn)
        accountEmailChangeBtn = view.findViewById(R.id.account_email_change_btn)
        accountPasswordChangeBtn = view.findViewById(R.id.account_password_change_btn)
        changeLanguageBtn = view.findViewById(R.id.change_language_btn)
        appLanguageText = view.findViewById(R.id.app_language_text)
        ordersBtn = view.findViewById(R.id.orders_btn)

        appLanguageText.text =
            getString(R.string.change_language_label, sharedManager.getString(LANGUAGE_KEY))

        accountUserNameChangeBtn.setOnClickListener {
            replaceFragment(ChangeNameFragment())
        }
        accountEmailChangeBtn.setOnClickListener {
            replaceFragment(ChangeEmailFragment())
        }
        accountPasswordChangeBtn.setOnClickListener {
            replaceFragment(ChangePasswordFragment())
        }
        changeLanguageBtn.setOnClickListener {
            showListDialog()
        }

        ordersBtn.setOnClickListener {
            replaceFragment(OrdersFragment(), true)
        }
    }

    private fun showListDialog() {
        val builder = AlertDialog.Builder(APP_ACTIVITY)

        val adapter = ArrayAdapter<String>(
            APP_ACTIVITY, android.R.layout.select_dialog_singlechoice,
            resources.getStringArray(R.array.languages)
        )

        builder.setTitle(getString(R.string.change_language_text))
            .setNegativeButton("cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setAdapter(adapter) { dialog, which ->
                val language = adapter.getItem(which)
                if (language != null) {
                    sharedManager.putString(LANGUAGE_KEY, language)
                    appLanguageText.text = getString(
                        R.string.change_language_label,
                        sharedManager.getString(LANGUAGE_KEY)
                    )

                    val localStr =
                        when (language) {
                            LANGUAGE_RUS -> "ru"
                            LANGUAGE_ENG -> "en"
                            LANGUAGE_DOT -> "de"
                            LANGUAGE_BOL -> "bg"
                            else -> "en"
                        }

                    val locale = Locale(localStr)
                    Locale.setDefault(locale)
                    val configuration = Configuration()
                    configuration.locale = locale
                    resources.updateConfiguration(configuration, null)
                    restartActivity()
                }
                showToast(getString(R.string.change_language_success_label))
            }
        builder.show()
    }

}