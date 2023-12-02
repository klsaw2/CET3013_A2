package com.example.cet3013_a2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
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
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.example.cet3013_a2.databinding.ActivityEditRecordBinding
import com.example.cet3013_a2.roomdb.Record
import com.example.cet3013_a2.roomdb.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditRecordActivity : AppCompatActivity(),
    DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
        private lateinit var binding: ActivityEditRecordBinding
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
        ) {
                result ->
            if (result.resultCode == RESULT_OK) {
                photoUrl = result.data!!.getStringExtra(CameraActivity.photoUrlTag)
                if (photoUrl != null) {
                    viewModel.newPhotoUrl = photoUrl
                    showCapturedPhotoUI(photoUrl as String)
                    togglePictureButtonUI(true)
                }
            }
        }
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityEditRecordBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Change status bar color
            window.statusBarColor = getColor(R.color.blue3)

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
            viewModel = ViewModelProvider(this)[ViewModel::class.java]
    
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

                val recordId = intent.getIntExtra(recordIdExtra, -1)
                viewModel.getRecordById(recordId).observe(this) {
                    val record = it.first()

                    // Retain lossy location info on config change
                    binding.txtLocationName.text = record.locationName
                    binding.txtLocationCoords.text = "( ${record.locationLat}, ${record.locationLng} )"

                    if (viewModel.editingRecord == null) {
                        viewModel.editingRecord = record
                        initializeActivity(record)
                    }

                    // Retain lossy edited photo on config change
                    if (viewModel.newPhotoUrl != null) {
                        photoUrl = viewModel.newPhotoUrl
                        binding.imgSavedPhoto.setImageBitmap(getBitmapFromUrl(viewModel.newPhotoUrl!!))
                        togglePictureButtonUI(true)
                    }
                }
            }

            currentCalendar.add(Calendar.HOUR_OF_DAY, 8)

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
                viewModel.newPhotoUrl = null
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

            binding.btnEdit.setOnClickListener {
                editRecord()
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
    
        private fun editRecord() {
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
                val recordId = intent.getIntExtra(recordIdExtra, -1)
                val updatedRecord = Record(
                    id = recordId,
                    title = title,
                    category = category,
                    dateTime = dateTime,
                    locationName = viewModel.editingRecord!!.locationName,
                    locationLat = viewModel.editingRecord!!.locationLat,
                    locationLng = viewModel.editingRecord!!.locationLng,
                    photoUrl = photoUrl,
                    reportedBy = reportedBy,
                    notes = notes
                )
                viewModel.updateRecord(updatedRecord)
                viewModel.editingRecord = null
                viewModel.newPhotoUrl = null
                goBack(true, false, updatedRecord)
            } catch (e: Exception) {
                Toast.makeText(this, "Unable to edit record, please try again.", Toast.LENGTH_SHORT)
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
    
        private fun showCapturedPhotoUI(photoUrl: String) {
            try {
                val bitmap = getBitmapFromUrl(photoUrl)
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
    
        private fun goBack(isProcessComplete: Boolean, showConfirmation: Boolean = true, updatedRecord: Record? = null) {
            fun backFunction() {
                if (isProcessComplete) {
                    val returnIntent = Intent()
                    returnIntent.putExtra("id", updatedRecord!!.id)
                    returnIntent.putExtra("title", updatedRecord.title)
                    returnIntent.putExtra("notes", updatedRecord.notes)
                    returnIntent.putExtra("dateTime", updatedRecord.dateTime)
                    returnIntent.putExtra("category", updatedRecord.category)
                    returnIntent.putExtra("reportedBy", updatedRecord.reportedBy)
                    returnIntent.putExtra("photoUrl", updatedRecord.photoUrl)
                    returnIntent.putExtra("locationName", updatedRecord.locationName)
                    returnIntent.putExtra("locationLat", updatedRecord.locationLat)
                    returnIntent.putExtra("locationLng", updatedRecord.locationLng)
                    setResult(RESULT_OK, returnIntent)
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

        private fun initializeActivity(record: Record) {
            val imgPhoto = binding.imgSavedPhoto
            val txtDate = binding.txtDate
            val txtTime = binding.txtTime
            val txtTitle = binding.txtTitle
            val txtNotes = binding.txtNotes
            val spinnerCategory = binding.spinnerCategory
            val spinnerReporter = binding.spinnerReporter
            val txtLocationName = binding.txtLocationName
            val txtLocationCoords = binding.txtLocationCoords

            val date = dateFormatter.format(record.dateTime.toLong())
            val time = timeFormatter.format(record.dateTime.toLong())

            if (record.photoUrl != null) {
                photoUrl = record.photoUrl
                viewModel.newPhotoUrl = photoUrl
                imgPhoto.setImageBitmap(getBitmapFromUrl(record.photoUrl!!))
                togglePictureButtonUI(true)
            }

            if (record.notes != null) {
                txtNotes.setText(record.notes)
            }

            txtDate.setText(date)
            txtTime.setText(time)
            txtTitle.setText(record.title)
            txtTitle.setText(record.title)
            txtTitle.setText(record.title)
            txtLocationName.text = record.locationName
            txtLocationCoords.text = "( ${record.locationLat} , ${record.locationLng} )"

            val categoryIndex = categories.indexOf(record.category)
            val reporterIndex = reporterIds.indexOf(record.reportedBy)
            spinnerCategory.setSelection(categoryIndex)
            spinnerReporter.setSelection(reporterIndex)
        }

        private fun getBitmapFromUrl(photoUrl: String): Bitmap {
            val imageUri = Uri.parse(photoUrl)
            val source = ImageDecoder.createSource(this.contentResolver, imageUri)
            return ImageDecoder.decodeBitmap(source)
        }

        companion object {
            const val REQUEST_LOCATION_PERMISSION = 1
            const val recordIdExtra = "recordIdExtra"
        }
}