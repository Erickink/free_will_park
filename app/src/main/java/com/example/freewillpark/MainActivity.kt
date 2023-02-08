package com.example.freewillpark

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.freewillpark.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var db = Firebase.firestore
    private var usersID = "KeaLGRMsmc4F7p1Dgmmd"
    private var firstNameDB = ""
    private var lastNameDB = ""
    private var mailDB = ""
    private var nbPointsDB = 0

    private var idDB = Vector<Int>()
    private var nbPlacesDB = Vector<Int>()
    private var latitudeDB = Vector<Double>()
    private var longitudeDB = Vector<Double>()
    private var typeDB = Vector<String>()
    private var nbCollectionDB = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        /*
        if (Build.VERSION.SDK_INT >= 21) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        } else if (Build.VERSION.SDK_INT >= 19) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //Virtual keyboard is also transparent
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }


        val w = window

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }



        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT

         */

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        makeStatusBarTransparent()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { _, insets ->
            //findViewById<FloatingActionButton>(R.id.fab).setMarginTop(insets.systemWindowInsetTop)
            insets.consumeSystemWindowInsets()
        }

        /*
        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
         */
        //fab
        binding.appBarMain.fab1.setOnClickListener {view -> onClickOpen()}


        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        navView.setupWithNavController(navController)

        /*
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController)
        navView.setupWithNavController(navController)
         */

        val docRef: DocumentReference = db.collection("users").document(usersID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    if(document.get("firstName") != null && document.get("lastName") != null && document.get("mail") != null && document.getLong("nbPoints") != null)
                    {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        firstNameDB = document.get("firstName") as String
                        lastNameDB = document.get("lastName") as String
                        mailDB = document.get("mail") as String
                        nbPointsDB = (document.getLong("nbPoints") as Long).toInt()
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        nbCollectionDB = 0
        val docRef2: CollectionReference = db.collection("emplacement")
        docRef2.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document.get("id") != null && document.get("localisation") != null && document.get("nbPlaces") != null && document.get("type") != null)
                    {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        idDB.add((document.getLong("id") as Long).toInt())
                        nbPlacesDB.add((document.getLong("nbPlaces") as Long).toInt())
                        latitudeDB.add(document.getGeoPoint("localisation")?.latitude)
                        longitudeDB.add(document.getGeoPoint("localisation")?.longitude)
                        typeDB.add(document.get("type") as String)
                        nbCollectionDB += 1
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }


    }

    /*
    override fun onSupportNavigateUp(): Boolean {
        val docRef: DocumentReference = db.collection("users").document(usersID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    if(document.get("firstName") != null && document.get("lastName") != null && document.get("mail") != null && document.getLong("nbPoints") != null)
                    {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        firstNameDB = document.get("firstName") as String
                        lastNameDB = document.get("lastName") as String
                        mailDB = document.get("mail") as String
                        nbPointsDB = (document.getLong("nbPoints") as Long).toInt()
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        nbCollectionDB = 0
        val docRef2: CollectionReference = db.collection("emplacement")
        docRef2.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document.get("id") != null && document.get("localisation") != null && document.get("nbPlaces") != null && document.get("type") != null)
                    {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        idDB.add((document.getLong("id") as Long).toInt())
                        nbPlacesDB.add((document.getLong("nbPlaces") as Long).toInt())
                        latitudeDB.add(document.getGeoPoint("localisation")?.latitude)
                        longitudeDB.add(document.getGeoPoint("localisation")?.longitude)
                        typeDB.add(document.get("type") as String)
                        nbCollectionDB += 1
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

        val navController = findNavController(R.id.nav_host_fragment_content_main)

        val textRecompense = "Vous avez $nbPointsDB points !"
        val spannable: Spannable = SpannableString(textRecompense)

        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#2BD598")), 10, 10 + nbPointsDB.toString().length + 7, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        val textViewMail : TextView = findViewById(R.id.mail)
        textViewMail.text = mailDB

        val textViewName : TextView = findViewById(R.id.name)
        textViewName.text = "Bonjour $lastNameDB $firstNameDB,"

        val textViewRecompense : TextView = findViewById(R.id.recompense)
        textViewRecompense.text = spannable

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

     */

    public fun getID(): Vector<Int> {
        return idDB
    }

    public fun getNBPlace(): Vector<Int> {
        return nbPlacesDB
    }

    public fun getLongitude(): Vector<Double> {
        return longitudeDB
    }

    public fun getLatitude(): Vector<Double> {
        return latitudeDB
    }

    public fun getType(): Vector<String> {
        return typeDB
    }

    public fun getNBCollection(): Int {
        return nbCollectionDB
    }

    private fun onClickOpen() {
        val docRef: DocumentReference = db.collection("users").document(usersID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    if(document.get("firstName") != null && document.get("lastName") != null && document.get("mail") != null && document.getLong("nbPoints") != null)
                    {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        firstNameDB = document.get("firstName") as String
                        lastNameDB = document.get("lastName") as String
                        mailDB = document.get("mail") as String
                        nbPointsDB = (document.getLong("nbPoints") as Long).toInt()
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        nbCollectionDB = 0
        val docRef2: CollectionReference = db.collection("emplacement")
        docRef2.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document.get("id") != null && document.get("localisation") != null && document.get("nbPlaces") != null && document.get("type") != null)
                    {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        idDB.add((document.getLong("id") as Long).toInt())
                        nbPlacesDB.add((document.getLong("nbPlaces") as Long).toInt())
                        latitudeDB.add(document.getGeoPoint("localisation")?.latitude)
                        longitudeDB.add(document.getGeoPoint("localisation")?.longitude)
                        typeDB.add(document.get("type") as String)
                        nbCollectionDB += 1
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }


        val textRecompense = "Vous avez $nbPointsDB points !"
        val spannable: Spannable = SpannableString(textRecompense)

        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#2BD598")), 10, 10 + nbPointsDB.toString().length + 7, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        val textViewMail : TextView = findViewById(R.id.mail)
        textViewMail.text = mailDB

        val textViewName : TextView = findViewById(R.id.name)
        textViewName.text = "Bonjour $lastNameDB $firstNameDB,"

        val textViewRecompense : TextView = findViewById(R.id.recompense)
        textViewRecompense.text = spannable
        val drawer: DrawerLayout = findViewById(R.id.drawer_layout)

        drawer.openDrawer(GravityCompat.START)
    }

    private fun Activity.makeStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                } else {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
                statusBarColor = Color.TRANSPARENT
                navigationBarColor = Color.TRANSPARENT
            }
        }
    }
}