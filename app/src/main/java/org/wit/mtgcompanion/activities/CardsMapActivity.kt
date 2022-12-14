package org.wit.mtgcompanion.activities

import android.Manifest
import android.annotation.SuppressLint
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.wit.mtgcompanion.BuildConfig.MAPS_API_KEY
import org.wit.mtgcompanion.databinding.ActivityCardsMapBinding
import org.wit.mtgcompanion.databinding.ContentCardsMapBinding
import org.wit.mtgcompanion.models.PlaceModel
import timber.log.Timber.e
import timber.log.Timber.i


class CardsMapActivity : AppCompatActivity(), GoogleMap.OnMarkerClickListener {

    private lateinit var binding: ActivityCardsMapBinding
    private lateinit var contentBinding: ContentCardsMapBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>
    private lateinit var map: GoogleMap
    private lateinit var client: HttpClient
    private val places = ArrayList<PlaceModel>()
    private lateinit var loc: LatLng

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

    @SuppressLint("SetTextI18n")
    override fun onMarkerClick(marker: Marker): Boolean {
        var placeId = marker.tag.toString().toShort()
        val foundPlace: PlaceModel? = places.find { p -> p.id == placeId}
        contentBinding.mapPlaceNameTxt.text = foundPlace?.name
        contentBinding.mapPlaceAddressTxt.text = foundPlace?.address
        contentBinding.mapPlaceLocTxt.text = foundPlace?.loc.toString()
        contentBinding.mapPlaceRatingTxt.text = foundPlace?.rating.toString()
        contentBinding.mapTotalUserRatingsTxt.text = foundPlace?.totalUserRatings.toString()
        if(foundPlace?.open!!) contentBinding.mapOpenTxt.text = "Open"
        else contentBinding.mapOpenTxt.text = "Closed"
        return false
    }

    private fun configureMap() {
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
    }

    private fun findCardShops(){
        runBlocking {
            client = HttpClient()
            var url: String =
                "https://maps.googleapis.com/maps/api/place/textsearch/json?query=card%20game%20shops%20near%20me&location=${loc.latitude}%2C${loc.longitude}&radius=10000&key=$MAPS_API_KEY"
            val response = client.get(url)
            //i("${response.body<String>()}")
            val copy = JSONObject(response.body<String>())
            //i("${copy.getJSONArray("results").getJSONObject(0)}")
            for(j in 0 until copy.getJSONArray("results").length()){
                var docPlace = copy.getJSONArray("results").getJSONObject(j)
                var place = PlaceModel(
                    id = j.toShort(),
                    name = docPlace.getString("name"),
                    address = docPlace.getString("formatted_address"),
                    loc = LatLng(
                        docPlace.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                        docPlace.getJSONObject("geometry").getJSONObject("location").getDouble("lng")
                    ),
                    rating = docPlace.getDouble("rating"),
                    totalUserRatings = docPlace.getInt("user_ratings_total"),
                    open = docPlace.getJSONObject("opening_hours").getBoolean("open_now")
                )
                places.add(place)
                //i("${copy.getJSONArray("results").getJSONObject(j).get("name")}")
            }
            places.forEach{
                i("$it")
                val options = MarkerOptions().title(it.name).position(it.loc)
                map.addMarker(options)?.tag = it.id
            }
        }
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

    @SuppressLint("MissingPermission")
    private fun retrieveLocation(){
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { result ->
                    loc = LatLng(result.latitude, result.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 12f))
                    findCardShops()
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
        loc = LatLng(52.245696, -7.139102)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 12f))
        findCardShops()
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