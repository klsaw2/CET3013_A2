package com.example.cet3013_a2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import com.example.cet3013_a2.roomdb.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cet3013_a2.databinding.ActivityAddRecordBinding
import com.example.cet3013_a2.roomdb.Record
import com.example.location.FetchLocationNameTask
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddRecordActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: ActivityAddRecordBinding
    private lateinit var viewModel: ViewModel
    private val backConfirmationTag = "add_record_back_confirmation"
    private val currentCalendar = Calendar.getInstance()
    private val recordCalendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd/M/yyyy", Locale.US)
    private val timeFormatter = SimpleDateFormat("hh:mm a", Locale.US)
    private val categories = ArrayList<String>(
        arrayOf(
            "Indoor",
            "Outdoor",
            "Physical",
            "Overnight",
            "Others"
        ).toMutableList()
    )
    private lateinit var reporters: List<String>
    private lateinit var reporterIds: List<Int>
    private lateinit var spinnerCategoryAdapter: ArrayAdapter<String>
    private lateinit var spinnerReporterAdapter: ArrayAdapter<String>

    private var photoUrl: String? = null
    private var locationLat: Double? = null
    private var locationLng: Double? = null
    private var locationName: String? = null
    private var locationCoords: String? = null
    lateinit var fusedLocationClient: FusedLocationProviderClient
    val fetchLocationNameCoroutine = CoroutineScope(Dispatchers.Main)
    private var cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        result ->
            if (result.resultCode == RESULT_OK) {
                photoUrl = result.data!!.getStringExtra(CameraActivity.photoUrlTag)
                if (photoUrl != null) {
                    showCapturedPhotoUI(photoUrl as String)
                    togglePictureButtonUI(true)

                    // Get location on photo
                    try {
                        getLocation(true)

                    } catch (e: Exception) {
                        Toast.makeText(this, "Unable to fetch location, please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSION
            )
        }

        // Set status bar color to match with app bar color
        window.statusBarColor = getColor(R.color.blue3)

        // Check if intent has imagePath
        val imagePath = intent.getStringExtra("imagePath")
        if (imagePath != null) {
            // parse imagePath into Uri
            val imageUri = Uri.fromFile(File(imagePath)).toString()
            showCapturedPhotoUI(imageUri) // Show image
            togglePictureButtonUI(true) // Toggle UI
        }

        // Toolbar
        setSupportActionBar(findViewById(binding.tbAddRecord.id))
        val backDrawable: Drawable = AppCompatResources.getDrawable(this, R.drawable.icon_back)!!
        backDrawable.setTint(getColor(R.color.white))
        supportActionBar!!.setHomeAsUpIndicator(backDrawable)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Spinner entries
        // Category
        spinnerCategoryAdapter = ArrayAdapter(this, R.layout.spinner_item_layout, categories)
        binding.spinnerCategory.adapter = spinnerCategoryAdapter
        // Reporter
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)

        // Todo: Prevent user from deleting all reporters as spinner requires at least 1 entry
        viewModel.getAllReporters().observe(this) {
            reporters = it.map { reporter ->
                reporter.name
            }
            reporterIds = it.map { reporter ->
                reporter.id!!
            }
            spinnerReporterAdapter =
                ArrayAdapter(this, R.layout.spinner_item_layout, reporters)
            binding.spinnerReporter.adapter = spinnerReporterAdapter
        }

        currentCalendar.add(Calendar.HOUR_OF_DAY, 8)
        binding.txtDate.setText(dateFormatter.format(recordCalendar.timeInMillis))
        binding.txtTime.setText(timeFormatter.format(recordCalendar.timeInMillis))

        binding.txtDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                R.style.date_time_picker,
                this,
                recordCalendar.get(Calendar.YEAR),
                recordCalendar.get(Calendar.MONTH),
                recordCalendar.get(Calendar.DAY_OF_MONTH),
            )
            // Dialog styling code
            datePicker.datePicker.maxDate = currentCalendar.timeInMillis
            datePicker.show()
        }

        binding.txtTime.setOnClickListener {
            TimePickerDialog(
                this,
                R.style.date_time_picker,
                this,
                recordCalendar.get(Calendar.HOUR_OF_DAY),
                recordCalendar.get(Calendar.MINUTE),
                false
            ).show()
        }

        binding.btnDiscardPhoto.setOnClickListener {
            photoUrl = null
            togglePictureButtonUI(false)
            toggleLocationButtonUI(false)
        }

        binding.btnTakePhoto.setOnClickListener {
            launchCameraActivity()
        }

        binding.btnRetakePhoto.setOnClickListener {
            launchCameraActivity()
        }

        binding.btnGetLocation.setOnClickListener {
            try {
                getLocation(false)
            } catch (e: Exception) {
                Toast.makeText(this, "Unable to fetch location, please try again.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDiscard.setOnClickListener {
            goBack(false)
        }

        binding.btnAdd.setOnClickListener {
            addNewRecord()
        }

        // Device back button
        onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    goBack(false)
                }
            })
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.simple_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home ->
                goBack(false)
        }
        return super.onOptionsItemSelected(menuItem)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        recordCalendar.apply {
            set(year, month, dayOfMonth)
        }
        binding.txtDate.setText(dateFormatter.format(recordCalendar.timeInMillis))
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        recordCalendar.apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }
        binding.txtTime.setText(timeFormatter.format(recordCalendar.timeInMillis))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION ->
                if (!(grantResults.size > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Location permissions denied, please enable them to continue.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun addNewRecord() {
        val title = binding.txtTitle.text.toString()
        val category = binding.spinnerCategory.selectedItem.toString()
        val dateTime = recordCalendar.timeInMillis.toString()
        val photoUrl = photoUrl
        val reportedBy = reporterIds[binding.spinnerReporter.selectedItemPosition]
        val notes = binding.txtNotes.text.toString()
        if (!validateInput()) {
            Toast.makeText(this, "Ensure all required inputs are filled.", Toast.LENGTH_SHORT)
                .show()
            return
        }

        try {
            viewModel.addRecord(
                Record(
                    title = title,
                    category = category,
                    dateTime = dateTime,
                    locationName = locationName!!,
                    locationLat = locationLat!!,
                    locationLng = locationLng!!,
                    photoUrl = photoUrl,
                    reportedBy = reportedBy,
                    notes = notes
                )
            )
            goBack(true, false)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to add record, please try again.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun validateInput(): Boolean {
        val title = binding.txtTitle.text.toString()

        if (title.isEmpty() || locationLat == null) {
            return false
        }

        return true
    }

    private fun getLocation(isGettingLocationAfterPhoto: Boolean) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
               location ->
                // Location not null only if location fetched with device location turned on
                if (location != null) {
                    locationLat = location.latitude
                    locationLng = location.longitude
                    locationCoords = "( $locationLat , $locationLng )"

                    // Get location name from coords
                    fetchLocationNameCoroutine.launch {
                        val fetchLocationNameTask = FetchLocationNameTask(this@AddRecordActivity, location)
                        fetchLocationNameTask.fetchLocationName()
                        locationName = fetchLocationNameTask.locationName

                        binding.txtLocationName.text = locationName
                    }
                    toggleLocationButtonUI(true, locationCoords!!, isGettingLocationAfterPhoto)
                } else {
                    Toast.makeText(this, "Turn on \"Location\" to get current location.", Toast.LENGTH_SHORT).show()
                }
           }
        }
    }

    // Utilities
    private fun togglePictureButtonUI(photoTaken: Boolean) {
        if (photoTaken) {
            binding.btnTakePhoto.visibility = GONE
            binding.groupRetakePhoto.visibility = VISIBLE
        } else {
            binding.btnTakePhoto.visibility = VISIBLE
            binding.groupRetakePhoto.visibility = GONE
        }
    }

    private fun toggleLocationButtonUI(isLocationFetched: Boolean,
                               locationCoords: String = "",
                               isGettingLocationAfterPhoto: Boolean = false) {
        if (isGettingLocationAfterPhoto) {
            binding.txtLocationCoords.text = locationCoords
            binding.groupLocationInfo.visibility = VISIBLE
            binding.btnGetLocation.visibility = GONE
            return
        }

        if (isLocationFetched) {
            binding.txtLocationCoords.text = locationCoords

            binding.groupLocationInfo.visibility = VISIBLE
        } else {
            binding.txtLocationName.text = ""
            binding.txtLocationCoords.text = ""

            binding.groupLocationInfo.visibility = GONE
            binding.btnGetLocation.visibility = VISIBLE
        }
    }

    private fun showCapturedPhotoUI(photoUrl: String) {
        try {
            val imageUri = Uri.parse(photoUrl)
            val source = ImageDecoder.createSource(this.contentResolver, imageUri)
            val bitmap = ImageDecoder.decodeBitmap(source)
            binding.imgSavedPhoto.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to display image, please try again.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun launchCameraActivity() {
        val intent = Intent(this, CameraActivity::class.java)
        cameraActivityResultLauncher.launch(intent)
    }

    private fun goBack(isProcessComplete: Boolean, showConfirmation: Boolean = true) {
        fun backFunction() {
            if (isProcessComplete) {
                setResult(RESULT_OK)
            }
            finish()
        }

        val alertDialog = ConfirmationDialog(
            getString(R.string.confirmation_title),
            getString(R.string.confirmation_desc_changesLost),
            getString(R.string.btn_confirm),
            getString(R.string.btn_cancel)
        ) { dialog, which ->
            backFunction()
        }

        if (showConfirmation) {
            alertDialog.show(supportFragmentManager, backConfirmationTag)
        } else {
            backFunction()
        }
    }

    companion object {
        val REQUEST_LOCATION_PERMISSION = 1
    }
}