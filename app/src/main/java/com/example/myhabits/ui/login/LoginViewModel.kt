package com.example.myhabits.ui.login

import androidx.lifecycle.ViewModel
import com.example.myhabits.data.UserDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, error = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, error = null) }
    }

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value
        
        // Validación: Campos vacíos
        if (state.email.isBlank()) {
            _uiState.update { it.copy(error = "Ingresa tu correo electrónico") }
            return
        }
        if (state.password.isBlank()) {
            _uiState.update { it.copy(error = "Ingresa tu contraseña") }
            return
        }

        // Lógica de validación contra "Base de Datos" usando email
        val user = UserDatabase.findUser(state.email, state.password)
        
        if (user != null) {
            onSuccess()
        } else {
            if (UserDatabase.exists(state.email)) {
                _uiState.update { it.copy(error = "Contraseña incorrecta") }
            } else {
                _uiState.update { it.copy(error = "El correo no está registrado.") }
            }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val error: String? = null
)
