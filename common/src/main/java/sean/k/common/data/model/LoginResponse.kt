package sean.k.common.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoginResponse(
    val userName: String,
    val sessionToken: String
)