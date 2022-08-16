package sean.k.account

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import dagger.hilt.android.AndroidEntryPoint
import sean.k.account.databinding.ActivityAccountBinding
import sean.k.common.data.LoginRepository
import sean.k.common.login.ui.LoginActivity
import javax.inject.Inject

@AndroidEntryPoint
class AccountActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding

    @Inject
    lateinit var loginRepository: LoginRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        loginRepository.isLoggedInObservable().asLiveData().observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                binding.greeting.text = getString(R.string.account_greeting_format, loginRepository.username)
                binding.lastLogin.text = getString(R.string.account_last_login_format, loginRepository.lastLoginDateFormatted)
                binding.email.visibility = View.VISIBLE
                binding.lastLogin.visibility = View.VISIBLE
                binding.login.text = getString(R.string.account_logout_button)
                binding.login.setOnClickListener {
                    loginRepository.logout()
                }
            } else {
                binding.greeting.text = getString(R.string.account_greeting_not_logged_in)
                binding.email.visibility = View.GONE
                binding.lastLogin.visibility = View.GONE
                binding.login.visibility = View.VISIBLE
                binding.login.text = getString(R.string.account_login_button)
                binding.login.setOnClickListener {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
        }
    }
}