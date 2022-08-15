package sean.k.login.data

import kotlinx.coroutines.delay
import sean.k.common.data.Result
import sean.k.common.data.model.LoginResponse
import java.util.*

interface LoginApi {
    suspend fun login(username: String, password: String): Result<LoginResponse>
}

class MockLoginApi : LoginApi {
    override suspend fun login(username: String, password: String): Result<LoginResponse> {
        delay(1000L)
        return Result.Success(LoginResponse(username, UUID.randomUUID().toString()))
    }
}