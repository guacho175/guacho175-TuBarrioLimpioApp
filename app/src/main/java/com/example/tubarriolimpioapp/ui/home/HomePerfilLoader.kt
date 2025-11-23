package com.example.tubarriolimpioapp.ui.home

import android.util.Log
import kotlinx.coroutines.*
import com.example.tubarriolimpioapp.network.ApiClient
import com.example.tubarriolimpioapp.models.PerfilResponse

object HomePerfilLoader {

    fun cargarPerfil(activity: HomeActivity, token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val perfil: PerfilResponse = ApiClient.apiService.me("Bearer $token")

                withContext(Dispatchers.Main) {
                    activity.txtBienvenida.text = "Hola, ${perfil.username} 👋"
                }

            } catch (e: Exception) {
                Log.e("HOME_ERROR", "Error cargando perfil", e)
            }
        }
    }

}
