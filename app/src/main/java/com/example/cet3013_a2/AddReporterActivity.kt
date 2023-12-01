package com.example.cet3013_a2

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.example.cet3013_a2.databinding.ActivityAddReporterBinding
import com.example.cet3013_a2.roomdb.Reporter
import com.example.cet3013_a2.roomdb.ViewModel

class AddReporterActivity: AppCompatActivity(), AdapterView.OnItemSelectedListener  {
    private lateinit var binding: ActivityAddReporterBinding
    private lateinit var viewModel: ViewModel
    private lateinit var name: String
    private lateinit var relationship: String
    private val backConfirmationTag = "add_reporter_back_confirmation"

    private var age: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReporterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)

        // Set spinner
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

        setSupportActionBar(findViewById(binding.tbAddRecord.id))
        val backDrawable: Drawable = AppCompatResources.getDrawable(this, R.drawable.ic_back)!!
        backDrawable.setTint(getColor(R.color.white))
        supportActionBar!!.setHomeAsUpIndicator(backDrawable)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.btnConfirm.setOnClickListener {
            if(binding.etvName.text.isEmpty()){
                binding.etvName.error = getString(R.string.error_msg_name)

            }else if (binding.etvAge.text.isEmpty()){
                binding.etvAge.error = getString(R.string.error_msg_age)
            }else{
                name = binding.etvName.text.toString()
                age = binding.etvAge.text.toString().toInt()

                if(age < 18){
                    binding.etvAge.error = "Age should be more than 18"
                }else{
                    if(name.length > 40 ){
                        binding.etvName.error= "Try a shorter name"
                    }else{
                        try{
                            viewModel.addReporter(Reporter(name = name, age = age , relationship = relationship))
                            Toast.makeText(this,"Insert success !",Toast.LENGTH_SHORT).show()
                            goBack(true, false)
                        }catch (e: Exception){
                            Toast.makeText(this, "Unable to add new reporter, please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        binding.btnCancel.setOnClickListener {
            goBack(false)

        }
        onBackPressedDispatcher.addCallback(
            object: OnBackPressedCallback(true) {
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

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        relationship = p0?.getItemAtPosition(p2).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

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