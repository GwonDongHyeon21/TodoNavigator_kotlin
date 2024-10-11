package todo_navigator.example.todo_navigator_kotlin.map

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import todo_navigator.example.todo_navigator_kotlin.BuildConfig
import todo_navigator.example.todo_navigator_kotlin.R
import todo_navigator.example.todo_navigator_kotlin.TodoAdd
import todo_navigator.example.todo_navigator_kotlin.databinding.ActivityGoogleMapBinding
import java.util.Locale

class GoogleMap : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMapBinding: ActivityGoogleMapBinding

    private lateinit var map: GoogleMap
    private lateinit var placesClient: PlacesClient
    private var marker: Marker? = null
    private val defaultLocation = LatLng(37.5665, 126.9780)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleMapBinding = ActivityGoogleMapBinding.inflate(layoutInflater)
        setContentView(googleMapBinding.root)

        Places.initialize(applicationContext, BuildConfig.GOOGLE_MAP_API)
        placesClient = Places.createClient(this)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocompleteFragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG
            )
        )
        autocompleteFragment.setCountries("KR")
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val latLng = place.latLng
                latLng?.let {
                    updateMapWithSelectedLocation(latLng)
                }
            }

            override fun onError(status: Status) {}
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))
        map.setOnMapClickListener { latLng ->
            val marker = MarkerOptions().position(latLng)
            map.clear()
            map.addMarker(marker)

            val address = getAddressFromLatLng(latLng).toString()
            val x = latLng.latitude.toString()
            val y = latLng.longitude.toString()
            addressIntent(address, y, x)
            hideKeyboard()
        }
    }

    private fun updateMapWithSelectedLocation(latLng: LatLng) {
        marker?.remove()
        marker = map.addMarker(MarkerOptions().position(latLng))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

        val address = getAddressFromLatLng(latLng).toString()
        val x = latLng.latitude.toString()
        val y = latLng.longitude.toString()
        addressIntent(address, y, x)
        hideKeyboard()
    }

    private fun getAddressFromLatLng(latLng: LatLng): String? {
        val geocoder = Geocoder(this, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) addresses[0].getAddressLine(0) else null
        } catch (e: Exception) {
            null
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun addressIntent(roadAddress: String, x: String, y: String) {
        googleMapBinding.addressChooseButton.visibility = View.VISIBLE
        googleMapBinding.addressChooseButton.setOnClickListener {
            val intent = Intent(this@GoogleMap, TodoAdd::class.java).apply {
                putExtra("ADDRESS", roadAddress)
                putExtra("COORDINATE_X", x)
                putExtra("COORDINATE_Y", y)
            }
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
