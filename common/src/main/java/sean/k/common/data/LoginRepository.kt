package sean.k.common.data

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import sean.k.common.date.Clock
import sean.k.login.data.LoginApi
import java.text.SimpleDateFormat
import java.util.*


interface LoginRepository {
    val username: String?
    val sessionToken: String?
    val lastLoginTime: Long?

    val lastLoginDateFormatted: String?
        get() = lastLoginTime?.let { dateFormat.format(Date(it)) }

    val isLoggedIn: Boolean
        get() = sessionToken != null

    fun isLoggedInObservable(): Flow<Boolean>

    fun logout()

    fun login(username: String, password: String): Flow<Boolean>

    companion object {
        private val dateFormat = SimpleDateFormat.getDateTimeInstance()
    }
}

class LoginRepositoryImpl(context: Context, private val api: LoginApi, private val clock: Clock) :
    LoginRepository {

    private val datastore = Datastore(context)
    override val username: String?
        get() = datastore.username
    override val sessionToken: String?
        get() = datastore.sessionToken
    override val lastLoginTime: Long?
        get() = datastore.loginDateTimestamp.let {
            if (it == 0L) {
                null
            } else {
                it
            }
        }

    /** Must be updated whenever login state changes. **/
    private val _isLoggedInObservable: MutableStateFlow<Boolean> = MutableStateFlow(isLoggedIn)

    override fun isLoggedInObservable(): Flow<Boolean> {
        return this._isLoggedInObservable
    }

    override fun logout() {
        datastore.sessionToken = null
        datastore.username = null
        datastore.loginDateTimestamp = 0L
        _isLoggedInObservable.value = false
    }

    override fun login(username: String, password: String): Flow<Boolean> {
        return flow {
            when (val result = api.login(username, password)) {
                is Result.Error -> {
                    //TODO
                    emit(false)
                }
                is Result.Success -> {
                    datastore.username = result.data.userName
                    datastore.sessionToken = result.data.sessionToken
                    datastore.loginDateTimestamp = clock.currentTimeMillis
                    _isLoggedInObservable.value = true
                    emit(true)
                }
            }

            //TODO: testable DispatcherProvider interface
        }.flowOn(Dispatchers.IO)
    }

    internal class Datastore(context: Context) {

        companion object {

            private const val PREFERENCES_NAME = "sean.k.common.login_preferences"
            private const val KEY_DATASTORE_VERSION = "key_datastore_version"

            private const val KEY_USERNAME = "key_username"
            private const val KEY_SESSION_TOKEN = "key_session_token"
            private const val KEY_LOGIN_TIME = "key_login_time"
        }

        private val prefs: SharedPreferences = context.getSharedPreferences(
            PREFERENCES_NAME,
            Activity.MODE_PRIVATE
        )

        private fun getEditor(): SharedPreferences.Editor {
            return prefs.edit()
        }

        var sessionToken: String?
            get() = prefs.getString(KEY_SESSION_TOKEN, null)
            set(value) = getEditor().putString(KEY_SESSION_TOKEN, value).apply()

        var username: String?
            get() = prefs.getString(KEY_USERNAME, null)
            set(value) = getEditor().putString(KEY_USERNAME, value).apply()

        var loginDateTimestamp: Long
            get() = prefs.getLong(KEY_LOGIN_TIME, 0)
            set(value) = getEditor().putLong(KEY_LOGIN_TIME, value).apply()
    }
}