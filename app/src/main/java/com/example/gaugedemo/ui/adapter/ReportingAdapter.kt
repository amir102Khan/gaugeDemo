package com.example.gaugedemo.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gaugedemo.databinding.ItemReadingsBinding
import com.example.gaugedemo.database.Readings

class ReportingAdapter(
    private val readings: List<Readings>,
    private val onItemClick: (Readings) -> Unit
) : RecyclerView.Adapter<ReportingAdapter.ReadingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadingViewHolder {
        val binding = ItemReadingsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReadingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReadingViewHolder, position: Int) {
        val reading = readings[position]
        holder.bind(reading)
    }

    override fun getItemCount(): Int = readings.size

    inner class ReadingViewHolder(private val binding: ItemReadingsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reading: Readings) {
            binding.imgGauge.visibility = View.GONE
            binding.readingValue.text = reading.value
            binding.timestamp.text = "Timestamp: ${reading.timestamp}"
            binding.status.text = if (reading.status == 1) "Synced" else "Offline"
            itemView.setOnClickListener { onItemClick(reading) }
        }
    }
}