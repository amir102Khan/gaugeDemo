package com.example.gaugemeterdemo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.gaugedemo.database.DatabaseClient
import com.example.gaugedemo.database.Readings
import kotlinx.coroutines.launch

class ReadingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReadingRepository
    val allReadings: MutableLiveData<List<Readings>> = MutableLiveData()

    val readingsByStatus: MutableLiveData<List<Readings>> = MutableLiveData()


    init {
        val readingDao = DatabaseClient.getInstance(application).getAppDatabase().readingsDao()
        repository = ReadingRepository(readingDao)
        //allReadings = repository.allReadings
    }

    fun insert(reading: Readings) {
        viewModelScope.launch {
            repository.insert(reading)
        }
    }

    fun updateStatusToUploaded() {
        viewModelScope.launch {
            repository.updateStatusToUploaded()
        }
    }

    fun getReadings() {
        viewModelScope.launch {
            val data = repository.getReadings()
            allReadings?.postValue(data)
        }
    }


    fun getReadingsByStatus(status: Int) {
        viewModelScope.launch {
            val data = repository.getReadingsByStatus(status)
            readingsByStatus?.postValue(data)
        }

    }

    fun delete(reading: Readings) {
        viewModelScope.launch {
            repository.delete(reading)
        }
    }
}
