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

    fun onUserIdChange(newId: String) {
        _uiState.update { it.copy(userId = newId, error = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, error = null) }
    }

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value
        
        // Validación: Campos vacíos
        if (state.userId.isBlank()) {
            _uiState.update { it.copy(error = "Ingresa tu usuario") }
            return
        }
        if (state.password.isBlank()) {
            _uiState.update { it.copy(error = "Ingresa tu contraseña") }
            return
        }

        // Lógica de validación contra "Base de Datos"
        val user = UserDatabase.findUser(state.userId, state.password)
        
        if (user != null) {
            onSuccess()
        } else {
            // Verificamos si es que el usuario existe pero la contraseña está mal, 
            // o si el usuario ni siquiera existe.
            if (UserDatabase.exists(state.userId)) {
                _uiState.update { it.copy(error = "Contraseña incorrecta") }
            } else {
                _uiState.update { it.copy(error = "El usuario no existe. Regístrate primero.") }
            }
        }
    }
}

data class LoginUiState(
    val userId: String = "",
    val password: String = "",
    val error: String? = null
)
