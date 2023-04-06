package com.example.freewillpark

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.freewillpark.databinding.ActivityMainBinding
import com.example.freewillpark.ui.slideshow.SlideshowFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var db = Firebase.firestore
    private var userIDeric = "KeaLGRMsmc4F7p1Dgmmd"
    private var userIDquentin = "GWbg7cfSYBBFOrJVcXAU"
    private var userID = userIDeric
    private var firstNameDB = ""
    private var lastNameDB = ""
    private var mailDB = ""
    private var nbPointsDB = 0
    private var adminDB: Boolean = false
    private val viewModel: SettingSave by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val docRef: DocumentReference = db.collection("users").document(userID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    if(document.get("firstName") != null && document.get("lastName") != null && document.get("mail") != null && document.getLong("nbPoints") != null && document.getBoolean("admin") != null)
                    {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        firstNameDB = document.get("firstName") as String
                        lastNameDB = document.get("lastName") as String
                        mailDB = document.get("mail") as String
                        nbPointsDB = (document.getLong("nbPoints") as Long).toInt()
                        adminDB = document.getBoolean("admin") as Boolean
                        viewModel.setAdminBoolean(adminDB)
                        viewModel.setnbPoints(nbPointsDB)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        makeStatusBarTransparent()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { _, insets ->
            insets.consumeSystemWindowInsets()
        }
        binding.appBarMain.contentMainId.fab1.setOnClickListener { onClickOpen()}
        viewModel.setbuttonPrincipale(binding.appBarMain.contentMainId.fab1)

        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        navView.setupWithNavController(navController)
        //navView[1].setOnClickListener { Log.w(TAG, "aaaaaaaaaaaaaa") }
        navView.menu.getItem(0).setOnMenuItemClickListener { vibratePhone() }
        navView.menu.getItem(1).setOnMenuItemClickListener { vibratePhone() }
        navView.menu.getItem(2).setOnMenuItemClickListener { vibratePhone() }

        if(savedInstanceState == null)
        {
            val slideshow : Fragment = SlideshowFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.drawer_layout, slideshow)
                .commit()

            supportFragmentManager
                .beginTransaction()
                .detach(slideshow)
                .commit()
        }
    }

    private fun vibratePhone(): Boolean {
        if(viewModel.getSonBoolean())
        {
            val mediaPlayer = MediaPlayer.create(applicationContext, R.raw.buttonv2)
            mediaPlayer.setVolume(((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0), ((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0))
            mediaPlayer.start()
        }
        if(viewModel.getVibrationBoolean())
        {
            val vibrator = applicationContext?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(50)
            }
        }
        return false
    }

    private fun onClickOpen() {
        if(viewModel.getSonBoolean())
        {
            val mediaPlayer = MediaPlayer.create(applicationContext, R.raw.buttonv2)
            mediaPlayer.setVolume(((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0), ((viewModel.getVolume().toFloat() - 0) * (1 - 0) / (100 - 0) + 0))
            mediaPlayer.start()
        }
        if(viewModel.getVibrationBoolean())
        {
            val vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(50)
            }
        }
        val docRef: DocumentReference = db.collection("users").document(userID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    if(document.get("firstName") != null && document.get("lastName") != null && document.get("mail") != null && document.getLong("nbPoints") != null && document.getBoolean("admin") != null)
                    {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        firstNameDB = document.get("firstName") as String
                        lastNameDB = document.get("lastName") as String
                        mailDB = document.get("mail") as String
                        nbPointsDB = (document.getLong("nbPoints") as Long).toInt()
                        adminDB = document.getBoolean("admin") as Boolean
                        viewModel.setAdminBoolean(adminDB)
                        viewModel.setnbPoints(nbPointsDB)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        val textRecompense = "Vous avez $nbPointsDB points !"
        val spannable: Spannable = SpannableString(textRecompense)

        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#2BD598")), 10, 10 + nbPointsDB.toString().length + 7, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        val textViewMail : TextView = findViewById(R.id.mail)
        textViewMail.text = mailDB

        val textName = "Bonjour $lastNameDB $firstNameDB,"
        val textViewName : TextView = findViewById(R.id.name)
        if(adminDB)
        {
            val spannable2: Spannable = SpannableString(textName)
            spannable2.setSpan(ForegroundColorSpan(Color.parseColor("#2BD598")), 8, 10 + lastNameDB.length + firstNameDB.length - 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            textViewName.text = spannable2
        }
        else
        {
            textViewName.text = textName
        }

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