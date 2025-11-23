package com.example.tubarriolimpioapp.ui.ingresar_denuncia

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tubarriolimpioapp.data.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class IngresarDenunciaViewModel : ViewModel() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _latLong = MutableLiveData<Pair<Double, Double>>()
    val latLong: LiveData<Pair<Double, Double>> get() = _latLong

    private val _toast = MutableLiveData<String>()
    val toast: LiveData<String> get() = _toast

    // NUEVO: estado de envío
    private val _isSending = MutableLiveData(false)
    val isSending: LiveData<Boolean> get() = _isSending

    private var imagenFile: File? = null

    fun setUbicacion(lat: Double, long: Double) {
        _latLong.postValue(Pair(lat, long))
    }

    fun setImagen(file: File) {
        imagenFile = file
    }

    fun enviarDenuncia(
        token: String,
        descripcion: String,
        direccion: String,
        onSuccess: () -> Unit
    ) {
        // Si ya se está enviando, ignoramos nuevos llamados
        if (_isSending.value == true) return

        val (lat, long) = _latLong.value ?: run {
            _toast.postValue("Ubicación no disponible")
            return
        }

        val imagen = imagenFile ?: run {
            _toast.postValue("Por favor selecciona una imagen")
            return
        }

        scope.launch {
            _isSending.postValue(true)
            try {
                val textMedia = "text/plain".toMediaTypeOrNull()

                val descripcionBody = descripcion.toRequestBody(textMedia)
                val direccionBody = direccion.toRequestBody(textMedia)
                val latBody = lat.toString().toRequestBody(textMedia)
                val longBody = long.toString().toRequestBody(textMedia)

                val imagenBody = imagen.asRequestBody("image/*".toMediaTypeOrNull())
                val imagenPart =
                    MultipartBody.Part.createFormData("imagen", imagen.name, imagenBody)

                ApiClient.apiService.crearDenuncia(
                    "Bearer $token",
                    descripcionBody,
                    direccionBody,
                    latBody,
                    longBody,
                    imagenPart
                )

                _toast.postValue("Denuncia enviada con éxito 🎉")
                withContext(Dispatchers.Main) {
                    onSuccess()
                }

                imagenFile = null
            } catch (e: Exception) {
                _toast.postValue("Error al enviar: ${e.message}")
            } finally {
                _isSending.postValue(false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}
