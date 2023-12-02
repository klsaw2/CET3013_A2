package com.example.cet3013_a2

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.cet3013_a2.databinding.ActivityCameraBinding
import java.text.SimpleDateFormat
import java.util.Locale

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null
    private var photoURL = ""
    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) {
            permissions ->
        var permissionGranted = true
        permissions.entries.forEach {
            if (it.key in REQUIRED_PERMISSIONS && it.value == false) {
                permissionGranted = false
            }
        }

        if (!permissionGranted) {
            Toast.makeText(baseContext,
                "Permission request denied",
                Toast.LENGTH_SHORT).show()
        } else {
            startCamera()
        }
    }
    private var hasTakenPhoto: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue3)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        binding.btnCapture.setOnClickListener {
            takePhoto()
        }

        binding.btnRecapture.setOnClickListener {
            toggleImageCaptureUI()
        }

        binding.btnConfirmPhoto.setOnClickListener {
            goBack(true)
        }

        binding.btnBackFromPreview.setOnClickListener { v ->
            goBack(false)
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {

                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(this@CameraActivity, "Failed to capture photo.", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    photoURL = output.savedUri.toString()
                    toggleImageCaptureUI()
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.imgPreview.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.e(TAG, "Camera use case binding failed.", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val photoUrlTag = "photoURL"
        private const val TAG = "CameraActivity"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    // Utilities

    fun toggleImageCaptureUI() {
        hasTakenPhoto = !hasTakenPhoto
        if (hasTakenPhoto) {
            binding.btnCapture.visibility = GONE
            binding.groupConfirmPhoto.visibility = VISIBLE

            // Show image and toggle image views
            val imageUri = Uri.parse(photoURL)
            val source = ImageDecoder.createSource(contentResolver, imageUri)
            val bitmap = ImageDecoder.decodeBitmap(source)
            binding.imgPhoto.setImageBitmap(bitmap)

            binding.imgPhoto.visibility = VISIBLE
            binding.imgPreview.visibility = GONE
        } else {
            binding.btnCapture.visibility = VISIBLE
            binding.groupConfirmPhoto.visibility = GONE
            binding.imgPreview.visibility = VISIBLE
            binding.imgPhoto.visibility = GONE
        }
    }

    private fun goBack(isProcessComplete: Boolean) {
        // Send photo URL back to Add/Edit Record
        if (isProcessComplete) {
            val intent = Intent()
            intent.putExtra(photoUrlTag, photoURL)
            Toast.makeText(this, photoURL, Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK, intent) //Return ok code with data
        }
        finish()
    }
}