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
import com.example.cet3013_a2.databinding.FragmentProfileReporterBinding
import com.example.cet3013_a2.roomdb.Reporter
import com.example.cet3013_a2.roomdb.ViewModel
import java.util.Timer
import kotlin.concurrent.schedule

class AddReporterActivity: AppCompatActivity(), AdapterView.OnItemSelectedListener  {
    private lateinit var binding: FragmentProfileReporterBinding
    private lateinit var viewModel: ViewModel
    private lateinit var name: String
    private lateinit var relationship: String
    private val backConfirmationTag = "add_reporter_back_confirmation"

    private var age: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentProfileReporterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)

        binding.tvErrorMsg.visibility = View.INVISIBLE
        val spinner: Spinner = binding.spinnerRelationship
        spinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            this,
            R.array.relationship,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            spinner.adapter = adapter
        }
        setSupportActionBar(findViewById(binding.tbAddRecord.id))
        val backDrawable: Drawable = AppCompatResources.getDrawable(this, R.drawable.ic_back)!!
        supportActionBar!!.setHomeAsUpIndicator(backDrawable)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.btnConfirm.setOnClickListener {
            if(binding.etvName.text.isEmpty()){
                binding.tvErrorMsg.text = getString(R.string.error_msg_name)
                binding.tvErrorMsg.visibility = View.VISIBLE
            }else if (binding.etvAge.text.isEmpty()){
                binding.tvErrorMsg.text = getString(R.string.error_msg_age)
                binding.tvErrorMsg.visibility = View.VISIBLE
            }else if (relationship.isBlank()){
                binding.tvErrorMsg.text = getString(R.string.error_msg_relationship)
                binding.tvErrorMsg.visibility = View.VISIBLE
            }else{
                name = binding.etvName.text.toString()
                age = binding.etvAge.text.toString().toInt()
                binding.tvErrorMsg.visibility = View.INVISIBLE

                if(age < 18){
                    binding.etvAge.error = "Age should be more than 18"
                }else{
                    if(name.length > 40 ){
                        binding.etvName.error= "Try a shorter name"
                    }else{
                        try{
                            viewModel.addReporter(Reporter(name = name, age = age , relationship = relationship))
                            Toast.makeText(this,"Insert success !",Toast.LENGTH_SHORT).show()
                            Timer().schedule(2000){
                                goBack(true, false)
                            }
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

        val alertDialog = ConfirmationDialog(getString(R.string.confirmation_title), getString(R.string.btn_confirm), getString(R.string.btn_cancel), {
                dialog, which ->
            backFunction()
        }, getString(R.string.confirmation_desc_changesLost))

        if (showConfirmation) {
            alertDialog.show(supportFragmentManager, backConfirmationTag)
        } else {
            backFunction()
        }
    }
}