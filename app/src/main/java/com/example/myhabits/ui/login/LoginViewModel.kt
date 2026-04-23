package com.example.myhabits.ui.login

import androidx.lifecycle.ViewModel
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
        
        // Lógica de "Read" simple: Validar que no estén vacíos
        if (state.userId.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Campos obligatorios") }
            return
        }

        // Simulación de validación exitosa
        onSuccess()
    }
}

data class LoginUiState(
    val userId: String = "",
    val password: String = "",
    val error: String? = null
)
