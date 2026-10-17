package com.example.reserve.ui.screens.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val selectedRole: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val roleError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    private val _forgotPasswordState = MutableStateFlow(ForgotPasswordUiState())
    val forgotPasswordState: StateFlow<ForgotPasswordUiState> = _forgotPasswordState.asStateFlow()

    // Login

    fun updateLoginEmail(email: String) {
        _loginState.update { it.copy(email = email, emailError = null, errorMessage = null) }
    }

    fun updateLoginPassword(password: String) {
        _loginState.update { it.copy(password = password, passwordError = null, errorMessage = null) }
    }

    fun login() {
        val state = _loginState.value
        var hasError = false

        if (state.email.isBlank()) {
            _loginState.update { it.copy(emailError = "Email is required") }
            hasError = true
        } else if (!isValidEmail(state.email)) {
            _loginState.update { it.copy(emailError = "Enter a valid email") }
            hasError = true
        }

        if (state.password.isBlank()) {
            _loginState.update { it.copy(passwordError = "Password is required") }
            hasError = true
        } else if (state.password.length < 6) {
            _loginState.update { it.copy(passwordError = "Password must be at least 6 characters") }
            hasError = true
        }

        if (hasError) return

        _loginState.update { it.copy(isLoading = true, errorMessage = null) }
        // Firebase login will be added in Commit 2
    }

    fun clearLoginError() {
        _loginState.update { it.copy(errorMessage = null) }
    }

    // Register

    fun updateRegisterName(name: String) {
        _registerState.update { it.copy(name = name, nameError = null, errorMessage = null) }
    }

    fun updateRegisterEmail(email: String) {
        _registerState.update { it.copy(email = email, emailError = null, errorMessage = null) }
    }

    fun updateRegisterPhone(phone: String) {
        _registerState.update { it.copy(phone = phone, phoneError = null, errorMessage = null) }
    }

    fun updateRegisterPassword(password: String) {
        _registerState.update { it.copy(password = password, passwordError = null, errorMessage = null) }
    }

    fun updateRegisterConfirmPassword(confirmPassword: String) {
        _registerState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null, errorMessage = null) }
    }

    fun updateRegisterRole(role: String) {
        _registerState.update { it.copy(selectedRole = role, roleError = null, errorMessage = null) }
    }

    fun register() {
        val state = _registerState.value
        var hasError = false

        if (state.name.isBlank()) {
            _registerState.update { it.copy(nameError = "Name is required") }
            hasError = true
        }

        if (state.email.isBlank()) {
            _registerState.update { it.copy(emailError = "Email is required") }
            hasError = true
        } else if (!isValidEmail(state.email)) {
            _registerState.update { it.copy(emailError = "Enter a valid email") }
            hasError = true
        }

        if (state.phone.isBlank()) {
            _registerState.update { it.copy(phoneError = "Phone number is required") }
            hasError = true
        }

        if (state.password.isBlank()) {
            _registerState.update { it.copy(passwordError = "Password is required") }
            hasError = true
        } else if (state.password.length < 6) {
            _registerState.update { it.copy(passwordError = "Password must be at least 6 characters") }
            hasError = true
        }

        if (state.confirmPassword.isBlank()) {
            _registerState.update { it.copy(confirmPasswordError = "Please confirm your password") }
            hasError = true
        } else if (state.password != state.confirmPassword) {
            _registerState.update { it.copy(confirmPasswordError = "Passwords do not match") }
            hasError = true
        }

        if (state.selectedRole.isBlank()) {
            _registerState.update { it.copy(roleError = "Please select a role") }
            hasError = true
        }

        if (hasError) return

        _registerState.update { it.copy(isLoading = true, errorMessage = null) }
        // Firebase registration will be added in Commit 2
    }

    fun clearRegisterError() {
        _registerState.update { it.copy(errorMessage = null) }
    }

    // Forgot Password

    fun updateForgotEmail(email: String) {
        _forgotPasswordState.update { it.copy(email = email, emailError = null, errorMessage = null) }
    }

    fun sendPasswordReset() {
        val state = _forgotPasswordState.value

        if (state.email.isBlank()) {
            _forgotPasswordState.update { it.copy(emailError = "Email is required") }
            return
        }
        if (!isValidEmail(state.email)) {
            _forgotPasswordState.update { it.copy(emailError = "Enter a valid email") }
            return
        }

        _forgotPasswordState.update { it.copy(isLoading = true, errorMessage = null) }
        // Firebase password reset will be added in Commit 2
    }

    fun clearForgotPasswordState() {
        _forgotPasswordState.update { ForgotPasswordUiState() }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
