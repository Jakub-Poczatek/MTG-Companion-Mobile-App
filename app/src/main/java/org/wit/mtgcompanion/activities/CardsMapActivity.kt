package org.wit.mtgcompanion.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import org.wit.mtgcompanion.databinding.ActivityCardsMapBinding
import org.wit.mtgcompanion.databinding.ContentCardsMapBinding
import timber.log.Timber.e
import timber.log.Timber.i

class CardsMapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardsMapBinding
    private lateinit var contentBinding: ContentCardsMapBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>
    lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCardsMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        contentBinding = ContentCardsMapBinding.bind(binding.root)
        contentBinding.mapMpVw.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()){
                permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    retrieveLocation()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    retrieveLocation()
                } else -> {
                    retrieveDefaultLocation()
                }
            }
        }
        checkLocPermissions()

        contentBinding.mapMpVw.getMapAsync {
            map = it
            configureMap()
        }
    }

    private fun configureMap() {
        map.uiSettings.isZoomControlsEnabled = true
    }

    private fun checkLocPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        } else {
            retrieveLocation()
        }
    }

    private fun retrieveLocation(){
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { result ->
                    var loc = LatLng(result.latitude, result.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16f))
                }
                .addOnFailureListener { error ->
                    e("Error occurred whilst retrieving current location: $error")
                    retrieveDefaultLocation()
                }
        } catch (ex: SecurityException){
            e("Error getting permissions: $ex")
        }
    }

    private fun retrieveDefaultLocation(){
        var loc = LatLng(52.245696, -7.139102)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16f))
    }

    override fun onDestroy() {
        super.onDestroy()
        contentBinding.mapMpVw.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        contentBinding.mapMpVw.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        contentBinding.mapMpVw.onPause()
    }

    override fun onResume() {
        super.onResume()
        contentBinding.mapMpVw.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        contentBinding.mapMpVw.onSaveInstanceState(outState)
    }
}