package com.example.myhabits.ui.login

import androidx.lifecycle.ViewModel
import com.example.myhabits.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegistrationViewModel : ViewModel() {
    
    // Mock de base de datos de usuarios (CRUD: Create/Read)
    private val _registeredUsers = MutableStateFlow<List<User>>(emptyList())
    
    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName, nameError = null) }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, emailError = null) }
    }

    fun onAliasChange(newAlias: String) {
        _uiState.update { it.copy(alias = newAlias, aliasError = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, passwordError = null) }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isFirstLetterUppercase(text: String): Boolean {
        return text.isNotEmpty() && text[0].isUpperCase()
    }

    fun registerUser(onSuccess: () -> Unit) {
        val state = _uiState.value
        var hasError = false

        // Validaciones
        if (!isFirstLetterUppercase(state.name)) {
            _uiState.update { it.copy(nameError = "La primera letra debe ser mayúscula") }
            hasError = true
        }

        if (!isValidEmail(state.email)) {
            _uiState.update { it.copy(emailError = "Formato de correo inválido") }
            hasError = true
        }

        if (state.alias.isBlank()) {
            _uiState.update { it.copy(aliasError = "El alias no puede estar vacío") }
            hasError = true
        }

        if (state.password.length < 6) {
            _uiState.update { it.copy(passwordError = "Mínimo 6 caracteres") }
            hasError = true
        }

        if (!hasError) {
            val newUser = User(state.name, state.email, state.alias, state.password)
            _registeredUsers.update { it + newUser }
            onSuccess()
        }
    }
}

data class RegistrationUiState(
    val name: String = "",
    val nameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val alias: String = "",
    val aliasError: String? = null,
    val password: String = "",
    val passwordError: String? = null
)
