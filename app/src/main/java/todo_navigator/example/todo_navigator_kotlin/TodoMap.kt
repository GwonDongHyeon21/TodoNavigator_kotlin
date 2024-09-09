package todo_navigator.example.todo_navigator_kotlin

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import todo_navigator.example.todo_navigator_kotlin.adapter.AddressAdapter
import todo_navigator.example.todo_navigator_kotlin.api.AddressResponse
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.widget.CompassView
import com.naver.maps.map.widget.LocationButtonView
import com.naver.maps.map.widget.ZoomControlView
import java.util.Locale

class TodoMap : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var naverMap: NaverMap
    private lateinit var addressAdapter: AddressAdapter
    private lateinit var locationSource: FusedLocationSource
    private var marker: Marker? = null
    private val addressInput: EditText by lazy {
        findViewById(R.id.addressInput)
    }
    private val searchButton: Button by lazy {
        findViewById(R.id.searchButton)
    }
    private val addressList: RecyclerView by lazy {
        findViewById(R.id.addressList)
    }
    private val addressChooseButton: FloatingActionButton by lazy {
        findViewById(R.id.addressChooseButton)
    }
    private val currentLocationButton: LocationButtonView by lazy {
        findViewById(R.id.currentLocationButton)
    }
    private val zoomController: ZoomControlView by lazy {
        findViewById(R.id.mapZoomController)
    }
    private val compassButton: CompassView by lazy {
        findViewById(R.id.compassButton)
    }
    private var addressCheck = true

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                supportFragmentManager.beginTransaction().add(R.id.map, it).commit()
            }

        mapFragment.getMapAsync(this)

        addressList.layoutManager = LinearLayoutManager(this)

        addressInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(addressText: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val address = addressText.toString()
                if (address.isNotEmpty()) {
                    searchAddress(address)
                } else {
                    addressList.visibility = android.view.View.GONE
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        addressInput.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEARCH || action == EditorInfo.IME_ACTION_DONE) {
                searchButton.performClick()
                true
            } else {
                false
            }
        }

        searchButton.setOnClickListener {
            val address = addressInput.text.toString()
            if (address.isNotEmpty()) {
                if (addressCheck) {
                    Toast.makeText(this@TodoMap, "주소를 선택하세요.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@TodoMap, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "주소를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        map.mapType = NaverMap.MapType.Basic

        map.maxZoom = 18.0
        map.minZoom = 10.0

        val uiSetting = map.uiSettings
        uiSetting.apply {
            isLocationButtonEnabled = false
            isZoomControlEnabled = false
            isCompassEnabled = false
        }

        currentLocationButton.map = naverMap
        zoomController.map = naverMap
        compassButton.map = naverMap

        locationSource = FusedLocationSource(this@TodoMap, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        map.setOnMapClickListener { _, latLng ->
            addMarker(latLng)
            getAddress(latLng)?.let { roadAddress ->
                addressIntent(
                    roadAddress,
                    latLng.longitude.toString(),
                    latLng.latitude.toString()
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }

        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) {
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
    }

    private fun searchAddress(address: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocationName(address, 5)
            if (!addresses.isNullOrEmpty()) {
                addressList.visibility = android.view.View.VISIBLE
                val addressesInfo = addresses.map { address ->
                    AddressResponse.AddressItem(
                        roadAddress = address.getAddressLine(0) ?: "",
                        jibunAddress = address.getAddressLine(1) ?: "",
                        x = address.longitude,
                        y = address.latitude,
                    )
                }

                addressAdapter = AddressAdapter(addressesInfo) { selectedAddress ->
                    chooseAddress(selectedAddress)
                }
                addressList.adapter = addressAdapter
                addressCheck = true
            } else {
                addressList.visibility = android.view.View.GONE
                addressCheck = false
            }
        } catch (e: Exception) {
            addressList.visibility = android.view.View.GONE
            Toast.makeText(this@TodoMap, "주소 검색에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun chooseAddress(selectedAddress: AddressResponse.AddressItem) {
        val latLng = LatLng(selectedAddress.y, selectedAddress.x)
        addMarker(latLng)

        addressList.visibility = android.view.View.GONE

        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(addressInput.windowToken, 0)

        addressIntent(
            selectedAddress.roadAddress,
            selectedAddress.x.toString(),
            selectedAddress.y.toString(),
        )
    }

    private fun getAddress(latLng: LatLng): String? {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0].getAddressLine(0)
                return address
            } else {
                Toast.makeText(this, "주소를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "주소를 가져오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
        return ""
    }

    private fun addressIntent(roadAddress: String, x: String, y: String) {
        addressChooseButton.visibility = android.view.View.VISIBLE
        addressChooseButton.setOnClickListener {
            val intent = Intent(this@TodoMap, TodoAdd::class.java).apply {
                putExtra("ADDRESS", roadAddress)
                putExtra("COORDINATE_X", x)
                putExtra("COORDINATE_Y", y)
            }
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun addMarker(latLng: LatLng) {
        marker?.map = null
        marker = Marker().apply {
            position = latLng
            map = naverMap
        }

        naverMap.moveCamera(
            CameraUpdate.scrollTo(latLng)
                .animate(CameraAnimation.Linear, 500)
        )
    }
}
