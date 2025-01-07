package com.example.gaugedemo.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.gaugedemo.R
import com.example.gaugedemo.ThemePreferenceManager
import com.example.gaugedemo.database.Readings
import com.example.gaugedemo.databinding.ActivityHomeBinding
import com.example.gaugemeterdemo.ReadingViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

class Home : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var imageCapture: ImageCapture? = null
    private val readingViewModel: ReadingViewModel by viewModels()
    private lateinit var themePreferenceManager: ThemePreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        themePreferenceManager = ThemePreferenceManager(this)
        val isDarkMode = themePreferenceManager.isDarkModeEnabled()
        setTheme(if (isDarkMode) R.style.Theme_GaugeDemoNight else R.style.Theme_GaugeDemo)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)


        binding.themeSwitch.isChecked = isDarkMode
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            themePreferenceManager.setDarkModeEnabled(isChecked)
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            recreate()
        }
        if (!isCameraPermissionGranted()) {
            requestCameraPermission()
        } else {
            startCamera()
            setupUI()
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    startCamera()
                    setupUI()
                    Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
    }

    private fun setupUI() {
        binding.btnOpenReadings.setOnClickListener {
            startActivity(Intent(this, ReadingsList::class.java))
        }

        binding.btnOpenReporting.setOnClickListener {
            startActivity(Intent(this, Reporting::class.java))
        }

        binding.captureButton.setOnClickListener {
            if (isCameraPermissionGranted()) {
                // Proceed with camera-related functionality
                Toast.makeText(this, "Launching camera", Toast.LENGTH_SHORT).show()
                takePhoto()
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
                requestCameraPermission()
            }
        }
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            imageCapture = ImageCapture.Builder().build()

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Log.e("CameraX", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }


    private fun simulateAIProcessing(imageUri: String) {
        val randomReadingValue = "${Random.nextInt(1, 100)}"
        val randomTemperature = "${Random.nextInt(20, 60)}"

        val reading = Readings(
            value = randomReadingValue,
            temperature = randomTemperature,
            imageUri = imageUri
        )
        readingViewModel.insert(reading)
        Handler(Looper.getMainLooper()).postDelayed({
            binding.loader.visibility = View.GONE
            Toast.makeText(this, "AI Processing Complete", Toast.LENGTH_SHORT).show()
        }, 5000)
    }

    private fun takePhoto() {
        val photoFile = File(
            externalMediaDirs.firstOrNull(),
            SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    Toast.makeText(this@Home, "Image saved", Toast.LENGTH_SHORT).show()
                    // Simulate AI processing
                    binding.loader.visibility = View.VISIBLE
                    simulateAIProcessing(savedUri.toString())
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@Home,
                        "Photo capture failed: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }
}