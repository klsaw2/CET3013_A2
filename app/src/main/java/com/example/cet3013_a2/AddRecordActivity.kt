package com.example.cet3013_a2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.ViewModelProvider
import com.example.cet3013_a2.databinding.ActivityAddRecordBinding
import com.example.cet3013_a2.roomdb.Record
import com.example.cet3013_a2.roomdb.ViewModel
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
    private var cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            photoUrl = result.data!!.getStringExtra(CameraActivity.photoUrlTag)
            if (photoUrl != null) {
                showCapturedPhotoUI(photoUrl as String)
                togglePictureButtonUI(true)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        spinnerCategoryAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categories)
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
                ArrayAdapter(this, android.R.layout.simple_list_item_1, reporters)
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
        }

        binding.btnTakePhoto.setOnClickListener {
            launchCameraActivity()
        }

        binding.btnRetakePhoto.setOnClickListener {
            launchCameraActivity()
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

    private fun addNewRecord() {
        val title = binding.txtTitle.text.toString()
        val category = binding.spinnerCategory.selectedItem.toString()
        val dateTime = recordCalendar.timeInMillis.toString()
        val photoUrl = photoUrl
        val reportedBy = reporterIds[binding.spinnerReporter.selectedItemPosition]
//        val locationLat =
//        val locationLng =

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
                    locationLat = 0.0,
                    locationLng = 0.0,
                    photoUrl = photoUrl,
                    reportedBy = reportedBy,
                )
            )
            goBack(true, false)
        } catch (e: Exception) {
            Log.d("db", "cant add record", e)
            Toast.makeText(this, "Unable to add record, please try again.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun validateInput(): Boolean {
        val title = binding.txtTitle.text.toString()

        if (title.isEmpty()) {
            return false
        }

        return true
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

    fun toggleLocationButtonUI(isLocationSelected: Boolean, locationName: String?) {
        if (isLocationSelected) {
            binding.lblLocationName.text = ""
            binding.btnLocation.text = getString(R.string.lbl_another_location)
        } else {
            binding.lblLocationName.text = locationName
            binding.btnLocation.text = getString(R.string.lbl_another_location)
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
            getString(R.string.btn_confirm),
            getString(R.string.btn_cancel),
            { dialog, which ->
                backFunction()
            },
            getString(R.string.confirmation_desc_changesLost)
        )

        if (showConfirmation) {
            alertDialog.show(supportFragmentManager, backConfirmationTag)
        } else {
            backFunction()
        }
    }
}