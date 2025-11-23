package com.example.tubarriolimpioapp.ui.home.notificaciones

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.tubarriolimpioapp.data.model.DenunciaResponse
import com.example.tubarriolimpioapp.data.model.NotificacionResponse
import com.example.tubarriolimpioapp.data.network.ApiClient
import com.example.tubarriolimpioapp.ui.home.denuncias.DetalleDenunciaActivity
import com.example.tubarriolimpioapp.ui.home.denuncias.HomeDenunciasLoader
import com.example.tubarriolimpioapp.ui.home.main.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object HomeNotificacionesLoader {

    fun cargarNotificaciones(activity: HomeActivity, token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. Obtenemos la lista de notificaciones y filtramos solo las NO leídas
                val lista: List<NotificacionResponse> =
                    ApiClient.apiService.obtenerNotificaciones("Bearer $token")
                        .filter { !it.leida }

                withContext(Dispatchers.Main) {

                    if (lista.isEmpty()) {
                        // 👉 No hay notificaciones sin leer: ocultamos título + lista
                        activity.txtTituloNotificaciones.visibility = View.GONE
                        activity.rvNotificaciones.visibility = View.GONE

                    } else {
                        // 👉 Hay notificaciones sin leer: mostramos título + lista
                        activity.txtTituloNotificaciones.visibility = View.VISIBLE
                        activity.rvNotificaciones.visibility = View.VISIBLE

                        // 2. Configuramos el adapter
                        activity.rvNotificaciones.adapter = NotificacionesAdapter(
                            lista,
                            onVerClick = { notificacion ->
                                // Aquí solo delegamos: abrir detalle con todos los datos
                                abrirDenunciaDesdeNotificacion(activity, token, notificacion)
                            },
                            onMarcarLeidaClick = { notificacion ->
                                marcarNotificacionLeida(activity, token, notificacion.id)
                            }
                        )
                    }
                }

            } catch (e: Exception) {
                Log.e("HOME_ERROR", "Error cargando notificaciones", e)
            }
        }
    }

    // 🔹 NUEVO: función que busca la denuncia por id y abre el detalle con foto
    private fun abrirDenunciaDesdeNotificacion(
        activity: HomeActivity,
        token: String,
        notificacion: NotificacionResponse
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Pedimos las denuncias del usuario
                val denuncias: List<DenunciaResponse> =
                    ApiClient.apiService.obtenerMisDenuncias("Bearer $token")

                // Buscamos la denuncia por id
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
                Log.e("HOME_ERROR", "Error obteniendo denuncia desde notificación", e)
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
                // 1. Llamada a la API (PATCH)
                ApiClient.apiService.marcarNotificacionLeida(
                    "Bearer $token",
                    idNotificacion,
                    mapOf("leida" to true)
                )

                withContext(Dispatchers.Main) {
                    Toast.makeText(activity, "Marcada como leída", Toast.LENGTH_SHORT).show()
                    // 2. Recargar notificaciones para refrescar la UI
                    cargarNotificaciones(activity, token)
                }

            } catch (e: Exception) {
                Log.e("HOME_ERROR", "Error al marcar como leída", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(activity, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
