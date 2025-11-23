package com.example.tubarriolimpioapp.ui.home.main

import android.content.Intent
import android.os.Bundle
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
import com.example.tubarriolimpioapp.utils.ToolbarUtils   // 👈 IMPORTANTE

class HomeActivity : AppCompatActivity() {

    lateinit var txtBienvenida: TextView
    lateinit var txtTituloNotificaciones: TextView
    lateinit var rvNotificaciones: RecyclerView
    lateinit var rvMisDenuncias: RecyclerView
    lateinit var btnIngresarDenuncia: Button

    lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 🔁 Header común, sin flecha (pantalla principal)
        ToolbarUtils.setupToolbarNoBack(this, "Inicio")

        // UI
        txtBienvenida = findViewById(R.id.txtBienvenida)
        txtTituloNotificaciones = findViewById(R.id.txtTituloNotificaciones)
        rvNotificaciones = findViewById(R.id.rvNotificaciones)
        rvMisDenuncias = findViewById(R.id.rvMisDenuncias)
        btnIngresarDenuncia = findViewById(R.id.btnIngresarDenuncia)

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

        // 1) Perfil
        HomePerfilLoader.cargarPerfil(this, token)

        // 2) Notificaciones
        HomeNotificacionesLoader.cargarNotificaciones(this, token)

        // 3) Mis denuncias
        HomeDenunciasLoader.cargarMisDenuncias(this, token)
    }

    override fun onResume() {
        super.onResume()
        HomeDenunciasLoader.cargarMisDenuncias(this, token)
        HomeNotificacionesLoader.cargarNotificaciones(this, token)
    }
}
