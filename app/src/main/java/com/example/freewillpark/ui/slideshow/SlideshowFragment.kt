package com.example.freewillpark.ui.slideshow

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.example.freewillpark.R
import com.example.freewillpark.SettingSave
import com.example.freewillpark.databinding.FragmentSlideshowBinding

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

        private lateinit var sonPref: SwitchPreferenceCompat
        private lateinit var volumePref: SeekBarPreference
        private lateinit var vibrationPref: SwitchPreferenceCompat

        private val viewModel: SettingSave by activityViewModels()

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
            typePref = findPreference("type")!!
            veloPref = findPreference("velo")!!
            trotPref = findPreference("trot")!!

            sonPref = findPreference("son")!!
            volumePref = findPreference("volume")!!
            vibrationPref = findPreference("vibration")!!

            var typeB = typePref.sharedPreferences?.getBoolean("type", false)
            var veloB = veloPref.sharedPreferences?.getBoolean("velo", false)
            var trotB = trotPref.sharedPreferences?.getBoolean("trot", false)

            var sonB = sonPref.sharedPreferences?.getBoolean("son", false)
            var volumeI = volumePref.sharedPreferences?.getInt("volume", 0)
            var vibrationB = vibrationPref.sharedPreferences?.getBoolean("vibration", false)

            typePref.setOnPreferenceClickListener { vibratePhone() }
            veloPref.setOnPreferenceClickListener { vibratePhone() }
            trotPref.setOnPreferenceClickListener { vibratePhone() }

            sonPref.setOnPreferenceClickListener { vibratePhone() }
            volumePref.setOnPreferenceChangeListener { _, _ -> vibratePhone2() }
            vibrationPref.setOnPreferenceClickListener { vibratePhone() }

            if (typeB != null) {
                viewModel.setTypeBoolean(typeB)
            }
            if (veloB != null) {
                viewModel.setVeloBoolean(veloB)
            }
            if (trotB != null) {
                viewModel.setTrotBoolean(trotB)
            }

            if (sonB != null) {
                viewModel.setSonBoolean(sonB)
            }
            if (volumeI != null) {
                viewModel.setVolume(volumeI)
            }
            if (vibrationB != null) {
                viewModel.setVibrationBoolean(vibrationB)
            }
        }

        private fun vibratePhone2(): Boolean {
            if(viewModel.getVibrationBoolean())
            {
                val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator.vibrate(50)
                }
            }
            return true
        }

        private fun vibratePhone(): Boolean {
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
            return true
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
                "son" -> {
                    var sonB = sharedPref.getBoolean("son", false)
                    viewModel.setSonBoolean(sonB)
                }
                "volume" -> {
                    var volumeI = sharedPref.getInt("volume", 0)
                    viewModel.setVolume(volumeI)
                }
                "vibration" -> {
                    var vibrationB = sharedPref.getBoolean("vibration", false)
                    viewModel.setVibrationBoolean(vibrationB)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}