package com.example.tubarriolimpioapp.data.model

data class DenunciaResponse(
    val id: Int,
    val descripcion: String,
    val direccion: String?,
    val zona: String?,
    val direccion_textual: String,
    val estado: String,
    val estado_display: String,
    val fecha_creacion: String,
    val imagen: String?,
    val latitud: Double,
    val longitud: Double,
    val cuadrilla_asignada: String?,
    val reporte_cuadrilla: ReporteCuadrilla?,
    val color: String?,
    val motivo_rechazo: String?,
    val jefe_cuadrilla_asignado: JefeCuadrilla?
)

data class ReporteCuadrilla(
    val id: Int?,
    val comentario: String?,
    val foto_trabajo: String?,
    val fecha_reporte: String?,
    val jefe_cuadrilla_asignado: JefeCuadrilla?
)

