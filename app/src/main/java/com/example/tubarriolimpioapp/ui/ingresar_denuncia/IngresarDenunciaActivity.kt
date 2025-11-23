package com.example.tubarriolimpioapp.ui.ingresar_denuncia

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.tubarriolimpioapp.R
import com.example.tubarriolimpioapp.ui.common.LoadingDialogFragment
import com.example.tubarriolimpioapp.utils.FileUtils
import com.example.tubarriolimpioapp.utils.ToolbarUtils
import com.example.tubarriolimpioapp.utils.textTrimmed
import com.example.tubarriolimpioapp.utils.validateRequired
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File

class IngresarDenunciaActivity : AppCompatActivity() {

    private val viewModel: IngresarDenunciaViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var etDescripcion: EditText
    private lateinit var etDireccion: EditText
    private lateinit var tvUbicacion: TextView
    private lateinit var btnEnviar: Button
    private lateinit var btnUbicacion: Button
    private lateinit var btnImagen: Button
    private lateinit var imagePreview: ImageView

    private var imagenSeleccionada: File? = null
    private var cameraTempFile: File? = null

    // NUEVO: referencia al dialog de carga
    private var loadingDialog: LoadingDialogFragment? = null

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                abrirCamara()
            } else {
                Toast.makeText(this, "Se requiere permiso de cámara", Toast.LENGTH_SHORT).show()
            }
        }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingresar_denuncia)

        // Header común con flecha atrás
        ToolbarUtils.setupToolbarWithBack(this, "Ingresar denuncia")

        etDescripcion = findViewById(R.id.etDescripcion)
        etDireccion = findViewById(R.id.etDireccion)
        tvUbicacion = findViewById(R.id.tvUbicacion)
        btnEnviar = findViewById(R.id.btnEnviar)
        btnUbicacion = findViewById(R.id.btnObtenerUbicacion)
        btnImagen = findViewById(R.id.btnSeleccionarImagen)
        imagePreview = findViewById(R.id.imagePreview)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btnUbicacion.setOnClickListener {
            obtenerUbicacion()
        }

        btnImagen.setOnClickListener {
            mostrarOpcionesImagen()
        }

        btnEnviar.setOnClickListener {
            // Si ya está enviando, ignoramos el clic
            if (viewModel.isSending.value == true) {
                return@setOnClickListener
            }

            val token = getSharedPreferences("auth", MODE_PRIVATE)
                .getString("token", null) ?: return@setOnClickListener

            // Validaciones
            val descOk = etDescripcion.validateRequired("La descripción es obligatoria")
            val dirOk = etDireccion.validateRequired("La dirección es obligatoria")

            var otrosOk = true

            // Ubicación requerida
            if (viewModel.latLong.value == null) {
                Toast.makeText(this, "Debes obtener la ubicación", Toast.LENGTH_SHORT).show()
                otrosOk = false
            }

            // Imagen requerida
            val imagen = imagenSeleccionada
            if (imagen == null) {
                Toast.makeText(this, "Debes seleccionar una imagen", Toast.LENGTH_SHORT).show()
                otrosOk = false
            }

            if (!descOk || !dirOk || !otrosOk) {
                return@setOnClickListener
            }

            val descripcion = etDescripcion.textTrimmed
            val direccion = etDireccion.textTrimmed

            viewModel.setImagen(imagen!!)
            viewModel.enviarDenuncia(token, descripcion, direccion) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

        // Observa cambios de ubicación
        viewModel.latLong.observe(this) { (lat, long) ->
            tvUbicacion.text = "Lat: $lat\nLong: $long"
        }

        // Observa mensajes tipo Toast
        viewModel.toast.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        // NUEVO: Observa estado de envío para mostrar/ocultar loader y desactivar botón
        viewModel.isSending.observe(this) { isSending ->
            btnEnviar.isEnabled = !isSending

            if (isSending) {
                if (loadingDialog == null) {
                    loadingDialog =
                        LoadingDialogFragment.newInstance("Enviando denuncia...")
                }
                if (loadingDialog?.isAdded != true) {
                    loadingDialog?.show(supportFragmentManager, "loading_dialog")
                }
            } else {
                loadingDialog?.dismissAllowingStateLoss()
            }
        }
    }

    private fun obtenerUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                viewModel.setUbicacion(it.latitude, it.longitude)
            }
        }
    }

    private fun mostrarOpcionesImagen() {
        val opciones = arrayOf("📷 Tomar foto", "🖼️ Elegir de galería")
        AlertDialog.Builder(this)
            .setTitle("Seleccionar imagen")
            .setItems(opciones) { _, opcion ->
                when (opcion) {
                    0 -> tomarFoto()
                    1 -> seleccionarGaleria()
                }
            }.show()
    }

    private fun tomarFoto() {
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun abrirCamara() {
        cameraTempFile = File.createTempFile("camera_image", ".jpg", cacheDir)
        val uri =
            FileProvider.getUriForFile(this, "$packageName.fileprovider", cameraTempFile!!)
        pickImageCamera.launch(uri)
    }

    private fun seleccionarGaleria() {
        pickImageLauncher.launch("image/*")
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imagePreview.setImageURI(it)
                imagenSeleccionada = com.example.tubarriolimpioapp.utils.FileUtils.uriToFile(
                    this,
                    it
                )
            }
        }

    private val pickImageCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && cameraTempFile != null) {
                imagenSeleccionada = cameraTempFile
                viewModel.setImagen(cameraTempFile!!)
                imagePreview.setImageURI(Uri.fromFile(cameraTempFile))
            }
        }
}
