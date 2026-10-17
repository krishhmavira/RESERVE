package com.example.reserve.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reserve.data.model.User
import com.example.reserve.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

sealed interface AuthEvent {
    data object LoginSuccess : AuthEvent
    data object RegisterSuccess : AuthEvent
    data object ProfileUpdateSuccess : AuthEvent
}

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userRepository = UserRepository()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    private val _forgotPasswordState = MutableStateFlow(ForgotPasswordUiState())
    val forgotPasswordState: StateFlow<ForgotPasswordUiState> = _forgotPasswordState.asStateFlow()

    private val _authEvent = MutableSharedFlow<AuthEvent>()
    val authEvent: SharedFlow<AuthEvent> = _authEvent.asSharedFlow()

    val currentUserId: String? get() = auth.currentUser?.uid
    val isLoggedIn: Boolean get() = auth.currentUser != null

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

        auth.signInWithEmailAndPassword(state.email.trim(), state.password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid != null) {
                    userRepository.getUserProfile(
                        userId = uid,
                        onSuccess = { user ->
                            _currentUser.value = user
                            _loginState.update { it.copy(isLoading = false) }
                            viewModelScope.launch { _authEvent.emit(AuthEvent.LoginSuccess) }
                        },
                        onFailure = {
                            _loginState.update { it.copy(isLoading = false) }
                            viewModelScope.launch { _authEvent.emit(AuthEvent.LoginSuccess) }
                        }
                    )
                } else {
                    _loginState.update { it.copy(isLoading = false) }
                    viewModelScope.launch { _authEvent.emit(AuthEvent.LoginSuccess) }
                }
            }
            .addOnFailureListener { exception ->
                _loginState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = mapFirebaseError(exception)
                    )
                }
            }
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

        auth.createUserWithEmailAndPassword(state.email.trim(), state.password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                val user = User(
                    userId = uid,
                    name = state.name.trim(),
                    email = state.email.trim(),
                    phone = state.phone.trim(),
                    role = state.selectedRole
                )
                userRepository.createUserProfile(
                    user = user,
                    onSuccess = {
                        _currentUser.value = user
                        _registerState.update { it.copy(isLoading = false) }
                        viewModelScope.launch { _authEvent.emit(AuthEvent.RegisterSuccess) }
                    },
                    onFailure = { exception ->
                        _registerState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Account created but profile setup failed. Please try logging in."
                            )
                        }
                    }
                )
            }
            .addOnFailureListener { exception ->
                _registerState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = mapFirebaseError(exception)
                    )
                }
            }
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

        auth.sendPasswordResetEmail(state.email.trim())
            .addOnSuccessListener {
                _forgotPasswordState.update { it.copy(isLoading = false, isSuccess = true) }
            }
            .addOnFailureListener { exception ->
                _forgotPasswordState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = mapFirebaseError(exception)
                    )
                }
            }
    }

    fun clearForgotPasswordState() {
        _forgotPasswordState.update { ForgotPasswordUiState() }
    }

    // Profile

    fun updateProfile(
        name: String,
        phone: String,
        address: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: run {
            onFailure("Not authenticated")
            return
        }
        val fields = mapOf(
            "name" to name.trim(),
            "phone" to phone.trim(),
            "address" to address.trim()
        )
        userRepository.updateUserProfile(
            userId = uid,
            fields = fields,
            onSuccess = {
                _currentUser.update { user ->
                    user?.copy(
                        name = name.trim(),
                        phone = phone.trim(),
                        address = address.trim()
                    )
                }
                viewModelScope.launch { _authEvent.emit(AuthEvent.ProfileUpdateSuccess) }
                onSuccess()
            },
            onFailure = { exception ->
                onFailure(exception.message ?: "Failed to update profile")
            }
        )
    }

    fun refreshCurrentUser() {
        val uid = auth.currentUser?.uid ?: return
        userRepository.getUserProfile(
            userId = uid,
            onSuccess = { user -> _currentUser.value = user },
            onFailure = { }
        )
    }

    // Logout

    fun logout() {
        auth.signOut()
        _currentUser.value = null
    }

    // Helpers

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun mapFirebaseError(exception: Exception): String {
        return when {
            exception.message?.contains("no user record", ignoreCase = true) == true ->
                "No account found with this email"
            exception.message?.contains("password is invalid", ignoreCase = true) == true ->
                "Incorrect password"
            exception.message?.contains("email address is badly formatted", ignoreCase = true) == true ->
                "Invalid email format"
            exception.message?.contains("email address is already in use", ignoreCase = true) == true ->
                "An account with this email already exists"
            exception.message?.contains("network error", ignoreCase = true) == true ->
                "Network error. Please check your connection."
            exception.message?.contains("too many unsuccessful", ignoreCase = true) == true ->
                "Too many attempts. Please try again later."
            else -> exception.message ?: "An unexpected error occurred"
        }
    }
}
