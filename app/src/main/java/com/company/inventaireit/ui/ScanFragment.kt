package com.company.inventaireit.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.company.inventaireit.R
import com.company.inventaireit.camera.BarcodeAnalyzer
import com.company.inventaireit.databinding.FragmentScanBinding
import com.company.inventaireit.utils.SettingsManager
import com.company.inventaireit.viewmodel.InventoryViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanFragment : Fragment() {
    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InventoryViewModel by activityViewModels()
    private lateinit var settingsManager: SettingsManager
    
    private lateinit var cameraExecutor: ExecutorService
    private var cameraControl: CameraControl? = null
    private var isFlashOn = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) startCamera()
        else {
            Toast.makeText(context, "Caméra requise", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        settingsManager = SettingsManager(requireContext())

        checkPermissionAndStart()

        viewModel.currentFloor.observe(viewLifecycleOwner) { updateLocationText() }
        viewModel.currentRoom.observe(viewLifecycleOwner) { updateLocationText() }

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnViewList.setOnClickListener {
            findNavController().navigate(R.id.action_scanFragment_to_itemListFragment)
        }
        binding.btnFlashMode.setOnClickListener { toggleFlash() }
    }

    private fun updateLocationText() {
        binding.tvCurrentLocation.text = "Étage: ${viewModel.currentFloor.value} | Bureau: ${viewModel.currentRoom.value}"
    }

    private fun checkPermissionAndStart() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) 
            == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { barcode ->
                        handleBarcode(barcode)
                    })
                }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageAnalyzer
                )
                cameraControl = camera.cameraControl
                
                if (settingsManager.isAutoFlashEnabled) {
                    cameraControl?.enableTorch(true)
                    isFlashOn = true
                }
            } catch (exc: Exception) {
                Toast.makeText(context, "Erreur caméra", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun handleBarcode(barcode: String) {
        viewModel.scanItem(barcode)
        
        activity?.runOnUiThread {
            binding.tvLastCode.text = "Dernier scan: $barcode"
            provideFeedback()
        }
    }

    private fun provideFeedback() {
        if (settingsManager.isVibrationEnabled) {
            val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(100)
            }
        }
        
        if (settingsManager.isSoundEnabled) {
            // Utilisation du son système de scan ou bip
            try {
                val mediaPlayer = MediaPlayer.create(context, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
                mediaPlayer.start()
                mediaPlayer.setOnCompletionListener { it.release() }
            } catch (e: Exception) {}
        }
    }

    private fun toggleFlash() {
        isFlashOn = !isFlashOn
        cameraControl?.enableTorch(isFlashOn)
        binding.btnFlashMode.alpha = if (isFlashOn) 1.0f else 0.5f
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }
}