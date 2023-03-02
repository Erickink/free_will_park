package com.example.freewillpark.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.freewillpark.R
import com.example.freewillpark.SettingSave
import com.example.freewillpark.databinding.FragmentStreetBinding
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback
import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StreetFragment : Fragment(), OnStreetViewPanoramaReadyCallback {

    private lateinit var mStreet: StreetViewPanorama
    private val viewModel: SettingSave by activityViewModels()
    private lateinit var lastMarkedLL: LatLng
    private lateinit var buttonPrincipale: FloatingActionButton
    private var _binding: FragmentStreetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentStreetBinding.inflate(inflater, container, false)

        val streetViewPanoramaFragment = childFragmentManager
            .findFragmentById(R.id.streetviewpanorama) as SupportStreetViewPanoramaFragment
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this)

        binding.fabS.setOnClickListener { onClickReturn() }

        buttonPrincipale = viewModel.getbuttonPrincipale()
        buttonPrincipale.visibility = View.GONE

        lastMarkedLL = viewModel.getlastMarkedLL()

        return binding.root
    }

    private fun onClickReturn() {
        activity?.onBackPressed()
    }

    override fun onDestroyView() {
        buttonPrincipale.visibility = View.VISIBLE
        super.onDestroyView()
        _binding = null
    }

    override fun onStreetViewPanoramaReady(streetViewPanorama: StreetViewPanorama) {
        mStreet = streetViewPanorama
        mStreet.setPosition(lastMarkedLL)
    }
}