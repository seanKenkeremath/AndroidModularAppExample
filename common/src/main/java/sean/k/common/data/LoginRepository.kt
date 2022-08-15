package sean.k.common.data

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import sean.k.login.data.LoginApi


interface LoginRepository {
    val username: String?
    val sessionToken: String?

    val isLoggedIn: Boolean
        get() = sessionToken != null
    fun isLoggedInObservable(): Flow<Boolean>

    fun logout()

    fun login(username: String, password: String): Flow<Boolean>
}

class LoginRepositoryImpl(context: Context, private val api: LoginApi):
    LoginRepository {

    private val datastore = Datastore(context)
    override val username: String?
        get() = datastore.username
    override val sessionToken: String?
        get() = datastore.sessionToken

    /** Must be updated whenever login state changes. **/
    private val _isLoggedInObservable: MutableStateFlow<Boolean> = MutableStateFlow(isLoggedIn)

    override fun isLoggedInObservable(): Flow<Boolean> {
        return this._isLoggedInObservable
    }

    override fun logout() {
        datastore.sessionToken = null
        datastore.username = null
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
    }
}