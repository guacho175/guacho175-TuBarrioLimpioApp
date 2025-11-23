package com.example.tubarriolimpioapp.ui.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tubarriolimpioapp.R
import com.example.tubarriolimpioapp.utils.validateEmail
import com.example.tubarriolimpioapp.utils.validateMinLength
import com.example.tubarriolimpioapp.utils.validateRequired
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegistrarUsuarioActivity : AppCompatActivity() {

    private val viewModel: RegistrarUsuarioViewModel by viewModels()

    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etPassword2: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var tvIrALogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_usuario)

        // Referencias a la UI
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etPassword2 = findViewById(R.id.etPassword2)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        tvIrALogin = findViewById(R.id.tvIrALogin)

        // Click en "Crear cuenta"
        btnRegistrar.setOnClickListener {
            if (!validarFormulario()) return@setOnClickListener

            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val password2 = etPassword2.text.toString()

            viewModel.onUsernameChange(username)
            viewModel.onEmailChange(email)
            viewModel.onPasswordChange(password)
            viewModel.onPassword2Change(password2)

            viewModel.registrarUsuario()
        }

        // Volver al login sin registrar
        tvIrALogin.setOnClickListener {
            finish()
        }

        observarEstado()
    }

    private fun observarEstado() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                btnRegistrar.isEnabled = !state.isLoading

                state.errorMessage?.let { msg ->
                    Toast.makeText(this@RegistrarUsuarioActivity, msg, Toast.LENGTH_LONG).show()
                }

                state.successMessage?.let { msg ->
                    Toast.makeText(this@RegistrarUsuarioActivity, msg, Toast.LENGTH_LONG).show()
                    // Volvemos al login
                    finish()
                }
            }
        }
    }

    private fun validarFormulario(): Boolean {
        val usernameOk =
            etUsername.validateRequired("El nombre de usuario es obligatorio")
        val emailOk =
            etEmail.validateEmail("Email inválido")
        val passOk =
            etPassword.validateMinLength(6, "La contraseña debe tener al menos 6 caracteres")
        val pass2Ok =
            etPassword2.validateMinLength(6, "La contraseña debe tener al menos 6 caracteres")

        if (passOk && pass2Ok) {
            val p1 = etPassword.text.toString()
            val p2 = etPassword2.text.toString()
            if (p1 != p2) {
                etPassword2.error = "Las contraseñas no coinciden"
                return false
            }
        }

        return usernameOk && emailOk && passOk && pass2Ok
    }
}
