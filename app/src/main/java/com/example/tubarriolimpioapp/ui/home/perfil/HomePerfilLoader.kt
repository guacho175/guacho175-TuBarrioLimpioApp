package com.example.tubarriolimpioapp.ui.home.perfil

import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.tubarriolimpioapp.R
import com.example.tubarriolimpioapp.data.model.PerfilResponse
import com.example.tubarriolimpioapp.data.network.ApiClient
import com.example.tubarriolimpioapp.ui.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object HomePerfilLoader {

    private const val TAG = "HOME_PERFIL"

    fun cargarPerfil(activity: HomeActivity, token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val perfil: PerfilResponse = ApiClient.apiService.me("Bearer $token")

                withContext(Dispatchers.Main) {
                    val txtBienvenida =
                        activity.findViewById<TextView>(R.id.txtBienvenida)

                    // Mensaje genérico (así evitamos errores de campos que no existen)
                    txtBienvenida.text = "¡Bienvenido a Tu Barrio Limpio!"
                    // Si quieres usar un campo concreto, cambia por algo como:
                    // txtBienvenida.text = "Hola, ${perfil.username}"
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error cargando perfil", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        activity,
                        "Error cargando perfil",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
