package com.example.tubarriolimpioapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tubarriolimpioapp.R
import com.example.tubarriolimpioapp.models.LoginResponse
import com.example.tubarriolimpioapp.network.ApiClient
import com.example.tubarriolimpioapp.network.ApiService
import com.example.tubarriolimpioapp.ui.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var api: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        api = ApiClient.retrofit.create(ApiService::class.java)
        val tvIrARegistro: TextView = findViewById(R.id.tvIrARegistro)
        tvIrARegistro.setOnClickListener {
            val intent = Intent(this, RegistrarUsuarioActivity::class.java)
            startActivity(intent)
        }

        val usernameInput = findViewById<EditText>(R.id.inputUsername)
        val passwordInput = findViewById<EditText>(R.id.inputPassword)
        val loginBtn = findViewById<Button>(R.id.btnLogin)

        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            login(username, password)
        }
    }

    private fun login(username: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: LoginResponse = api.login(
                    mapOf(
                        "username" to username,
                        "password" to password
                    )
                )

                // GUARDAR TOKEN
                val prefs = getSharedPreferences("auth", MODE_PRIVATE)
                prefs.edit().putString("token", response.access).apply()

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
