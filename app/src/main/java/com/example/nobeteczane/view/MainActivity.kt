package com.example.nobeteczane.view

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.nobeteczane.api.EczaneAPI
import com.example.nobeteczane.model.EczaneModel
import com.example.nobeteczane.model.EczaneViewModel
import com.example.nobeteczane.model.KonumViewModel
import com.example.nobeteczane.model.ListModel
import com.example.nobeteczane.R
import com.example.nobeteczane.databinding.ActivityMainBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private val konumIzin = "android.permission.ACCESS_FINE_LOCATION"
    private val url = "https://api.collectapi.com/health/"
    private lateinit var list: ArrayList<EczaneModel>
    private lateinit var konum: LatLng
    private lateinit var konumViewModel: KonumViewModel
    private lateinit var eczaneViewModel: EczaneViewModel
    private lateinit var siralanmisEczane:ArrayList<EczaneModel>
    private lateinit var fragment:FragmentContainerView
    private var compositeDisposable : CompositeDisposable? = null
    private var state=false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        compositeDisposable = CompositeDisposable()
        fragment = findViewById(R.id.nav_host_fragment_activity_main)
        supportActionBar?.hide()
        fragment.visibility=View.GONE
        register()
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(EczaneAPI::class.java)

            compositeDisposable?.add(retrofit.veriCek()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        handleResponse(response)
                    },
                    {
                        Toast.makeText(this,"Bağlantı Hatası Oluştu",Toast.LENGTH_LONG).show()
                        println(it.localizedMessage)
                    }
                )
            )

        }catch (e:Exception){
            Toast.makeText(this,"Bağlantı Hatası Oluştu",Toast.LENGTH_LONG).show()
            println(e.localizedMessage)
        }

    }

    private fun handleResponse(veri:ListModel){
        list = ArrayList()
        veri.result.forEach {
            val konum = parseLocation(it.loc)
            it.enlem = konum!!.first
            it.boylam = konum.second
            list.add(it)
        }
        if (list.isNotEmpty()) {
            konumViewModel = ViewModelProvider(this@MainActivity)[KonumViewModel::class.java]
            eczaneViewModel = ViewModelProvider(this@MainActivity)[EczaneViewModel::class.java]
            locationManager = this@MainActivity.getSystemService(LOCATION_SERVICE) as LocationManager

            if (ContextCompat.checkSelfPermission(this@MainActivity,konumIzin) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,konumIzin)) {
                    Snackbar.make(binding.root, "Konum İzni Gereklidir", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Evet") {
                            permissionLauncher.launch(konumIzin)
                        }.show()
                }
                else {
                    permissionLauncher.launch(konumIzin)
                }
            }
            else {
                getLocation()
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun getLocation(){
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                konum = LatLng(location.latitude, location.longitude)
                konumViewModel.konumBilgisi.value = konum
                siralanmisEczane = ArrayList(findNearestEczanes(konum.latitude,konum.longitude,list))
                val ilk30Eczane = if (siralanmisEczane.size > 30) {
                    siralanmisEczane.subList(0, 30)
                } else {
                    siralanmisEczane
                }
                eczaneViewModel.list.value = ArrayList(ilk30Eczane)
                if(!state) {
                    bind()
                }
            }
            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }
            override fun onProviderDisabled(provider: String) {
                Toast.makeText(this@MainActivity,"Konum Kapalı",Toast.LENGTH_SHORT).show()
            }
            override fun onProviderEnabled(provider: String) {
                Toast.makeText(this@MainActivity,"Konum Etkin",Toast.LENGTH_SHORT).show()
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000,15f,locationListener)
        }

    private fun bind(){
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_home, R.id.mapsFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        state=true
        binding.animationView.pauseAnimation()
        binding.animationView.clearAnimation()
        binding.animationView.visibility = View.GONE
        fragment.visibility= View.VISIBLE
    }

    private fun register(){
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it){
                if (ContextCompat.checkSelfPermission(this,konumIzin)!= PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Konum izni gereklidir", Toast.LENGTH_SHORT).show()
                    permissionLauncher.launch(konumIzin)
                }
                else{
                    getLocation()
                }
            }
            else{
                Toast.makeText(this,"Konum izni gereklidir", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun parseLocation(locationString: String): Pair<Double, Double>? {
        val parts = locationString.split(',')
        if (parts.size == 2) {
            return try {
                val latitude = parts[0].toDouble()
                val longitude = parts[1].toDouble()
                Pair(latitude, longitude)
            } catch (e: NumberFormatException) {
                null
            }
        }
        return null
    }

    private fun findNearestEczanes(userLatitude: Double, userLongitude: Double, eczaneler: ArrayList<EczaneModel>): List<EczaneModel> {
        val eczanelerWithDistance = eczaneler.map { eczane ->
            val eczaneLatitude = eczane.enlem
            val eczaneLongitude = eczane.boylam

            val theta = userLongitude - eczaneLongitude
            val sinUserLat = sin(deg2rad(userLatitude))
            val sinEczaneLat = sin(deg2rad(eczaneLatitude))
            val cosUserLat = cos(deg2rad(userLatitude))
            val cosEczaneLat = cos(deg2rad(eczaneLatitude))
            val deltaTheta = deg2rad(theta)
            val mesafe = sinUserLat * sinEczaneLat + cosUserLat * cosEczaneLat * cos(deltaTheta)
            val mesafeDerece = acos(mesafe)
            val mesafeRadyan = rad2deg(mesafeDerece)
            val mesafeKilometre = mesafeRadyan * 60.0 * 1.1515 * 1.60934
            eczane.mesafe = mesafeKilometre

            Pair(eczane, mesafeKilometre)
        }

        return eczanelerWithDistance.sortedBy { it.second }.map { it.first }
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable?.clear()
    }
}