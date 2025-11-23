package com.example.tubarriolimpioapp.ui.home.notificaciones

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tubarriolimpioapp.R
import com.example.tubarriolimpioapp.data.model.NotificacionResponse
import com.example.tubarriolimpioapp.util.DateFormatter


class NotificacionesAdapter(
    private val lista: List<NotificacionResponse>,
    private val onVerClick: (notificacion: NotificacionResponse) -> Unit,
    private val onMarcarLeidaClick: (notificacion: NotificacionResponse) -> Unit
) : RecyclerView.Adapter<NotificacionesAdapter.NotiViewHolder>() {

    inner class NotiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtEstado: TextView = view.findViewById(R.id.txtEstado)
        val txtMensaje: TextView = view.findViewById(R.id.txtMensaje)
        val txtFecha: TextView = view.findViewById(R.id.txtFecha)
        val btnVerDenuncia: Button = view.findViewById(R.id.btnVerDenuncia)
        val btnMarcarLeida: Button = view.findViewById(R.id.btnMarcarLeida)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notificacion, parent, false)
        return NotiViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotiViewHolder, position: Int) {
        val item = lista[position]

        holder.txtEstado.text = item.estado_nuevo_display ?: "Estado"
        holder.txtMensaje.text = item.mensaje
        holder.txtFecha.text = DateFormatter.formatFecha(item.fecha_creacion)

        holder.btnVerDenuncia.setOnClickListener {
            onVerClick(item) // 🔄 Cambiado
        }

        holder.btnMarcarLeida.setOnClickListener {
            onMarcarLeidaClick(item) // 🔄 Cambiado
        }
    }

    override fun getItemCount(): Int = lista.size
}
