package com.example.nobeteczane.api

import com.example.nobeteczane.model.ListModel
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Headers

interface EczaneAPI {
    @Headers(
        "Content-Type: application/json",
        "Authorization: apikey 3S4qp8T3iXZpsHfxJrDpWn:0Ev3rsJzfkGYw8TbnDGf6P"
    )
    @GET("dutyPharmacy?il=Istanbul'")
    fun veriCek() : Observable<ListModel>

    //fun veriCek() : Call<ListModel>
}