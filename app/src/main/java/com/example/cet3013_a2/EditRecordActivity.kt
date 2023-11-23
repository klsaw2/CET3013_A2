package com.example.cet3013_a2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cet3013_a2.databinding.ActivityEditRecordBinding

class EditRecordActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditRecordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}