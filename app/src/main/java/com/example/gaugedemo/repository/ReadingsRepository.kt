package com.example.gaugemeterdemo

import com.example.gaugedemo.database.Readings
import com.example.gaugedemo.database.ReadingsDao

class ReadingRepository(private val readingDao: ReadingsDao) {

    //val allReadings: LiveData<List<Readings>> = readingDao.getAllReadingsFromDb()

    suspend fun insert(reading: Readings) {
        readingDao.insert(reading)
    }

    suspend fun updateStatus(reading: Readings) {
        readingDao.updateStatusToDb(reading)
    }

    suspend fun updateStatusToUploaded() {
        readingDao.updateStatusToUploaded()
    }

    suspend fun delete(reading: Readings) {
        readingDao.deleteFromDb(reading)
    }

    suspend fun getReadingsByStatus(status: Int): List<Readings> {
        return readingDao.getReadingsByStatusFromDb(status)
    }

    suspend fun getReadings(): List<Readings>{
        return readingDao.getAllReadingsFromDb()
    }
}