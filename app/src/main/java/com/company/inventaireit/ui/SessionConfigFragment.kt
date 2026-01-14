package com.company.inventaireit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.company.inventaireit.R
import com.company.inventaireit.databinding.FragmentSessionConfigBinding
import com.company.inventaireit.viewmodel.InventoryViewModel

class SessionConfigFragment : Fragment() {
    private var _binding: FragmentSessionConfigBinding? = null
    private val binding get() = _binding!!

    // Utilisation d'un ViewModel partagé au niveau de l'activité
    private val viewModel: InventoryViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSessionConfigBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Configuration du Spinner Site
        val sites = arrayOf("Acoss - Marseille")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sites)
        binding.spinnerSite.adapter = adapter

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnValidateConfig.setOnClickListener {
            val selectedSite = binding.spinnerSite.selectedItem.toString()
            // Démarrage de la session dans le ViewModel
            viewModel.startNewSession(selectedSite, "Opérateur")
            
            findNavController().navigate(R.id.action_sessionConfigFragment_to_scanFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}