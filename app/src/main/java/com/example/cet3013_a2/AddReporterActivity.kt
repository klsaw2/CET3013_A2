package com.example.cet3013_a2

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.example.cet3013_a2.databinding.FragmentProfileReporterBinding

class AddReporterActivity: AppCompatActivity(), AdapterView.OnItemSelectedListener  {
    private lateinit var binding: FragmentProfileReporterBinding
    private lateinit var name: String
    private lateinit var relationship: String
    private var age: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = FragmentProfileReporterBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                TODO()
                //Toast.makeText(this,name + age + relationship,Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnCancel.setOnClickListener {
            TODO()
        }
        binding.btnDelete.setOnClickListener {
            TODO()
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

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        relationship = p0?.getItemAtPosition(p2).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}