package com.example.nobeteczane.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class KonumViewModel : ViewModel() {
    var konumBilgisi: MutableLiveData<LatLng> = MutableLiveData()
}