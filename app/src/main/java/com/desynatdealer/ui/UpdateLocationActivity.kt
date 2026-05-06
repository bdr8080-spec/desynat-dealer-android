package com.desynatdealer.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.desynatdealer.R
import com.desynatdealer.databinding.ActivityUpdateLocationBinding
import com.desynatdealer.utils.CommonMethod
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.io.IOException
import java.util.Locale

class UpdateLocationActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {

    private lateinit var binding: ActivityUpdateLocationBinding
    private var mMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Location variables
    private var latitude = 28.6210
    private var longitude = 77.3812

    private var savedAddress = ""
    private var savedLat = 0.0
    private var savedLng = 0.0

    private val LOCATION_PERMISSION_REQUEST_CODE = 100

    // Address variables
    private var city = ""
    private var street = ""
    private var state = ""
    private var pincode = ""
    private var houseno = ""
    private var fullAddress = ""
    private var latitudeDisplay = 0.0
    private var longitudeDisplay = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initViews()
        initClicks()
    }

    private fun initViews() {
        CommonMethod.detectGestureMode(binding.root, binding.viewBottom)

        savedLat = intent.getDoubleExtra("savedLat", 0.0)
        savedLng = intent.getDoubleExtra("savedLng", 0.0)
        savedAddress = intent.getStringExtra("savedAddress").toString()
        Log.e("send", "receive: $savedAddress" )

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapConst) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initClicks() {
        binding.etSearch.setOnClickListener(this)
        binding.tvCurrent.setOnClickListener(this)
        binding.tvToolbar.setOnClickListener(this)
        binding.btnContinue.setOnClickListener(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap?.uiSettings?.isMyLocationButtonEnabled = false
        mMap?.isMyLocationEnabled = false

        checkLocationPermission()

        // When user moves the map
        mMap?.setOnCameraIdleListener {
            val pos = mMap?.cameraPosition?.target
            pos?.let {
                savedLat = 0.0
                savedLng = 0.0
                updateLocationText(it.latitude, it.longitude)
            }
        }
    }

    // ------------------- PERMISSION FLOW -------------------

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                if (savedLat != 0.0 && savedLng != 0.0) {
                    latitude = savedLat
                    longitude = savedLng

                    binding.etSearch.setText(savedAddress)
                    binding.tvAddress.text = savedAddress

                    moveMapToLocation(savedLat, savedLng)
                }
                else{
                    getLastKnownLocation()
                }
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) -> showPermissionDialog()

            else -> requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_required))
            .setMessage(getString(R.string.location_permission_is_required_to_detect_your_current_location))
            .setPositiveButton(getString(R.string.grant)) { _, _ -> requestLocationPermission() }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            if (savedLat != 0.0 && savedLng != 0.0) {
                latitude = savedLat
                longitude = savedLng

                binding.etSearch.setText(savedAddress)
                binding.tvAddress.text = savedAddress

                moveMapToLocation(savedLat, savedLng)
            }
            else {
                getLastKnownLocation()
            }
        } else {
            Toast.makeText(this, getString(R.string.location_permission_denied), Toast.LENGTH_LONG).show()
        }
    }

    // ------------------- GET CURRENT LOCATION -------------------

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        if (!isGPSEnabled()) {
            showGPSEnableDialog()
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    moveMapToLocation(it.latitude, it.longitude)
                }
            }
            .addOnFailureListener {
                Log.e("Location", "Error: ${it.message}")
            }
    }

    private fun isGPSEnabled(): Boolean {
        val manager = getSystemService(LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun showGPSEnableDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.enable_gps))
            .setMessage(getString(R.string.please_enable_gps_to_fetch_your_current_location))
            .setPositiveButton(getString(R.string.settings)) { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }.setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun moveMapToLocation(lat: Double, lng: Double) {
        val target = LatLng(lat, lng)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 16f))
        updateLocationText(lat, lng)
    }

    // ------------------- REVERSE GEOCODING -------------------

    private fun updateLocationText(lat: Double, lng: Double) {
        if (savedLat != 0.0 && savedLng != 0.0) {
            return
        }

        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addressList: List<Address> = geocoder.getFromLocation(lat, lng, 1) ?: return

            if (addressList.isNotEmpty()) {
                val address = addressList[0]

                fullAddress = address.getAddressLine(0) ?: ""
                city = address.locality ?: ""
                street = address.subLocality ?: ""
                state = address.adminArea ?: ""
                pincode = address.postalCode ?: ""
                houseno = address.subThoroughfare ?: ""

                latitudeDisplay = lat
                longitudeDisplay = lng

                binding.tvAddress.text = fullAddress
                binding.etSearch.setText(fullAddress)
                Log.e("send", "new: $latitudeDisplay $longitudeDisplay" )

            }
        } catch (e: IOException) {
            Log.e("Geo", "Error: ${e.message}")
        }
    }

    // ------------------- PLACE PICKER -------------------

    private fun openPlacePicker() {
        val fields = listOf(
            Place.Field.ID,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
            Place.Field.NAME
        )
        val intent =
            Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this)

        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            val place = Autocomplete.getPlaceFromIntent(data)
            place.latLng?.let {
                moveMapToLocation(it.latitude, it.longitude)
            }
            binding.etSearch.setText(place.address)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // ------------------- CLICK EVENTS -------------------

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvToolbar -> finish()
            R.id.tvCurrent ->{
                savedLat = 0.0
                savedLng = 0.0
                getLastKnownLocation()}
            R.id.etSearch -> {
                savedLat = 0.0
                savedLng = 0.0
                openPlacePicker()
            }
            R.id.btnContinue -> sendBackResult()
        }
    }

    private fun sendBackResult() {
        if (latitudeDisplay==0.0) return

        val i = Intent()
        i.putExtra("city", city)
        i.putExtra("street", street)
        i.putExtra("state", state)
        i.putExtra("pincode", pincode)
        i.putExtra("houseno", houseno)
        i.putExtra("addressH", fullAddress)
        i.putExtra("lat", latitudeDisplay)
        i.putExtra("lng", longitudeDisplay)

        setResult(RESULT_OK, i)
        finish()
    }
}
