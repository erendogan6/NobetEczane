package com.example.nobeteczane.model

import com.google.gson.annotations.SerializedName

data class EczaneModel(

    @SerializedName("dist")
    var dist:String,

    @SerializedName("phone")
    var phone: String,

    @SerializedName("address")
    var address: String,

    @SerializedName("loc")
    val loc:String,

    @SerializedName("name")
    val name:String,

    var enlem:Double,

    var boylam:Double,

    var mesafe:Double
)