package com.example.tubarriolimpioapp.data.repository

import com.example.tubarriolimpioapp.data.model.RegistroRequest
import com.example.tubarriolimpioapp.data.model.RegistroResponse
import com.example.tubarriolimpioapp.data.network.ApiClient
import com.example.tubarriolimpioapp.data.network.ApiService

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
