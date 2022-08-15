package sean.k.common.view

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.lifecycle.*
import dagger.hilt.android.AndroidEntryPoint
import sean.k.common.R
import sean.k.common.data.LoginRepository
import sean.k.common.databinding.ViewLoginBannerBinding
import sean.k.common.login.ui.LoginActivity
import javax.inject.Inject

@AndroidEntryPoint
class LoginBannerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs), LifecycleOwner {

    @Inject
    lateinit var loginRepository: LoginRepository
    private var binding: ViewLoginBannerBinding

    // Lifecycle used to avoid memory leak when observing loggedInState
    private val lifecycleRegistry = LifecycleRegistry(this)
    override fun getLifecycle() = lifecycleRegistry

    private val loggedInState: LiveData<Boolean> = loginRepository
        .isLoggedInObservable().asLiveData(timeoutInMs = 0)


    init {
        binding = ViewLoginBannerBinding.inflate(LayoutInflater.from(context), this, true)
        loggedInState.observe(this) { isLoggedIn ->
            refreshLoginState(isLoggedIn)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    private fun refreshLoginState(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            binding.bannerText.text = resources.getString(R.string.login_banner_greeting_format, loginRepository.username)
            binding.root.setOnClickListener {
                loginRepository.logout()
            }
        } else {
            binding.bannerText.text = resources.getString(R.string.login_banner_signed_out)
            binding.root.setOnClickListener {
                context.startActivity(Intent(context, LoginActivity::class.java))
            }
        }
    }
}