package com.example.nobeteczane.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.nobeteczane.R
import com.example.nobeteczane.model.EczaneViewModel
import com.example.nobeteczane.model.KonumViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {
    private lateinit var eczaneViewModel: EczaneViewModel
    private lateinit var konumViewModel: KonumViewModel

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(konumViewModel.konumBilgisi.value!!, 15f))
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true

        googleMap.isMyLocationEnabled = true
        observeNewLocation(googleMap)
    }

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        konumViewModel = ViewModelProvider(requireActivity())[KonumViewModel::class.java]
        eczaneViewModel = ViewModelProvider(requireActivity())[EczaneViewModel::class.java]
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun observeNewLocation(googleMap: GoogleMap) {
        konumViewModel.konumBilgisi.observe(viewLifecycleOwner) { newLocation ->
            newLocation?.let {
                updateEczane(googleMap)
            }
        }
    }

    private fun updateEczane(googleMap: GoogleMap){
        val maxEczaneCount = 30
        for ((index, eczane) in eczaneViewModel.list.value!!.withIndex()) {
            if (index < maxEczaneCount) {
                googleMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(eczane.enlem, eczane.boylam))
                        .title(eczane.name)
                        .snippet(eczane.address)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo))
                )
            } else {
                break
            }
        }
    }
}