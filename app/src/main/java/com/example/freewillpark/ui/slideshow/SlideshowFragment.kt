package com.example.freewillpark.ui.slideshow

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.freewillpark.R
import com.example.freewillpark.SettingSave
import com.example.freewillpark.databinding.FragmentSlideshowBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        if (savedInstanceState == null) {
            childFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }

        return binding.root
    }

    class SettingsFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {
        private lateinit var typePref: SwitchPreferenceCompat
        private lateinit var veloPref: SwitchPreferenceCompat
        private lateinit var trotPref: SwitchPreferenceCompat
        private val viewModel: SettingSave by activityViewModels()

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
            typePref = findPreference("type")!!
            veloPref = findPreference("velo")!!
            trotPref = findPreference("trot")!!

            var typeB = typePref.sharedPreferences?.getBoolean("type", false)
            var veloB = veloPref.sharedPreferences?.getBoolean("velo", false)
            var trotB = trotPref.sharedPreferences?.getBoolean("trot", false)

            if (typeB != null) {
                viewModel.setTypeBoolean(typeB)
            }
            if (veloB != null) {
                viewModel.setVeloBoolean(veloB)
            }
            if (trotB != null) {
                viewModel.setTrotBoolean(trotB)
            }
        }

        override fun onSharedPreferenceChanged(sharedPref: SharedPreferences, key: String) {
            when (key) {
                "type" -> {
                    var typeB = sharedPref.getBoolean("type", false)
                    viewModel.setTypeBoolean(typeB)
                }
                "velo" -> {
                    var veloB = sharedPref.getBoolean("velo", false)
                    viewModel.setVeloBoolean(veloB)
                }
                "trot" -> {
                    var trotB = sharedPref.getBoolean("trot", false)
                    viewModel.setTrotBoolean(trotB)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}