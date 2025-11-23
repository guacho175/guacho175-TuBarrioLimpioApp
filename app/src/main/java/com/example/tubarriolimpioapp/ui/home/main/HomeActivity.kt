package com.example.tubarriolimpioapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
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

    lateinit var txtBienvenida: TextView
    lateinit var txtTituloNotificaciones: TextView
    lateinit var rvNotificaciones: RecyclerView
    lateinit var rvMisDenuncias: RecyclerView
    lateinit var btnIngresarDenuncia: Button

    // Nuevos para el acordeón
    lateinit var cardNotificaciones: MaterialCardView
    lateinit var layoutNotificacionesHeader: LinearLayout
    lateinit var txtNotificacionesBadge: TextView
    lateinit var imgExpandNotificaciones: ImageView

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
        txtTituloNotificaciones = findViewById(R.id.txtTituloNotificaciones)
        rvNotificaciones = findViewById(R.id.rvNotificaciones)
        rvMisDenuncias = findViewById(R.id.rvMisDenuncias)
        btnIngresarDenuncia = findViewById(R.id.btnIngresarDenuncia)

        // Vistas nuevas del acordeón
        cardNotificaciones = findViewById(R.id.cardNotificaciones)
        layoutNotificacionesHeader = findViewById(R.id.layoutNotificacionesHeader)
        txtNotificacionesBadge = findViewById(R.id.txtNotificacionesBadge)
        imgExpandNotificaciones = findViewById(R.id.imgExpandNotificaciones)

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

        // Token: igual que tenías antes
        token = getSharedPreferences("auth", MODE_PRIVATE)
            .getString("token", null) ?: return

        // 1) Perfil
        HomePerfilLoader.cargarPerfil(this, token)

        // 2) Notificaciones
        HomeNotificacionesLoader.cargarNotificaciones(this, token)

        // 3) Mis denuncias
        HomeDenunciasLoader.cargarMisDenuncias(this, token)

        // Acordeón: header y flecha abren/cierran
        layoutNotificacionesHeader.setOnClickListener {
            toggleAcordeonNotificaciones()
        }
        imgExpandNotificaciones.setOnClickListener {
            toggleAcordeonNotificaciones()
        }
    }

    override fun onResume() {
        super.onResume()
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

    // ---------- Lógica del acordeón ----------

    private fun toggleAcordeonNotificaciones() {
        // Si la lista todavía no tiene adapter, no hacemos nada
        if (rvNotificaciones.adapter == null) {
            // Puede no haber notificaciones nuevas, solo mostramos el título
            return
        }

        acordeonAbierto = !acordeonAbierto

        if (acordeonAbierto) {
            rvNotificaciones.visibility = View.VISIBLE
            imgExpandNotificaciones.animate()
                .rotation(180f)
                .setDuration(150)
                .start()
        } else {
            rvNotificaciones.visibility = View.GONE
            imgExpandNotificaciones.animate()
                .rotation(0f)
                .setDuration(150)
                .start()
        }
    }
}
