package com.example.tubarriolimpioapp.ui.home.denuncias

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.tubarriolimpioapp.R
import com.example.tubarriolimpioapp.data.model.DenunciaResponse
import com.example.tubarriolimpioapp.data.network.ApiClient
import com.example.tubarriolimpioapp.ui.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object HomeDenunciasLoader {

    private const val TAG = "HOME_DENUNCIAS"

    fun cargarMisDenuncias(activity: HomeActivity, token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val denuncias: List<DenunciaResponse> =
                    ApiClient.apiService.obtenerMisDenuncias("Bearer $token")

                Log.d(TAG, "Denuncias recibidas desde API: ${denuncias.size}")

                val denunciasOrdenadas = denuncias.sortedByDescending { it.id }

                withContext(Dispatchers.Main) {

                    val rvMisDenuncias =
                        activity.findViewById<RecyclerView>(R.id.rvMisDenuncias)

                    if (denunciasOrdenadas.isNotEmpty()) {

                        rvMisDenuncias.adapter = DenunciasAdapter(
                            denunciasOrdenadas
                        ) { idDenuncia ->

                            val denuncia = denunciasOrdenadas.find { it.id == idDenuncia }

                            if (denuncia != null) {
                                val intent =
                                    Intent(activity, DetalleDenunciaActivity::class.java).apply {
                                        putExtra("idDenuncia", denuncia.id)
                                        putExtra("descripcion", denuncia.descripcion)
                                        putExtra("direccion", denuncia.direccion_textual)
                                        putExtra(
                                            "estado",
                                            denuncia.estado_display ?: denuncia.estado
                                        )
                                        putExtra("fecha", denuncia.fecha_creacion)
                                        putExtra(
                                            "imagen",
                                            buildImageUrl(denuncia.imagen)
                                        )
                                        putExtra("latitud", denuncia.latitud)
                                        putExtra("longitud", denuncia.longitud)
                                    }
                                activity.startActivity(intent)
                            }
                        }

                    } else {
                        Toast.makeText(
                            activity,
                            "No tienes denuncias aún",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error cargando denuncias", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        activity,
                        "Error cargando denuncias",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun buildImageUrl(path: String?): String? {
        if (path.isNullOrEmpty()) return null
        return if (path.startsWith("http")) {
            path
        } else {
            "https://tubarriolimpio.space$path"
        }
    }
}
