package com.example.cet3013_a2

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import com.example.cet3013_a2.databinding.FragmentProfileReporterBinding

class AddReporterActivity: AppCompatActivity() {
    private lateinit var binding: FragmentProfileReporterBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = FragmentProfileReporterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(binding.tbAddRecord.id))
        val backDrawable: Drawable = AppCompatResources.getDrawable(this, R.drawable.ic_back)!!
        supportActionBar!!.setHomeAsUpIndicator(backDrawable)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.btnConfirm.setOnClickListener {
            var name = binding.etvName.text
            var relationship = ""
            var age = ""
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
}