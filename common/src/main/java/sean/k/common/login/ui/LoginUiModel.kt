package sean.k.common.login.ui

data class LoginUiModel(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)