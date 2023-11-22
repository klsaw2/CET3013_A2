package com.example.cet3013_a2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.example.cet3013_a2.roomdb.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cet3013_a2.databinding.ActivityAddRecordBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddRecordActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: ActivityAddRecordBinding
    private val currentCalendar = Calendar.getInstance()
    private val recordCalendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd/M/yyyy", Locale.US)
    private val timeFormatter = SimpleDateFormat("hh:mm a", Locale.US)

    private val categories = ArrayList<String>(arrayOf(
        "Indoor",
        "Outdoor",
        "Physical",
        "Overnight",
        "Others"
    ).toMutableList())
    private lateinit var reporters: List<String>
    private lateinit var spinnerCategoryAdapter: ArrayAdapter<String>
    private lateinit var spinnerReporterAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        setSupportActionBar(findViewById(binding.tbAddRecord.id))
        val backDrawable: Drawable = AppCompatResources.getDrawable(this, R.drawable.ic_back)!!
        supportActionBar!!.setHomeAsUpIndicator(backDrawable)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Spinner entries
        // Category
        spinnerCategoryAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categories)
        binding.spinnerCategory.adapter = spinnerCategoryAdapter
        // Reporter
        val viewModel = ViewModelProvider(this).get(ViewModel::class.java)

        // Todo: Prevent user from deleting all reporters as spinner requires at least 1 entry
        viewModel.getAllReporters().observe(this) {
            reporters = it.map { reporter ->
                reporter.reporterName
            }
            spinnerReporterAdapter =
                ArrayAdapter(this, android.R.layout.simple_list_item_1, reporters)
            binding.spinnerReporter.adapter = spinnerReporterAdapter
        }

        currentCalendar.add(Calendar.HOUR_OF_DAY,8)
        binding.txtDate.setText(dateFormatter.format(recordCalendar.timeInMillis))
        binding.txtTime.setText(timeFormatter.format(recordCalendar.timeInMillis))

        binding.txtDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                R.style.DateTimePicker,
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
                R.style.DateTimePicker,
                this,
                recordCalendar.get(Calendar.HOUR_OF_DAY),
                recordCalendar.get(Calendar.MINUTE),
                false
            ).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.simple_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home ->
                finish()
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
}