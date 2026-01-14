package com.company.inventaireit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.company.inventaireit.R
import com.company.inventaireit.databinding.FragmentItemListBinding
import com.company.inventaireit.ui.adapter.ItemAdapter
import com.company.inventaireit.viewmodel.InventoryViewModel

class ItemListFragment : Fragment() {
    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InventoryViewModel by activityViewModels()
    private val itemAdapter = ItemAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        
        binding.rvItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = itemAdapter
        }

        viewModel.scannedItems.observe(viewLifecycleOwner) { items ->
            itemAdapter.submitList(items)
        }

        binding.btnValidateList.setOnClickListener {
            findNavController().navigate(R.id.action_itemListFragment_to_exportFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}