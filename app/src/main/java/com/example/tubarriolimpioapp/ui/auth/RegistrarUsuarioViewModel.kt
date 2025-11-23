package com.example.tubarriolimpioapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubarriolimpioapp.data.repository.RegistroUsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistroUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val password2: String = "",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class RegistrarUsuarioViewModel(
    private val repository: RegistroUsuarioRepository = RegistroUsuarioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun onPassword2Change(value: String) {
        _uiState.update { it.copy(password2 = value) }
    }

    fun registrarUsuario() {
        val state = _uiState.value

        // 🔎 Validaciones básicas
        if (state.username.isBlank() ||
            state.email.isBlank() ||
            state.password.isBlank() ||
            state.password2.isBlank()
        ) {
            _uiState.update {
                it.copy(errorMessage = "Completa todos los campos", successMessage = null)
            }
            return
        }

        if (state.password != state.password2) {
            _uiState.update {
                it.copy(errorMessage = "Las contraseñas no coinciden", successMessage = null)
            }
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )
        }

        viewModelScope.launch {
            val result = repository.registrarUsuario(
                username = state.username,
                email = state.email,
                password = state.password
            )

            result
                .onSuccess { _ ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Cuenta creada correctamente. Ya puedes iniciar sesión.",
                            errorMessage = null
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Error al registrar usuario",
                            successMessage = null
                        )
                    }
                }
        }
    }
}
