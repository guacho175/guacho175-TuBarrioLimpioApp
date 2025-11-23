package com.example.tubarriolimpioapp.data.network

import com.example.tubarriolimpioapp.data.model.DenunciaResponse
import com.example.tubarriolimpioapp.data.model.LoginResponse
import com.example.tubarriolimpioapp.data.model.NotificacionResponse
import com.example.tubarriolimpioapp.data.model.PerfilResponse
import com.example.tubarriolimpioapp.data.model.RegistroRequest
import com.example.tubarriolimpioapp.data.model.RegistroResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    // LOGIN
    @POST("usuarios/login/")
    suspend fun login(@Body body: Map<String, String>): LoginResponse

    // REGISTRO
    @POST("usuarios/registro/")
    suspend fun registro(@Body body: Map<String, String>): RegistroResponse

    // PERFIL
    @GET("usuarios/me/")
    suspend fun me(@Header("Authorization") token: String): PerfilResponse

    // CREAR DENUNCIA
    @Multipart
    @POST("denuncias/")
    suspend fun crearDenuncia(
        @Header("Authorization") token: String,
        @Part("descripcion") descripcion: RequestBody,
        @Part("direccion_textual") direccionTextual: RequestBody,
        @Part("latitud") latitud: RequestBody,
        @Part("longitud") longitud: RequestBody,
        @Part imagen: MultipartBody.Part
    ): DenunciaResponse

    // 🚨 USA SOLO ESTA
    @GET("denuncias/mis/")
    suspend fun obtenerMisDenuncias(
        @Header("Authorization") token: String
    ): List<DenunciaResponse>

    // NOTIFICACIONES
    @GET("denuncias/notificaciones/")
    suspend fun obtenerNotificaciones(
        @Header("Authorization") token: String
    ): List<NotificacionResponse>

    // MARCAR LEÍDA
    @PATCH("denuncias/notificaciones/{id}/")
    suspend fun marcarNotificacionLeida(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body body: Map<String, Boolean>
    ): NotificacionResponse




    // 👉 NUEVO REGISTRO para el módulo con ViewModel
    @POST("usuarios/registro/")          // <-- aquí debe ir la ruta que existe en Django
    suspend fun registrarUsuario(
        @Body body: RegistroRequest
    ): RegistroResponse
}
