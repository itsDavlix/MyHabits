package com.example.myhabits.ui.login

import androidx.lifecycle.ViewModel
import com.example.myhabits.data.User
import com.example.myhabits.data.UserDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegistrationViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    fun onNameChange(newName: String) {
        // Asegura que la primera letra siempre sea mayúscula
        val formattedName = if (newName.isNotEmpty()) {
            newName.replaceFirstChar { it.uppercase() }
        } else {
            newName
        }
        _uiState.update { it.copy(name = formattedName, nameError = null) }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, emailError = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, passwordError = null) }
    }

    fun validateEmailOnBlur() {
        val email = _uiState.value.email
        if (email.isNotEmpty() && !isValidEmail(email)) {
            _uiState.update { it.copy(emailError = "Formato de correo inválido") }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun registerUser(onSuccess: () -> Unit) {
        val state = _uiState.value
        var hasError = false

        // Validación: Campos vacíos
        if (state.name.isBlank()) {
            _uiState.update { it.copy(nameError = "El nombre es obligatorio") }
            hasError = true
        }

        if (state.email.isBlank()) {
            _uiState.update { it.copy(emailError = "El correo es obligatorio") }
            hasError = true
        } else if (!isValidEmail(state.email)) {
            _uiState.update { it.copy(emailError = "Formato de correo inválido") }
            hasError = true
        } else if (UserDatabase.exists(state.email)) {
            _uiState.update { it.copy(emailError = "Este correo ya está registrado") }
            hasError = true
        }

        if (state.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "La contraseña es obligatoria") }
            hasError = true
        } else if (state.password.length < 6) {
            _uiState.update { it.copy(passwordError = "Mínimo 6 caracteres") }
            hasError = true
        }

        if (!hasError) {
            val newUser = User(state.name, state.email, state.password)
            UserDatabase.addUser(newUser)
            onSuccess()
        }
    }
}

data class RegistrationUiState(
    val name: String = "",
    val nameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null
)
