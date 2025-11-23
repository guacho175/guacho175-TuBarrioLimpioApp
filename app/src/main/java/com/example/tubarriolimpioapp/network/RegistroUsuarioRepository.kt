package com.example.tubarriolimpioapp.network

import com.example.tubarriolimpioapp.models.RegistroRequest
import com.example.tubarriolimpioapp.models.RegistroResponse

class RegistroUsuarioRepository(
    private val api: ApiService = ApiClient.apiService
) {

    suspend fun registrarUsuario(
        username: String,
        email: String,
        password: String
    ): Result<RegistroResponse> {
        return try {
            // Llamada directa: ApiService devuelve un RegistroResponse
            val usuario = api.registrarUsuario(
                RegistroRequest(
                    username = username,
                    email = email,
                    password = password
                )
            )
            // Si llegamos aquí sin excepción, consideramos éxito
            Result.success(usuario)
        } catch (e: Exception) {
            // Cualquier error de red/servidor se captura aquí
            Result.failure(e)
        }
    }
}
