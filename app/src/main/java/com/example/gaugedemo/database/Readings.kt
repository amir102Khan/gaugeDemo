package com.example.gaugedemo.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Readings")
data class Readings(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "reading_value") val value: String,
    @ColumnInfo(name = "temperature") val temperature: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "status") val status: Int = 0,
    @ColumnInfo(name = "image_uri") val imageUri: String? = null
)