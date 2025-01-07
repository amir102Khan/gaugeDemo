package com.example.gaugedemo.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ReadingsDao {

    @Insert
    suspend fun insert(readings: Readings)

    @Query("SELECT * FROM readings ORDER BY timestamp DESC")
    suspend fun getAllReadingsFromDb(): List<Readings>

    @Query("SELECT * FROM readings WHERE status = :status ORDER BY timestamp DESC")
    suspend fun getReadingsByStatusFromDb(status: Int): List<Readings>

    @Query("UPDATE Readings SET status = 1 WHERE status = 0")
    suspend fun updateStatusToUploaded()

    @Update
    suspend fun updateStatusToDb(reading: Readings)

    @Delete
    suspend fun deleteFromDb(reading: Readings)
}
