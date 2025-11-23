package com.example.tubarriolimpioapp.ui.home.denuncias

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tubarriolimpioapp.R
import com.example.tubarriolimpioapp.data.model.DenunciaResponse
import com.example.tubarriolimpioapp.util.DateFormatter

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class DenunciasAdapter(
    private val lista: List<DenunciaResponse>,
    private val onVerClick: (idDenuncia: Int) -> Unit
) : RecyclerView.Adapter<DenunciasAdapter.DViewHolder>() {

    inner class DViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtCaso: TextView = view.findViewById(R.id.txtCaso)
        val txtFecha: TextView = view.findViewById(R.id.txtFecha)
        val txtDescripcion: TextView = view.findViewById(R.id.txtDescripcion)
        val txtDireccion: TextView = view.findViewById(R.id.txtDireccion)
        val txtEstado: TextView = view.findViewById(R.id.txtEstado)
        val imgFoto: ImageView = view.findViewById(R.id.imgFoto)
        val btnVer: Button = view.findViewById(R.id.btnVer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_denuncia, parent, false)
        return DViewHolder(view)
    }

    override fun onBindViewHolder(holder: DViewHolder, position: Int) {
        val item = lista[position]

        // Caso / ID
        holder.txtCaso.text = "Caso Nº ${item.id}"

        // Fecha formateada
        holder.txtFecha.text = DateFormatter.formatFecha(item.fecha_creacion)

        holder.txtDescripcion.text = item.descripcion
        holder.txtDireccion.text = item.direccion_textual
        holder.txtEstado.text = item.estado_display ?: item.estado

        // Construye la URL de imagen siempre completa
        val urlImagen = buildImageUrl(item.imagen)

        Glide.with(holder.imgFoto.context)
            .load(urlImagen)
            .placeholder(R.drawable.ic_image_placeholder)
            .error(R.drawable.ic_image_placeholder)
            .into(holder.imgFoto)

        holder.btnVer.setOnClickListener {
            onVerClick(item.id)
        }
    }

    override fun getItemCount(): Int = lista.size

    // ------- Helpers privados -------

    private fun buildImageUrl(path: String?): String? {
        if (path.isNullOrEmpty()) return null
        return if (path.startsWith("http")) {
            path
        } else {
            "https://tubarriolimpio.space$path"
        }
    }

    private fun formatFecha(raw: String?): String {
        if (raw.isNullOrBlank()) return ""
        return try {
            // Ejemplo de raw: 2025-11-22T19:32:05.472018-03:00
            val odt = OffsetDateTime.parse(raw)
            val formatterSalida = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            odt.format(formatterSalida)      // sin segundos
        } catch (e: Exception) {
            // Si algo falla, mostramos el valor original para no romper la app
            raw
        }
    }
}
