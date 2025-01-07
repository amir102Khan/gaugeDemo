package com.example.gaugedemo.ui.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gaugedemo.databinding.ItemReadingsBinding
import com.example.gaugedemo.database.Readings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale.*

class ReadingAdapter(
    private val context: Context,
    private val readings: List<Readings>,
    private val onItemClick: (Readings) -> Unit
) : RecyclerView.Adapter<ReadingAdapter.ReadingViewHolder>() {

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

    inner class ReadingViewHolder(private val binding: ItemReadingsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reading: Readings) {
            binding.imgGauge.visibility = View.VISIBLE
            val imageUriString = reading.imageUri
            val imageUri = Uri.parse(imageUriString)
            Glide.with(context)
                .load(imageUri)
                .into(binding.imgGauge)
            binding.readingValue.text = "Reading value: ${reading.value}"
            binding.timestamp.text = "Timestamp: ${formatTimestamp(reading.timestamp)}"
            binding.tvTmp.text = "Temperature: ${reading.temperature} °C"
            binding.status.text = if (reading.status == 1) {
                "Status: Synced"
            } else {
                "Status: Offline"
            }
            itemView.setOnClickListener { onItemClick(reading) }
        }
    }

    fun formatTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", getDefault())
        return dateFormat.format(Date(timestamp))
    }
}