package com.example.freewillpark.ui.home

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.RelativeLayout.LayoutParams
import android.widget.Toast
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.example.freewillpark.R
import com.example.freewillpark.SettingSave
import com.example.freewillpark.databinding.FragmentHomeBinding
import com.example.freewillpark.databinding.NumberEmpBinding
import com.example.freewillpark.databinding.SwitchTrotBinding
import com.example.freewillpark.databinding.SwitchVeloBinding
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class HomeFragment : Fragment(), OnMapReadyCallback, OnMarkerClickListener, OnMapClickListener, OnMapLongClickListener {
    private lateinit var mMap: GoogleMap
    private var _binding: FragmentHomeBinding? = null
    private var db = Firebase.firestore
    private var nbPlacesDB = Vector<Int>()
    private var latitudeDB = Vector<Double>()
    private var longitudeDB = Vector<Double>()
    private var typeDB = Vector<String>()
    private var myMarker = Vector<Marker>()
    private var addedMarker: Marker? = null
    private var addedMarkerB: Boolean = false
    private var onMarkerB: Boolean = false
    private var nbCollectionDB = 0
    private var initMaps = 0
    private lateinit var currentLocation: LatLng
    private var isCurrentLocation: Boolean = false
    private var typeMap = 0
    private val viewModel: SettingSave by activityViewModels()
    private var typeB: Boolean = false
    private var veloB: Boolean = false
    private var trotB: Boolean = false
    private var adminB: Boolean = false
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var fabST: FloatingActionButton
    private lateinit var fabA: FloatingActionButton
    private lateinit var fabDelete: FloatingActionButton
    private lateinit var numberBinding: NumberEmpBinding
    private lateinit var veloBinding: SwitchVeloBinding
    private lateinit var trotBinding: SwitchTrotBinding
    private var stringET: String = ""
    private var numberET: Int = 0
    private var veloBooleanSwitch = false
    private var trotBooleanSwitch = false
    private lateinit var toast: Toast
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT)

        mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.fab2.setOnClickListener { onClickMyLocation() }
        binding.fab3.setOnClickListener { onClickTypeMap() }
        binding.fab4.setOnClickListener { onClickStreetView() }
        binding.fabA.setOnClickListener { onClickAddToDB() }
        binding.fabDelete.setOnClickListener { onClickDelete() }

        fabST = binding.fab4
        fabST.visibility = View.GONE

        adminB = viewModel.getAdminBoolean()

        fabA = binding.fabA
        fabA.visibility = View.GONE

        fabDelete = binding.fabDelete
        fabDelete.visibility = View.GONE

        numberBinding = binding.number
        numberBinding.root.visibility = View.GONE

        veloBinding = binding.switchVelo
        veloBinding.root.visibility = View.GONE

        trotBinding = binding.switchTrot
        trotBinding.root.visibility = View.GONE

        veloBinding.switchvelo.setOnClickListener {
            run {
                if(viewModel.getSonBoolean())
                {
                    val mediaPlayer = MediaPlayer.create(context, R.raw.buttonv2)
                    mediaPlayer.setVolume(((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0), ((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0))
                    mediaPlayer.start()
                }
                if(viewModel.getVibrationBoolean())
                {
                    val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (Build.VERSION.SDK_INT >= 26) {
                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        vibrator.vibrate(50)
                    }
                }
            }
        }
        veloBinding.switchvelo.setOnCheckedChangeListener { _, b ->
            run {
                veloBooleanSwitch = b
            }
        }

        trotBinding.switchtrot.setOnClickListener {
            run {
                if(viewModel.getSonBoolean())
                {
                    val mediaPlayer = MediaPlayer.create(context, R.raw.buttonv2)
                    mediaPlayer.setVolume(((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0), ((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0))
                    mediaPlayer.start()
                }
                if(viewModel.getVibrationBoolean())
                {
                    val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (Build.VERSION.SDK_INT >= 26) {
                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        vibrator.vibrate(50)
                    }
                }
            }
        }
        trotBinding.switchtrot.setOnCheckedChangeListener { _, b ->
            run {
                trotBooleanSwitch = b
            }
        }
        return binding.root
    }

    private fun onClickDelete() {
        if(viewModel.getSonBoolean())
        {
            val mediaPlayer = MediaPlayer.create(context, R.raw.buttonv2)
            mediaPlayer.setVolume(((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0), ((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0))
            mediaPlayer.start()
        }
        if(viewModel.getVibrationBoolean())
        {
            val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(50)
            }
        }
        val snip: String = viewModel.getlastMarked().snippet.toString()
        val title: String = viewModel.getlastMarked().title.toString()

        var nbPlaces: Int
        var type = ""
        val lat: Double = viewModel.getlastMarked().position.latitude
        val long: Double = viewModel.getlastMarked().position.longitude

        if(snip == "pour trottinettes")
        {
            type = "trot"
        }
        else if(snip == "pour vélos")
        {
            type = "velo"
        }

        val arrOfStr: List<String> = title.split(" ")
        nbPlaces = Integer.parseInt(arrOfStr.elementAt(3))

        db.collection("emplacement").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (document.get("localisation") != null && document.get("nbPlaces") != null && document.get("type") != null) {
                        if (((document.getLong("nbPlaces") as Long).toInt() == nbPlaces) && (document.getGeoPoint("localisation")?.latitude == lat) && (document.getGeoPoint("localisation")?.longitude == long) && (document.get("type") as String == type))
                        {
                            db.collection("emplacement").document(document.id)
                                .delete()
                                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!")
                                    val text = "Emplacement supprimé !"
                                    toast.cancel()
                                    toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
                                    toast.show()

                                    parentFragmentManager.beginTransaction()
                                        .detach(this)
                                        .commit()

                                    parentFragmentManager.beginTransaction()
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                        .attach(this)
                                        .commit()
                                }
                                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
                        }
                    }
                }
            }
        .addOnFailureListener { exception ->
            Log.w(TAG, "Error getting documents.", exception)
        }
    }

    private fun onClickAddToDB() {
        if(viewModel.getSonBoolean())
        {
            val mediaPlayer = MediaPlayer.create(context, R.raw.buttonv2)
            mediaPlayer.setVolume(((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0), ((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0))
            mediaPlayer.start()
        }
        if(viewModel.getVibrationBoolean())
        {
            val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(50)
            }
        }
        stringET = numberBinding.etnumber.text.toString()
        if(!veloBooleanSwitch && !trotBooleanSwitch)
        {
            val text = "Veuillez selectionner Vélo et/ou Trottinette"
            toast.cancel()
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
            toast.show()
        }
        else
        {
            if(stringET == "" || stringET == null)
            {
                val text = "Veuillez selectonner une valeur pour le nombre de places"
                toast.cancel()
                toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
                toast.show()
            }
            else
            {
                numberET = Integer.parseInt(stringET)
                if(numberET == 0)
                {
                    val text = "Veuillez selectonner une valeur autre que 0 pour le nombre de places"
                    toast.cancel()
                    toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
                    toast.show()
                }
                else
                {
                    val animationduration = resources.getInteger(android.R.integer.config_shortAnimTime)
                    if(veloBooleanSwitch)
                    {
                        val data = hashMapOf(
                            "localisation" to GeoPoint(addedMarker!!.position.latitude, addedMarker!!.position.longitude),
                            "nbPlaces" to numberET,
                            "type" to "velo"
                        )

                        db.collection("emplacement")
                            .add(data)
                            .addOnSuccessListener { documentReference ->
                                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                                val text = "Emplacement ajouté !"
                                toast.cancel()
                                toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
                                toast.show()

                                if(addedMarkerB)
                                {
                                    addedMarker?.remove()
                                    activity?.hideSoftKeyboard()
                                    addedMarkerB = false
                                    if(fabA.visibility != View.GONE) {
                                        fabA.apply {
                                            animate()
                                                .alpha(0f)
                                                .setListener(object : AnimatorListenerAdapter() {
                                                    override fun onAnimationEnd(animation: Animator?) {
                                                        fabA.visibility = View.GONE
                                                    }
                                                })
                                                .duration = animationduration.toLong()
                                        }
                                    }
                                    if(numberBinding.root.visibility != View.GONE) {
                                        numberBinding.root.apply {
                                            animate()
                                                .alpha(0f)
                                                .setListener(object : AnimatorListenerAdapter() {
                                                    override fun onAnimationEnd(animation: Animator?) {
                                                        numberBinding.root.visibility = View.GONE
                                                    }
                                                })
                                                .duration = animationduration.toLong()
                                        }
                                    }
                                    if(veloBinding.root.visibility != View.GONE) {
                                        veloBinding.root.apply {
                                            animate()
                                                .alpha(0f)
                                                .setListener(object : AnimatorListenerAdapter() {
                                                    override fun onAnimationEnd(animation: Animator?) {
                                                        veloBinding.root.visibility = View.GONE
                                                    }
                                                })
                                                .duration = animationduration.toLong()
                                        }
                                    }
                                    if(trotBinding.root.visibility != View.GONE) {
                                        trotBinding.root.apply {
                                            animate()
                                                .alpha(0f)
                                                .setListener(object : AnimatorListenerAdapter() {
                                                    override fun onAnimationEnd(animation: Animator?) {
                                                        trotBinding.root.visibility = View.GONE
                                                    }
                                                })
                                                .duration = animationduration.toLong()
                                        }
                                    }
                                    parentFragmentManager.beginTransaction()
                                        .detach(this)
                                        .commit()

                                    parentFragmentManager.beginTransaction()
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                        .attach(this)
                                        .commit()
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }
                    }
                    if(trotBooleanSwitch)
                    {
                        val data = hashMapOf(
                            "localisation" to GeoPoint(addedMarker!!.position.latitude, addedMarker!!.position.longitude),
                            "nbPlaces" to numberET,
                            "type" to "trot"
                        )

                        db.collection("emplacement")
                            .add(data)
                            .addOnSuccessListener { documentReference ->
                                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                                val text = "Emplacement ajouté !"
                                toast.cancel()
                                toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
                                toast.show()

                                if(addedMarkerB)
                                {
                                    addedMarker?.remove()
                                    activity?.hideSoftKeyboard()
                                    addedMarkerB = false
                                    if(fabA.visibility != View.GONE) {
                                        fabA.apply {
                                            animate()
                                                .alpha(0f)
                                                .setListener(object : AnimatorListenerAdapter() {
                                                    override fun onAnimationEnd(animation: Animator?) {
                                                        fabA.visibility = View.GONE
                                                    }
                                                })
                                                .duration = animationduration.toLong()
                                        }
                                    }
                                    if(numberBinding.root.visibility != View.GONE) {
                                        numberBinding.root.apply {
                                            animate()
                                                .alpha(0f)
                                                .setListener(object : AnimatorListenerAdapter() {
                                                    override fun onAnimationEnd(animation: Animator?) {
                                                        numberBinding.root.visibility = View.GONE
                                                    }
                                                })
                                                .duration = animationduration.toLong()
                                        }
                                    }
                                    if(veloBinding.root.visibility != View.GONE) {
                                        veloBinding.root.apply {
                                            animate()
                                                .alpha(0f)
                                                .setListener(object : AnimatorListenerAdapter() {
                                                    override fun onAnimationEnd(animation: Animator?) {
                                                        veloBinding.root.visibility = View.GONE
                                                    }
                                                })
                                                .duration = animationduration.toLong()
                                        }
                                    }
                                    if(trotBinding.root.visibility != View.GONE) {
                                        trotBinding.root.apply {
                                            animate()
                                                .alpha(0f)
                                                .setListener(object : AnimatorListenerAdapter() {
                                                    override fun onAnimationEnd(animation: Animator?) {
                                                        trotBinding.root.visibility = View.GONE
                                                    }
                                                })
                                                .duration = animationduration.toLong()
                                        }
                                    }
                                    parentFragmentManager.beginTransaction()
                                        .detach(this)
                                        .commit()

                                    parentFragmentManager.beginTransaction()
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                        .attach(this)
                                        .commit()
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }
                    }
                }
            }
        }
    }

    private fun onClickStreetView() {
        if(viewModel.getSonBoolean())
        {
            val mediaPlayer = MediaPlayer.create(context, R.raw.buttonv2)
            mediaPlayer.setVolume(((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0), ((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0))
            mediaPlayer.start()
        }
        if(viewModel.getVibrationBoolean())
        {
            val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(50)
            }
        }
        val street = StreetFragment()
        parentFragmentManager
            .beginTransaction()
            .replace(((view as ViewGroup).parent as View).id, street)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .commit()
    }

    private fun onClickTypeMap() {
        if(viewModel.getSonBoolean())
        {
            val mediaPlayer = MediaPlayer.create(context, R.raw.buttonv2)
            mediaPlayer.setVolume(((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0), ((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0))
            mediaPlayer.start()
        }
        if(viewModel.getVibrationBoolean())
        {
            val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(50)
            }
        }
        if(typeMap == 0)
        {
            typeMap = 1
            mMap.mapType = MAP_TYPE_HYBRID
        }
        else if(typeMap == 1)
        {
            typeMap = 0
            mMap.mapType = MAP_TYPE_NORMAL
        }
    }

    private fun onClickMyLocation() {
        if(viewModel.getSonBoolean())
        {
            val mediaPlayer = MediaPlayer.create(context, R.raw.buttonv2)
            mediaPlayer.setVolume(((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0), ((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0))
            mediaPlayer.start()
        }
        if(viewModel.getVibrationBoolean())
        {
            val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(50)
            }
        }
        val animationduration = resources.getInteger(android.R.integer.config_shortAnimTime)
        if(fabDelete.visibility != View.GONE) {
            fabDelete.apply {
                animate()
                    .alpha(0f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            fabDelete.visibility = View.GONE
                        }
                    })
                    .duration = animationduration.toLong()
            }
        }
        if(addedMarkerB)
        {
            addedMarker?.remove()
            activity?.hideSoftKeyboard()
            addedMarkerB = false
            if(fabA.visibility != View.GONE) {
                fabA.apply {
                    animate()
                        .alpha(0f)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                fabA.visibility = View.GONE
                            }
                        })
                        .duration = animationduration.toLong()
                }
            }
            if(numberBinding.root.visibility != View.GONE) {
                numberBinding.root.apply {
                    animate()
                        .alpha(0f)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                numberBinding.root.visibility = View.GONE
                            }
                        })
                        .duration = animationduration.toLong()
                }
            }
            if(veloBinding.root.visibility != View.GONE) {
                veloBinding.root.apply {
                    animate()
                        .alpha(0f)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                veloBinding.root.visibility = View.GONE
                            }
                        })
                        .duration = animationduration.toLong()
                }
            }
            if(trotBinding.root.visibility != View.GONE) {
                trotBinding.root.apply {
                    animate()
                        .alpha(0f)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                trotBinding.root.visibility = View.GONE
                            }
                        })
                        .duration = animationduration.toLong()
                }
            }
        }
        val louvre = LatLng(48.86057699448167, 2.3375864679309104)
        if(isCurrentLocation)
        {
            mMap.animateCamera(CameraUpdateFactory.zoomIn())
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null)
            val cameraPosition = CameraPosition.Builder()
                .target(currentLocation)
                .zoom(15f)
                .build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            onMarkerB = true
            if(fabST.visibility != View.VISIBLE)
            {
                fabST.apply {
                    alpha = 0f
                    visibility = View.VISIBLE
                    animate()
                        .alpha(1f)
                        .setListener(null)
                        .duration = animationduration.toLong()
                }
            }
            viewModel.setlastMarkedLL(currentLocation)
            if(addedMarkerB)
            {
                addedMarker!!.remove()
                addedMarkerB = false
            }
        }
        else
        {
            mMap.animateCamera(CameraUpdateFactory.zoomIn())
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null)
            val cameraPosition = CameraPosition.Builder()
                .target(louvre)
                .zoom(12f)
                .build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        onMarkerB = false
        addedMarkerB = false

        fabST.visibility = View.GONE

        if(mapFragment.requireView().findViewById<View>(Integer.parseInt("1")) != null)
        {
            val locationCompass: View = (mapFragment.requireView().findViewById<View?>(Integer.parseInt("1")).parent as View).findViewById(Integer.parseInt("5"))
            val layoutParams: LayoutParams = locationCompass.layoutParams as LayoutParams
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, 0)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.setMargins(0, 0, 0, 280)
            layoutParams.marginEnd = 30
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
                .target(louvre)       // Sets the center of the map to Mountain View
                .zoom(12f)            // Sets the zoom
                .build()              // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            googleMap.mapType = MAP_TYPE_NORMAL
            initMaps = 1
        }

        typeB = viewModel.getTypeBoolean()
        veloB = viewModel.getVeloBoolean()
        trotB = viewModel.getTrotBoolean()
        adminB = viewModel.getAdminBoolean()

        mMap.clear()
        myMarker.removeAllElements()
        if(typeB)
        {
            val docRef1: CollectionReference = db.collection("emplacement")
            docRef1.get()
                .addOnSuccessListener { result ->
                    nbPlacesDB.removeAllElements()
                    latitudeDB.removeAllElements()
                    longitudeDB.removeAllElements()
                    typeDB.removeAllElements()
                    nbCollectionDB = 0
                    for (document in result) {
                        if (document.get("localisation") != null && document.get("nbPlaces") != null && document.get("type") != null) {
                            Log.d(TAG, "${document.id} => ${document.data}")
                            nbPlacesDB.add((document.getLong("nbPlaces") as Long).toInt())
                            latitudeDB.add(document.getGeoPoint("localisation")?.latitude)
                            longitudeDB.add(document.getGeoPoint("localisation")?.longitude)
                            typeDB.add(document.get("type") as String)
                            nbCollectionDB += 1
                        }
                    }
                    repeat(nbCollectionDB) { index ->
                        if(veloB)
                        {
                            if (typeDB.elementAt(index) == "velo") {
                                myMarker.removeAllElements()
                                myMarker.add(
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
                                )
                            }
                        }
                        if(trotB)
                        {
                            if (typeDB.elementAt(index) == "trot") {
                                myMarker.removeAllElements()
                                myMarker.add(
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
                                )
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }
        }

        mMap.setOnMarkerClickListener(this)
        mMap.setOnMapClickListener(this)
        mMap.setOnMapLongClickListener(this)

        /*
        mMap.setOnInfoWindowClickListener {
            run {
                if(viewModel.getSonBoolean())
                {
                    val mediaPlayer = MediaPlayer.create(context, R.raw.buttonv2)
                    mediaPlayer.setVolume(((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0), ((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0))
                    mediaPlayer.start()
                }
                if(viewModel.getVibrationBoolean())
                {
                    val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (Build.VERSION.SDK_INT >= 26) {
                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        vibrator.vibrate(50)
                    }
                }
            }
        }
         */

        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

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

        val mLocationRequest = LocationRequest.create()
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        client.checkLocationSettings(builder.build())

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

        mMap.setOnMyLocationChangeListener { location ->
            if (location != null) {
                isCurrentLocation = true
                currentLocation = LatLng(location.latitude, location.longitude)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if(viewModel.getSonBoolean())
        {
            val mediaPlayer = MediaPlayer.create(context, R.raw.buttonv2)
            mediaPlayer.setVolume(((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0), ((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0))
            mediaPlayer.start()
        }
        if(viewModel.getVibrationBoolean())
        {
            val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(50)
            }
        }
        onMarkerB = true
        val animationduration = resources.getInteger(android.R.integer.config_shortAnimTime)
        if(fabST.visibility != View.VISIBLE)
        {
            fabST.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .setListener(null)
                    .duration = animationduration.toLong()
            }
        }
        if(marker != addedMarker && adminB)
        {
            if(fabDelete.visibility != View.VISIBLE)
            {
                fabDelete.apply {
                    alpha = 0f
                    visibility = View.VISIBLE
                    animate()
                        .alpha(1f)
                        .setListener(null)
                        .duration = animationduration.toLong()
                }
            }
        }
        viewModel.setlastMarkedLL(marker.position)
        viewModel.setlastMarked(marker)
        if(marker != addedMarker && addedMarkerB)
        {
            addedMarker?.remove()
            addedMarkerB = false
            if(fabA.visibility != View.GONE) {
                fabA.apply {
                    animate()
                        .alpha(0f)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                fabA.visibility = View.GONE
                            }
                        })
                        .duration = animationduration.toLong()
                }
            }
            if(numberBinding.root.visibility != View.GONE) {
                numberBinding.root.apply {
                    animate()
                        .alpha(0f)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                numberBinding.root.visibility = View.GONE
                            }
                        })
                        .duration = animationduration.toLong()
                }
            }
            if(veloBinding.root.visibility != View.GONE) {
                veloBinding.root.apply {
                    animate()
                        .alpha(0f)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                veloBinding.root.visibility = View.GONE
                            }
                        })
                        .duration = animationduration.toLong()
                }
            }
            if(trotBinding.root.visibility != View.GONE) {
                trotBinding.root.apply {
                    animate()
                        .alpha(0f)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                trotBinding.root.visibility = View.GONE
                            }
                        })
                        .duration = animationduration.toLong()
                }
            }
        }
        return false
    }

    override fun onMapClick(latlng: LatLng) {
        onMarkerB = false
        val animationduration = resources.getInteger(android.R.integer.config_shortAnimTime)
        if(fabST.visibility != View.GONE) {
            fabST.apply {
                animate()
                    .alpha(0f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            fabST.visibility = View.GONE
                        }
                    })
                    .duration = animationduration.toLong()
            }
        }
        if(fabDelete.visibility != View.GONE) {
            fabDelete.apply {
                animate()
                    .alpha(0f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            fabDelete.visibility = View.GONE
                        }
                    })
                    .duration = animationduration.toLong()
            }
        }
        if(addedMarkerB)
        {
            if(viewModel.getVibrationBoolean())
            {
                val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator.vibrate(50)
                }
            }
            addedMarker?.remove()
            activity?.hideSoftKeyboard()
            addedMarkerB = false
            if(fabA.visibility != View.GONE) {
                fabA.apply {
                    animate()
                        .alpha(0f)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                fabA.visibility = View.GONE
                            }
                        })
                        .duration = animationduration.toLong()
                }
            }
            if(numberBinding.root.visibility != View.GONE) {
                numberBinding.root.apply {
                    animate()
                        .alpha(0f)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                numberBinding.root.visibility = View.GONE
                            }
                        })
                        .duration = animationduration.toLong()
                }
            }
            if(veloBinding.root.visibility != View.GONE) {
                veloBinding.root.apply {
                    animate()
                        .alpha(0f)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                veloBinding.root.visibility = View.GONE
                            }
                        })
                        .duration = animationduration.toLong()
                }
            }
            if(trotBinding.root.visibility != View.GONE) {
                trotBinding.root.apply {
                    animate()
                        .alpha(0f)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                trotBinding.root.visibility = View.GONE
                            }
                        })
                        .duration = animationduration.toLong()
                }
            }
        }
    }

    override fun onMapLongClick(latlng: LatLng) {
        veloBinding.switchvelo.isChecked = false
        trotBinding.switchtrot.isChecked = false
        numberBinding.etnumber.text = null
        if(!addedMarkerB && !onMarkerB && adminB)
        {
            if(viewModel.getVibrationBoolean())
            {
                val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator.vibrate(50)
                }
            }
            addedMarker = mMap.addMarker(MarkerOptions()
                .position(latlng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )!!
            addedMarkerB = true
            val animationduration = resources.getInteger(android.R.integer.config_shortAnimTime)
            if(fabA.visibility != View.VISIBLE)
            {
                fabA.apply {
                    alpha = 0f
                    visibility = View.VISIBLE
                    animate()
                        .alpha(1f)
                        .setListener(null)
                        .duration = animationduration.toLong()
                }
            }
            if(numberBinding.root.visibility != View.VISIBLE)
            {
                numberBinding.root.apply {
                    alpha = 0f
                    visibility = View.VISIBLE
                    animate()
                        .alpha(1f)
                        .setListener(null)
                        .duration = animationduration.toLong()
                }
            }
            if(veloBinding.root.visibility != View.VISIBLE)
            {
                veloBinding.root.apply {
                    alpha = 0f
                    visibility = View.VISIBLE
                    animate()
                        .alpha(1f)
                        .setListener(null)
                        .duration = animationduration.toLong()
                }
            }
            if(trotBinding.root.visibility != View.VISIBLE)
            {
                trotBinding.root.apply {
                    alpha = 0f
                    visibility = View.VISIBLE
                    animate()
                        .alpha(1f)
                        .setListener(null)
                        .duration = animationduration.toLong()
                }
            }
        }
    }

    private fun Activity.hideSoftKeyboard() {
        currentFocus?.let {
            val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}