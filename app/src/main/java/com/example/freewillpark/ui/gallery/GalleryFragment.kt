package com.example.freewillpark.ui.gallery

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.Constraints
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.freewillpark.R
import com.example.freewillpark.SettingSave
import com.example.freewillpark.databinding.FragmentGalleryBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class GalleryFragment : Fragment() {

    private var db = Firebase.firestore
    private var companyNameDB = Vector<String>()
    private var descriptionDB = Vector<String>()
    private var urlDB = Vector<String>()
    private var needPointsDB = Vector<Int>()
    private var nbCollectionDB = 0
    private var nbPoints = 0
    private lateinit var toast: Toast
    private val viewModel: SettingSave by activityViewModels()
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT)

        val dynamicLayout: LinearLayout = binding.dynamicContent

        nbPoints = viewModel.getnbPoints()

        val docRef1: CollectionReference = db.collection("rewards")
        docRef1.get()
            .addOnSuccessListener { result ->
                companyNameDB.removeAllElements()
                descriptionDB.removeAllElements()
                urlDB.removeAllElements()
                needPointsDB.removeAllElements()
                nbCollectionDB = 0
                for (document in result) {
                    if (document.get("companyName") != null && document.get("description") != null && document.get("needPoints") != null && document.get("url") != null) {
                        Log.d(Constraints.TAG, "${document.id} => ${document.data}")
                        companyNameDB.add(document.get("companyName") as String)
                        descriptionDB.add(document.get("description") as String)
                        urlDB.add(document.get("url") as String)
                        needPointsDB.add((document.getLong("needPoints") as Long).toInt())

                        val wizardView = layoutInflater.inflate(R.layout.vignette, dynamicLayout, false)
                        val textViewDescription : TextView = wizardView.findViewById(R.id.description)
                        textViewDescription.text = descriptionDB.elementAt(nbCollectionDB)
                        val textViewCompanyName : TextView = wizardView.findViewById(R.id.company_name)
                        textViewCompanyName.text = companyNameDB.elementAt(nbCollectionDB)
                        val textViewNeedPoints : TextView = wizardView.findViewById(R.id.need_points)
                        textViewNeedPoints.text = "${needPointsDB.elementAt(nbCollectionDB)} points"

                        val buttonURL: FloatingActionButton = wizardView.findViewById(R.id.fabV)
                        val index = nbCollectionDB //val to make index final (so it doesn't change)
                        if(nbPoints >= needPointsDB.elementAt(nbCollectionDB))
                        {
                            buttonURL.setOnClickListener { onClickURL(index) }
                        }
                        else
                        {
                            buttonURL.setOnClickListener { onClickNotEnough(index) }
                        }

                        dynamicLayout.addView(wizardView)

                        nbCollectionDB += 1
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(Constraints.TAG, "Error getting documents.", exception)
            }

        return binding.root
    }

    private fun onClickURL(index: Int) {
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
        val urlString: String = urlDB.elementAt(index)
        val uri: Uri = Uri.parse(urlString)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    private fun onClickNotEnough(index: Int) {
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
        val diffValue = needPointsDB.elementAt(index) - nbPoints
        val text = "Vous n'avez pas assez de points, il vous manque $diffValue points !"
        toast.cancel()
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}