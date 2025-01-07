package com.example.gaugedemo.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.gaugedemo.R
import com.example.gaugedemo.database.Readings
import com.example.gaugedemo.databinding.ActivityReportingBinding
import com.example.gaugedemo.ui.adapter.ReadingAdapter
import com.example.gaugemeterdemo.ReadingViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale.getDefault
import kotlin.math.abs

class Reporting : AppCompatActivity() {
    private lateinit var binding: ActivityReportingBinding
    private lateinit var readingsAdapter: ReadingAdapter
    private val readingsList: ArrayList<Readings> = arrayListOf()
    private val readingViewModel: ReadingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imgBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        setupRecyclerView()

        readingViewModel.allReadings?.observe(this) { readings ->
            readings?.let {
                setupDateFilter(readings)
            }
        }

        readingViewModel.getReadings()
    }

    private fun setupRecyclerView() {
        readingsAdapter = ReadingAdapter(this@Reporting, readingsList, { onClick ->

        })

        binding.recyclerView.apply {
            layoutManager =
                LinearLayoutManager(this@Reporting, LinearLayoutManager.HORIZONTAL, false)
            adapter = readingsAdapter
        }

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                updateDots()
            }
        })
    }

    private fun setupDateFilter(readingsList: List<Readings>) {
        val uniqueDates = readingsList.map { reading ->
            SimpleDateFormat("dd MMM yyyy", getDefault()).format(Date(reading.timestamp))
        }.distinct()
        println("AMD $uniqueDates")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, uniqueDates)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDateFilter.adapter = adapter

        binding.spinnerDateFilter.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedDate = uniqueDates[position]
                    filterReadingsByDate(selectedDate, readingsList)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun filterReadingsByDate(selectedDate: String, list: List<Readings>) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", getDefault())
        val filteredList = list.filter { reading ->
            val readingDate = dateFormat.format(Date(reading.timestamp))
            readingDate == selectedDate
        }

        readingsList.clear()
        readingsList.addAll(filteredList)
        readingsAdapter?.notifyDataSetChanged()
        setupDots()
        updateDots()
        setupLineChart()
    }


    private fun updateDots() {
        val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
        val position = layoutManager.findFirstVisibleItemPosition()

        for (i in 0 until binding.dotsContainer.childCount) {
            val dot = binding.dotsContainer.getChildAt(i)
            dot.setBackgroundResource(
                if (i == position) {
                    R.drawable.dot_selected
                } else {
                    R.drawable.dot_unselected
                }
            )
        }
    }

    private fun setupDots() {
        binding.dotsContainer.removeAllViews()

        for (i in 0 until readingsList.size) {
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(8.dpToPx(), 8.dpToPx()).apply {
                    setMargins(8.dpToPx(), 0, 8.dpToPx(), 0)
                }
                setBackgroundResource(R.drawable.dot_unselected)
            }
            binding.dotsContainer.addView(dot)
        }
    }


    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun setupLineChart() {
        val lineChart: LineChart = binding.lineChart
        readingsList.sortBy { it.timestamp }

        val startTime = readingsList.firstOrNull()?.timestamp ?: 0L
        val entries = readingsList.mapIndexed { index, reportItem ->
            val timeElapsed = abs(reportItem.timestamp - startTime) / (1000 * 60).toFloat()
            Entry(timeElapsed, reportItem.temperature.toFloat())
        }

        val dataSet = LineDataSet(entries, "Temperature Trend")
        dataSet.color = Color.BLUE
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 12f
        dataSet.setDrawCircles(true)
        dataSet.setCircleColor(Color.RED)
        dataSet.setCircleRadius(6f)
        dataSet.setDrawFilled(true)
        dataSet.fillColor = Color.BLUE
        dataSet.lineWidth = 3f

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        val description = Description()
        description.text = "Temperature over time"
        lineChart.description = description

        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val timeInMillis = startTime + (value * 60 * 1000).toLong()
                val dateFormat = SimpleDateFormat("HH:mm", getDefault())
                return dateFormat.format(Date(timeInMillis))
            }
        }
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(true)

        val xMin = entries.firstOrNull()?.x ?: 0f
        val xMax = entries.lastOrNull()?.x ?: 0f
        val padding = 5f // Optional padding
        xAxis.axisMinimum = xMin - padding
        xAxis.axisMaximum = xMax + padding

        val yAxis = lineChart.axisLeft
        yAxis.axisMinimum = 20f
        yAxis.axisMaximum = 60f
        lineChart.axisRight.isEnabled = false

        lineChart.invalidate()
    }
}