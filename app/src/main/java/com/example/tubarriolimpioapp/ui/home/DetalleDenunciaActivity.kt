package com.example.tubarriolimpioapp.ui.home

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.tubarriolimpioapp.R
import com.example.tubarriolimpioapp.util.DateFormatter

class DetalleDenunciaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_denuncia)

        // ✅ Configurar la barra superior
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // ✅ Datos desde el intent
        val idDenuncia = intent.getIntExtra("idDenuncia", 0)
        val descripcion = intent.getStringExtra("descripcion") ?: "Sin descripción"
        val direccion = intent.getStringExtra("direccion") ?: "Sin dirección"
        val estado = intent.getStringExtra("estado") ?: "Sin estado"
        val fechaRaw = intent.getStringExtra("fecha")
        val fechaFormateada = DateFormatter.formatFecha(fechaRaw)
        val imagenUrl = intent.getStringExtra("imagen")
        val latitud = intent.getDoubleExtra("latitud", 0.0)
        val longitud = intent.getDoubleExtra("longitud", 0.0)

        // ✅ Mostrar los datos en la vista
        findViewById<TextView>(R.id.tvIdDenuncia).text = "Caso Nº $idDenuncia"
        findViewById<TextView>(R.id.tvDescripcion).text = descripcion
        findViewById<TextView>(R.id.tvDireccion).text = direccion
        findViewById<TextView>(R.id.tvEstado).text = estado
        findViewById<TextView>(R.id.tvFecha).text =
            if (fechaFormateada.isBlank()) "Sin fecha" else fechaFormateada
        findViewById<TextView>(R.id.tvCoordenadas).text = "Lat: $latitud\nLong: $longitud"

        // ✅ Cargar imagen
        val ivImagen = findViewById<ImageView>(R.id.ivImagen)
        if (!imagenUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imagenUrl)
                .into(ivImagen)
        }

        // ✅ Mapa
        val mapFragment = DenunciaMapFragment.newInstance(latitud, longitud)
        supportFragmentManager.beginTransaction()
            .replace(R.id.mapContainer, mapFragment)
            .commit()
    }
}
