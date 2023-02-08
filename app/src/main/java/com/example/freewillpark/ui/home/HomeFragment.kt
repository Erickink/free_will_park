package com.example.freewillpark.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.freewillpark.MainActivity
import com.example.freewillpark.R
import com.example.freewillpark.databinding.FragmentHomeBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class HomeFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var _binding: FragmentHomeBinding? = null
    private var db = Firebase.firestore
    private var idDB = Vector<Int>()
    private var nbPlacesDB = Vector<Int>()
    private var latitudeDB = Vector<Double>()
    private var longitudeDB = Vector<Double>()
    private var typeDB = Vector<String>()
    private var nbCollectionDB = 0
    private var initMaps = 0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        nbCollectionDB = 0

        val docRef: CollectionReference = db.collection("emplacement")
        docRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document.get("id") != null && document.get("localisation") != null && document.get("nbPlaces") != null && document.get("type") != null)
                    {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        //idDB.add((document.getLong("id") as Long).toInt())
                        //nbPlacesDB.add((document.getLong("nbPlaces") as Long).toInt())
                        //latitudeDB.add(document.getGeoPoint("localisation")?.latitude)
                        //longitudeDB.add(document.getGeoPoint("localisation")?.longitude)
                        //typeDB.add(document.get("type") as String)
                        nbCollectionDB += 1
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

        val activity = activity as MainActivity?
        if (activity != null) {
            idDB = activity.getID()
            nbPlacesDB = activity.getNBPlace()
            latitudeDB = activity.getLatitude()
            longitudeDB = activity.getLongitude()
            typeDB = activity.getType()
            nbCollectionDB = activity.getNBCollection()
        }

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        ///////////////////////////////////////////////////////////////////////////////////////////

        repeat(nbCollectionDB) {index ->
            if (typeDB.elementAt(index) == "velo") {
                mMap.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                latitudeDB.elementAt(index),
                                longitudeDB.elementAt(index)
                            )
                        )
                        .title("Il y a ${nbPlacesDB.elementAt(index)} places disponible")
                        .snippet("pour vélos")
                )
            } else if (typeDB.elementAt(index) == "trot") {
                mMap.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                latitudeDB.elementAt(index),
                                longitudeDB.elementAt(index)
                            )
                        )
                        .title("Il y a ${nbPlacesDB.elementAt(index)} places disponible")
                        .snippet("pour trottinettes")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                )
            }
        }

        if(initMaps == 0)
        {
            // Zoom in, animating the camera.
            mMap.animateCamera(CameraUpdateFactory.zoomIn())

            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null)

            val louvre = LatLng(48.86057699448167, 2.3375864679309104)

            // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
            val cameraPosition = CameraPosition.Builder()
                .target(louvre) // Sets the center of the map to Mountain View
                .zoom(12f)            // Sets the zoom
                .build()              // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            initMaps = 1
        }
        ///////////////////////////////////////////////////////////////////////////////////////////
        /*
        // Add a marker in Sydney and move the camera
        val issy = LatLng(48.824252, 2.274621)

        mMap.addMarker(MarkerOptions().position(issy).title("Il y a 17 places disponible"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(issy))

        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn())

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null)

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        val cameraPosition = CameraPosition.Builder()
            .target(issy) // Sets the center of the map to Mountain View
            .zoom(17f)            // Sets the zoom
            .build()              // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
         */
        ///////////////////////////////////////////////////////////////////////////////////////////

        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), 177
                )
            }
            return
        }

        val mLocationRequest = LocationRequest.create() as LocationRequest
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        mMap.isMyLocationEnabled = true

        mMap.uiSettings.isRotateGesturesEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isScrollGesturesEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isTiltGesturesEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true

        mMap.uiSettings.setAllGesturesEnabled(true)

        //mMap.isTrafficEnabled = true;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}