package com.company.inventaireit.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.company.inventaireit.databinding.FragmentExportBinding
import com.company.inventaireit.utils.ExportUtils
import com.company.inventaireit.viewmodel.InventoryViewModel
import java.io.File
import java.io.FileOutputStream

class ExportFragment : Fragment() {
    private var _binding: FragmentExportBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InventoryViewModel by activityViewModels()
    private var selectedFormat = "csv"

    // Lanceur pour choisir l'emplacement de sauvegarde (SAF)
    private val createFileLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let { saveFileToUri(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.btnExportTxt.setOnClickListener { selectedFormat = "txt" }
        binding.btnExportCsv.setOnClickListener { selectedFormat = "csv" }
        binding.btnExportPdf.setOnClickListener { selectedFormat = "pdf" }

        binding.btnSaveLocal.setOnClickListener {
            val fileName = "Export_Acoss_${System.currentTimeMillis()}.$selectedFormat"
            createFileLauncher.launch(fileName)
        }

        binding.btnSendEmail.setOnClickListener {
            sendEmailWithAttachment()
        }
    }

    private fun saveFileToUri(uri: Uri) {
        val items = viewModel.scannedItems.value ?: return
        val content = when (selectedFormat) {
            "csv" -> ExportUtils.generateCsvData(items)
            "txt" -> ExportUtils.generateTxtData(items)
            else -> ExportUtils.generateCsvData(items)
        }

        try {
            requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(content.toByteArray())
            }
            Toast.makeText(context, "Fichier enregistré !", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Erreur d'enregistrement", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmailWithAttachment() {
        val items = viewModel.scannedItems.value ?: return
        val content = ExportUtils.generateCsvData(items)
        
        // Création d'un fichier temporaire pour la pièce jointe
        val tempFile = File(requireContext().cacheDir, "export_inventaire.csv")
        tempFile.writeText(content)
        
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            tempFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "Export Inventaire Acoss 2026")
            putExtra(Intent.EXTRA_TEXT, "Veuillez trouver ci-joint l'export de l'inventaire.")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        startActivity(Intent.createChooser(intent, "Envoyer l'export via..."))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}