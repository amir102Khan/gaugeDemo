package com.example.gaugedemo.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gaugedemo.database.Readings
import com.example.gaugedemo.databinding.ActivityReadingsListBinding
import com.example.gaugedemo.ui.adapter.ReadingAdapter
import com.example.gaugemeterdemo.ReadingViewModel


class ReadingsList : AppCompatActivity() {
    private lateinit var binding: ActivityReadingsListBinding
    private val readingViewModel: ReadingViewModel by viewModels()
    private val readingsList: ArrayList<Readings> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadingsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val readingsAdapter = ReadingAdapter(this@ReadingsList, readingsList, { onClick ->

        })

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ReadingsList)
            adapter = readingsAdapter
        }
        readingViewModel.allReadings?.observe(this) { readings ->
            readings?.let {
                readingsList.clear()
                readingsList.addAll(readings)
                readingsAdapter.notifyDataSetChanged()
            }
        }

        binding.syncButton.setOnClickListener {
            binding.loader.visibility = View.VISIBLE
            simulateSyncToServer()
        }

        binding.imgBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        readingViewModel.getReadings()
    }

    private fun simulateSyncToServer() {
        readingViewModel.updateStatusToUploaded()
        Handler(Looper.getMainLooper()).postDelayed({
            binding.loader.visibility = View.GONE
            Toast.makeText(this, "Data synced", Toast.LENGTH_SHORT).show()
            readingViewModel.getReadings()
        }, 5000)
    }
}