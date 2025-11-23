package com.example.tubarriolimpioapp.ui.home.perfil

import android.util.Log
import kotlinx.coroutines.*
import com.example.tubarriolimpioapp.data.network.ApiClient
import com.example.tubarriolimpioapp.data.model.PerfilResponse
import com.example.tubarriolimpioapp.ui.home.main.HomeActivity

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
