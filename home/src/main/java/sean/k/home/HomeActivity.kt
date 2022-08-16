package sean.k.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.asLiveData
import dagger.hilt.android.AndroidEntryPoint
import sean.k.common.data.LoginRepository
import sean.k.home.databinding.ActivityHomeBinding
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    @Inject
    lateinit var loginRepository: LoginRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        loginRepository.isLoggedInObservable().asLiveData(timeoutInMs = 0).observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                binding.message.text = getString(R.string.home_message_signed_in)
            } else {
                binding.message.text = getString(R.string.home_message_not_signed_in)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}