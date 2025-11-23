package com.example.tubarriolimpioapp.ui.home.denuncias

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.tubarriolimpioapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DenunciaMapFragment : Fragment(R.layout.fragment_denuncia_map), OnMapReadyCallback {

    private var latitud: Double = 0.0
    private var longitud: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            latitud = it.getDouble("latitud", 0.0)
            longitud = it.getDouble("longitud", 0.0)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val location = LatLng(latitud, longitud)
        googleMap.addMarker(MarkerOptions().position(location).title("Ubicación de la denuncia"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
    }

    companion object {
        fun newInstance(lat: Double, lon: Double) = DenunciaMapFragment().apply {
            arguments = Bundle().apply {
                putDouble("latitud", lat)
                putDouble("longitud", lon)
            }
        }
    }
}
