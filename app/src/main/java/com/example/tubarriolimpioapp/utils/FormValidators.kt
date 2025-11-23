package com.example.tubarriolimpioapp.utils

import android.util.Patterns
import android.widget.EditText

/**
 * Valida que el campo no esté vacío.
 * Si está vacío, muestra error y devuelve false.
 */
fun EditText.validateRequired(errorMessage: String): Boolean {
    val textValue = this.text?.toString()?.trim().orEmpty()
    return if (textValue.isEmpty()) {
        this.error = errorMessage
        false
    } else {
        this.error = null
        true
    }
}

/**
 * Valida que el contenido tenga formato de email.
 * Si no es válido, muestra error y devuelve false.
 */
fun EditText.validateEmail(errorMessage: String): Boolean {
    val textValue = this.text?.toString()?.trim().orEmpty()
    return if (textValue.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(textValue).matches()) {
        this.error = errorMessage
        false
    } else {
        this.error = null
        true
    }
}

/**
 * Valida que el contenido tenga al menos [minLength] caracteres.
 */
fun EditText.validateMinLength(minLength: Int, errorMessage: String): Boolean {
    val textValue = this.text?.toString()?.trim().orEmpty()
    return if (textValue.length < minLength) {
        this.error = errorMessage
        false
    } else {
        this.error = null
        true
    }
}

/**
 * Acceso rápido al texto del EditText ya trimmeado.
 */
val EditText.textTrimmed: String
    get() = this.text?.toString()?.trim().orEmpty()
