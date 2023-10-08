package com.example.nobeteczane.model

import com.google.gson.annotations.SerializedName

data class ListModel(
    val success:String,

    @SerializedName("result")
    val result:List<EczaneModel>,
)