package com.example.tubarriolimpioapp.ui.home.notificaciones

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.tubarriolimpioapp.R
import com.example.tubarriolimpioapp.data.model.DenunciaResponse
import com.example.tubarriolimpioapp.data.model.NotificacionResponse
import com.example.tubarriolimpioapp.data.network.ApiClient
import com.example.tubarriolimpioapp.ui.home.HomeActivity
import com.example.tubarriolimpioapp.ui.home.denuncias.DetalleDenunciaActivity
import com.example.tubarriolimpioapp.ui.home.denuncias.HomeDenunciasLoader
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object HomeNotificacionesLoader {

    private const val TAG = "HOME_NOTIFICACIONES"

    fun cargarNotificaciones(activity: HomeActivity, token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lista: List<NotificacionResponse> =
                    ApiClient.apiService.obtenerNotificaciones("Bearer $token")
                        .filter { !it.leida }

                withContext(Dispatchers.Main) {

                    val cardNotificaciones =
                        activity.findViewById<MaterialCardView>(R.id.cardNotificaciones)
                    val txtTitulo =
                        activity.findViewById<TextView>(R.id.txtTituloNotificaciones)
                    val txtBadge =
                        activity.findViewById<TextView>(R.id.txtNotificacionesBadge)
                    val rv =
                        activity.findViewById<RecyclerView>(R.id.rvNotificaciones)

                    cardNotificaciones.visibility = View.VISIBLE

                    if (lista.isEmpty()) {
                        txtTitulo.text = "Sin notificaciones nuevas"
                        txtBadge.visibility = View.GONE
                        rv.visibility = View.GONE
                        rv.adapter = null
                    } else {
                        txtTitulo.text = "Notificaciones recientes"
                        txtBadge.visibility = View.VISIBLE
                        txtBadge.text = lista.size.toString()

                        rv.adapter = NotificacionesAdapter(
                            lista,
                            onVerClick = { notificacion ->
                                abrirDenunciaDesdeNotificacion(activity, token, notificacion)
                            },
                            onMarcarLeidaClick = { notificacion ->
                                marcarNotificacionLeida(activity, token, notificacion.id)
                            }
                        )

                        // El acordeón parte cerrado; HomeActivity lo controla
                        rv.visibility = View.GONE
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error cargando notificaciones", e)
            }
        }
    }

    private fun abrirDenunciaDesdeNotificacion(
        activity: HomeActivity,
        token: String,
        notificacion: NotificacionResponse
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val denuncias: List<DenunciaResponse> =
                    ApiClient.apiService.obtenerMisDenuncias("Bearer $token")

                val denuncia = denuncias.find { it.id == notificacion.denuncia }

                withContext(Dispatchers.Main) {
                    if (denuncia != null) {
                        val intent = Intent(activity, DetalleDenunciaActivity::class.java).apply {
                            putExtra("idDenuncia", denuncia.id)
                            putExtra("descripcion", denuncia.descripcion)
                            putExtra("direccion", denuncia.direccion_textual)
                            putExtra("estado", denuncia.estado_display ?: denuncia.estado)
                            putExtra("fecha", denuncia.fecha_creacion)
                            putExtra("imagen", HomeDenunciasLoader.buildImageUrl(denuncia.imagen))
                            putExtra("latitud", denuncia.latitud)
                            putExtra("longitud", denuncia.longitud)
                        }
                        activity.startActivity(intent)
                    } else {
                        Toast.makeText(
                            activity,
                            "No se encontró la denuncia asociada",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error obteniendo denuncia desde notificación", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        activity,
                        "Error al abrir la denuncia",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun marcarNotificacionLeida(activity: HomeActivity, token: String, idNotificacion: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ApiClient.apiService.marcarNotificacionLeida(
                    "Bearer $token",
                    idNotificacion,
                    mapOf("leida" to true)
                )

                withContext(Dispatchers.Main) {
                    Toast.makeText(activity, "Marcada como leída", Toast.LENGTH_SHORT).show()
                    cargarNotificaciones(activity, token)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error al marcar como leída", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(activity, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
