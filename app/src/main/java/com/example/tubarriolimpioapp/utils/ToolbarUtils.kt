package com.example.tubarriolimpioapp.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.tubarriolimpioapp.R

object ToolbarUtils {

    // Función interna para no repetir código
    private fun setupToolbarInternal(
        activity: AppCompatActivity,
        title: String,
        showBack: Boolean
    ) {
        val toolbar = activity.findViewById<Toolbar>(R.id.toolbar) ?: return

        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.apply {
            this.title = title
            setDisplayHomeAsUpEnabled(showBack)
            setDisplayShowHomeEnabled(showBack)
        }

        if (showBack) {
            toolbar.setNavigationOnClickListener {
                activity.onBackPressedDispatcher.onBackPressed()
            }
        } else {
            // Por si acaso, limpiamos cualquier listener previo
            toolbar.setNavigationOnClickListener(null)
        }
    }

    // 👉 Para pantallas con flecha (Detalle, Ingresar, etc.)
    fun setupToolbarWithBack(activity: AppCompatActivity, title: String) {
        setupToolbarInternal(activity, title, showBack = true)
    }

    // 👉 Para pantallas sin flecha (Home, Login, etc.)
    fun setupToolbarNoBack(activity: AppCompatActivity, title: String) {
        setupToolbarInternal(activity, title, showBack = false)
    }
}
