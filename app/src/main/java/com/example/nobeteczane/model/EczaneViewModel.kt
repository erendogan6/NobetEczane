package com.example.nobeteczane.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EczaneViewModel : ViewModel() {
    var list: MutableLiveData<ArrayList<EczaneModel>> = MutableLiveData()
}