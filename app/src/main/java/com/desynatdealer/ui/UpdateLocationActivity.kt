package com.desynatdealer.ui


import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.desynatdealer.R
import com.desynatdealer.databinding.ActivityUpdateLocationBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.GPSTracker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.io.IOException
import java.util.Locale

class UpdateLocationActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {
    private lateinit var binding: ActivityUpdateLocationBinding
    private var mMap: GoogleMap?=null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var gpsTracker: GPSTracker? = null
    private var currentMarker: Marker? = null
    private var latitude: Double = 28.6210
    private var longitude: Double = 77.3812
    private val LOCATION_PERMISSION_REQUEST_CODE = 100

    private var city: String = ""
    private var street: String = ""
    private var state: String = ""
    private var pincode: String = ""
    private var addressH: String = ""
    private var houseno: String = ""
    private var mLat: String = ""
    private var mLong: String = ""
    private var latitudeDisplay: String = ""
    private var longitudeDisplay: String = ""
    private var addPlace: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUpdateLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        gpsTracker = GPSTracker(this)

        initView()
        initControl()
    }

    private fun initView() {
        CommonMethod.detectGestureMode(binding?.root!!,binding?.viewBottom!!)

        binding?.toolbar?.tvToolbar?.text = "Add Address"

        checkLocationPermission()
        val supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.mapConst) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
    }

    private fun initControl() {
        binding.tvSearchLocation.setOnClickListener(this)
        binding.tvCurrentLoc.setOnClickListener(this)
        binding.toolbar.tvToolbar.setOnClickListener(this)
        binding.btnConfirm.setOnClickListener(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLastKnownLocation()
        } else {
            requestLocationPermission()
        }

        mMap?.uiSettings?.isMyLocationButtonEnabled = false
        mMap?.isMyLocationEnabled = false

        // Listen for map movements (scroll)

        mMap?.setOnCameraIdleListener {
            val center = mMap?.cameraPosition?.target
            center?.latitude?.let { center?.longitude?.let { it1 -> updateLocationText(it, it1) } }
        }
    }

    fun updateNewLocation(newLat: Double, newLng: Double) {
        latitude = newLat
        longitude = newLng
        moveMapToLocation(latitude, longitude)
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getLastKnownLocation()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                showPermissionRationale()
            }

            else -> {
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle("Location Permission Required")
            .setMessage("We need access to your location to show your current position on the map.")
            .setPositiveButton("Grant") { _, _ -> requestLocationPermission() }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation()
            } else {
                Toast.makeText(this, "Location permission denied!", Toast.LENGTH_LONG).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (!isGPSEnabled()) {
                showEnableGPSDialog()
                return
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude

                    //  latitude = mLat.toDouble()//location.latitude
                    //longitude = mLong.toDouble()//location.longitude

                    moveMapToLocation(latitude, longitude)
                } else {
                    Log.e("MapsDebug", "Failed to get location: Null Location")
                }
            }.addOnFailureListener {
                Log.e("MapsDebug", "Failed to get location: ${it.message}")
            }
        }
    }


    private fun moveMapToLocation(lat: Double, lng: Double) {
        val newLocation = LatLng(lat, lng)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 15F))

        // Remove previous marker
       /* currentMarker?.remove()
        val customMarkerIcon = getBitmapDescriptorFromVector(R.drawable.ic_map)

        currentMarker = mMap.addMarker(
            MarkerOptions()
                .position(newLocation)
                .title("Your Location")
                .icon(customMarkerIcon) // Set custom marker icon
        )*/

        // Update text with address
        updateLocationText(lat, lng)
    }

    // Convert Drawable to BitmapDescriptor
    private fun getBitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable: Drawable? = ContextCompat.getDrawable(this, vectorResId)
        vectorDrawable?.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        val bitmap = Bitmap.createBitmap(
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun updateLocationText(lat: Double, lng: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(lat, lng, 1)!!
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val fullAddress = address.getAddressLine(0) ?: "Unknown Location"
                val locality = address.locality ?: "Unknown Locality"   // City or Town
                val subLocality = address.subLocality ?: "Unknown Sublocality" // Area inside city
                val stateh = address.adminArea ?: "N/A"
                val postalCode = address.postalCode ?: "Unknown Pincode" // Zip Code
                val houseNumber = address.subThoroughfare ?: address.premises ?: address.featureName
                binding.tvAddress.text = fullAddress
                binding.tvSearchLocation.text = fullAddress
                city = locality
                street = subLocality
                state = stateh
                pincode = postalCode
                houseno = houseNumber
                addressH = fullAddress
                latitudeDisplay = address.latitude.toString()
                longitudeDisplay = address.longitude.toString()
            } else {
                binding.tvAddress.text = "No address found"
            }
        } catch (e: IOException) {
            Log.e("MapsDebug", "Geocoder failed: ${e.message}")
            binding.tvAddress.text = "Error fetching address"
        }
    }


    private fun isGPSEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun showEnableGPSDialog() {
        AlertDialog.Builder(this)
            .setTitle("Enable GPS")
            .setMessage("GPS is required for getting your current location. Please enable it.")
            .setPositiveButton("Settings") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvToolbar -> finish()
            R.id.tvSearchLocation -> openPlacePicker()
            R.id.tvCurrentLoc -> getLastKnownLocation()
            R.id.btnConfirm -> {
                if (latitudeDisplay!=""&&longitudeDisplay!=""){
                    val resultIntent = Intent()
                    resultIntent.putExtra("city", city)
                    resultIntent.putExtra("street", street)
                    resultIntent.putExtra("state", state)
                    resultIntent.putExtra("pincode", pincode)
                    resultIntent.putExtra("houseno", houseno)
                    resultIntent.putExtra("addressH", addressH)
                    resultIntent.putExtra("latitudeD", latitudeDisplay)
                    resultIntent.putExtra("longitudeD", longitudeDisplay)
                    city.lowercase()
                    setResult(RESULT_OK, resultIntent)
                }

                finish()


            }
        }
    }

    object searchLatLng {
        var latitude: Double? = 0.0
        var longitude: Double? = 0.0
        var totalDistance: String = ""
    }

    private fun openPlacePicker() {
        val fields: List<Place.Field> = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS
        )
        val intent =
            Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this)
        startActivityForResult(intent, 1001!!)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(data!!)
            latitude = place.latLng?.latitude ?: latitude
            longitude = place.latLng?.longitude ?: longitude
            val currentLocation = LatLng(latitude, longitude)
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15F))
            binding.tvSearchLocation.text = place.address
            binding.tvAddress.text = place.address
            moveMapToLocation(latitude, longitude)
        }
    }
}
