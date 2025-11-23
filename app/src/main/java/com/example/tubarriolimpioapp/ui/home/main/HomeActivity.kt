package com.example.tubarriolimpioapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tubarriolimpioapp.R
import com.example.tubarriolimpioapp.ui.home.denuncias.HomeDenunciasLoader
import com.example.tubarriolimpioapp.ui.home.notificaciones.HomeNotificacionesLoader
import com.example.tubarriolimpioapp.ui.home.perfil.HomePerfilLoader
import com.example.tubarriolimpioapp.ui.ingresar_denuncia.IngresarDenunciaActivity
import com.example.tubarriolimpioapp.utils.ToolbarUtils
import com.google.android.material.card.MaterialCardView

class HomeActivity : AppCompatActivity() {

    // Variables mantenidas
    lateinit var txtBienvenida: TextView
    lateinit var rvNotificaciones: RecyclerView
    lateinit var rvMisDenuncias: RecyclerView
    lateinit var btnIngresarDenuncia: Button
    lateinit var cardNotificaciones: MaterialCardView // Se mantiene para control de visibilidad
    lateinit var token: String

    // Estado del acordeón
    private var acordeonAbierto: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Header común, sin flecha (pantalla principal)
        ToolbarUtils.setupToolbarNoBack(this, "Inicio")

        // UI
        txtBienvenida = findViewById(R.id.txtBienvenida)
        rvNotificaciones = findViewById(R.id.rvNotificaciones)
        rvMisDenuncias = findViewById(R.id.rvMisDenuncias)
        btnIngresarDenuncia = findViewById(R.id.btnIngresarDenuncia)

        // Vistas para el acordeón (Solo se mantiene cardNotificaciones y rvNotificaciones)
        cardNotificaciones = findViewById(R.id.cardNotificaciones)

        btnIngresarDenuncia.setOnClickListener {
            startActivity(Intent(this, IngresarDenunciaActivity::class.java))
        }

        // LayoutManagers: sin scroll propio, lo maneja el NestedScrollView
        rvNotificaciones.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean = false
        }

        rvMisDenuncias.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean = false
        }

        // Token
        token = getSharedPreferences("auth", MODE_PRIVATE)
            .getString("token", null) ?: return

        // Carga de datos
        HomePerfilLoader.cargarPerfil(this, token)
        HomeNotificacionesLoader.cargarNotificaciones(this, token)
        HomeDenunciasLoader.cargarMisDenuncias(this, token)

        // **Listeners de encabezado eliminados, la funcionalidad se centraliza en el Toolbar.**
    }

    override fun onResume() {
        super.onResume()
        // Recargar datos al volver a la actividad
        token = getSharedPreferences("auth", MODE_PRIVATE)
            .getString("token", null) ?: return

        HomeDenunciasLoader.cargarMisDenuncias(this, token)
        HomeNotificacionesLoader.cargarNotificaciones(this, token)
    }

    // ---------- Menú del Toolbar: campanita ----------

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notificaciones -> {
                toggleAcordeonNotificaciones()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ---------- Lógica del acordeón simplificada ----------

    private fun toggleAcordeonNotificaciones() {
        // Aseguramos que haya datos antes de expandir/contraer
        if (rvNotificaciones.adapter == null) {
            return
        }

        acordeonAbierto = !acordeonAbierto

        if (acordeonAbierto) {
            // Muestra el RecyclerView al presionar el ícono
            rvNotificaciones.visibility = View.VISIBLE
        } else {
            // Oculta el RecyclerView
            rvNotificaciones.visibility = View.GONE
        }

        // **Lógica de animación de flecha eliminada**
    }
}