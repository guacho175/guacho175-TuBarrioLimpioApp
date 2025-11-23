package com.example.tubarriolimpioapp.models

data class NotificacionResponse(
    val id: Int,
    val mensaje: String,
    val leida: Boolean,
    val fecha_creacion: String,
    val denuncia: Int,
    val denuncia_descripcion: String?,
    val estado_nuevo: String?,
    val estado_nuevo_display: String?
)
