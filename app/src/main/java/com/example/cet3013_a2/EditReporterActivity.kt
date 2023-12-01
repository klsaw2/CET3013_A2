package com.example.cet3013_a2

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.example.cet3013_a2.databinding.ActivityEditReporterBinding
import com.example.cet3013_a2.roomdb.Reporter
import com.example.cet3013_a2.roomdb.ViewModel
import java.util.Timer
import kotlin.concurrent.schedule

class EditReporterActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    // Binding
    private lateinit var binding: ActivityEditReporterBinding
    private lateinit var viewModel: ViewModel
    private lateinit var relationship: String
    private val backConfirmationTag = "add_reporter_back_confirmation"
    private val deleteConfirmationTag = "delete_reporter_confirmation"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout
        binding = ActivityEditReporterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup spinner
        val spinner: Spinner = binding.spinnerRelationship
        spinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            this,
            R.array.relationship,
            R.layout.spinner_item_layout
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(R.layout.spinner_item_layout)
            // Apply the adapter to the spinner.
            spinner.adapter = adapter
        }

        // Set status bar color
        window.statusBarColor = getColor(R.color.blue3)

        // Setup App Bar
        setSupportActionBar(findViewById(binding.tbEditReporter.id))
        val backDrawable: Drawable = AppCompatResources.getDrawable(this, R.drawable.ic_back)!!
        backDrawable.setTint(getColor(R.color.white))
        supportActionBar!!.setHomeAsUpIndicator(backDrawable)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Setup ViewModel to access database
        viewModel = ViewModelProvider(this)[ViewModel::class.java]

        // Get the information base on id passed in, from database to set the UI
        val id = intent.getIntExtra("id", -1)
        if (id != -1) {
            // try to get the reporter from the database
            viewModel.getReporterById(id).observe(this) {
                if (it.isEmpty()) {
                    // Failed to find this reporter
                    Toast.makeText(this, "Failed to find this reporter.", Toast.LENGTH_SHORT).show()
                    goBack(false, false)
                    return@observe
                } else if (it.size > 1) {
                    // This should not happen
                    Toast.makeText(this, "Found more than one reporter.", Toast.LENGTH_SHORT).show()
                    goBack(false, false)
                    return@observe
                } else {
                    // Found the reporter
                    // Update the UI
                    binding.etvName.setText(it[0].name)
                    binding.etvAge.setText(it[0].age.toString())

                    // Set the spinner to the correct value
                    for (i in 0 until binding.spinnerRelationship.adapter.count) {
                        val item = binding.spinnerRelationship.adapter.getItem(i).toString()
                        if (item == it[0].relationship) {
                            // Found a match
                            spinner.setSelection(i)
                            break
                        }
                    }
                }
            }
        } else {
            // Failed to get intent id
            Toast.makeText(this, "Failed to find this reporter.", Toast.LENGTH_SHORT).show()
            goBack(false, false)
        }


        // Setup save button
        binding.btnConfirm.setOnClickListener {
            // Get the information from the UI
            val name = binding.etvName.text
            val age = binding.etvAge.text
            val relationship = this.relationship
            // Check for empty fields
            if (name.isEmpty()) {
                binding.etvName.error = getString(R.string.error_msg_name)
            } else if (age.isEmpty()) {
                binding.etvAge.error = getString(R.string.error_msg_age)
            } else {
                if (age.toString().toInt() < 18) {
                    binding.etvAge.error = "Age should be more than 18"
                } else {
                    if (name.length > 40) {
                        binding.etvName.error = "Try a shorter name"
                    } else {
                        try {
                            viewModel.updateReporter(
                                Reporter(
                                    id = id,
                                    name = name.toString(),
                                    age = age.toString().toInt(),
                                    relationship = relationship
                                )
                            )
                            Toast.makeText(this, "Update success !", Toast.LENGTH_SHORT).show()
                            goBack(true, false)

                        } catch (e: Exception) {
                            Toast.makeText(
                                this,
                                "Unable to update reporter, please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
        // Setup cancel button
        binding.btnCancel.setOnClickListener {
            goBack(false,true)
        }

        binding.btnDelete.setOnClickListener {
            // Ask for using alert dialog
            val alertDialog = ConfirmationDialog(
                getString(R.string.confirmation_title),
                getString(R.string.confirmation_desc_deleteReporter),
                getString(R.string.btn_confirm),
                getString(R.string.btn_cancel)
            ) { dialog, which ->
                // Delete the reporter
                viewModel.deleteReporter(Reporter(
                    id = id,
                    name = binding.etvName.text.toString(),
                    age = binding.etvAge.text.toString().toInt(),
                    relationship = relationship))
                Toast.makeText(this, "Delete success !", Toast.LENGTH_SHORT).show()
                Timer().schedule(2000) {
                    goBack(true, false)
                }
            }

            alertDialog.show(supportFragmentManager, deleteConfirmationTag)

        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        relationship = p0?.getItemAtPosition(p2).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home ->
                goBack(false)
        }
        return super.onOptionsItemSelected(menuItem)
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
}