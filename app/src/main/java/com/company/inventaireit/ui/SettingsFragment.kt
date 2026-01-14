package com.company.inventaireit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.company.inventaireit.databinding.FragmentSettingsBinding
import com.company.inventaireit.utils.SettingsManager

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var settingsManager: SettingsManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsManager = SettingsManager(requireContext())

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Bind settings
        binding.switchSound.isChecked = settingsManager.isSoundEnabled
        binding.switchVibration.isChecked = settingsManager.isVibrationEnabled
        binding.switchFlashAuto.isChecked = settingsManager.isAutoFlashEnabled

        binding.switchSound.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.isSoundEnabled = isChecked
        }
        binding.switchVibration.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.isVibrationEnabled = isChecked
        }
        binding.switchFlashAuto.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.isAutoFlashEnabled = isChecked
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}