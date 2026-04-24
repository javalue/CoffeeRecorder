package com.demo.coffeerecorder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.demo.coffeerecorder.CoffeeApplication
import com.demo.coffeerecorder.data.CoffeeRepository
import com.demo.coffeerecorder.data.CoffeeStats
import com.demo.coffeerecorder.data.CoffeeStatsCalculator
import com.demo.coffeerecorder.data.local.CoffeeRecordEntity

class CoffeeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CoffeeRepository =
        (application as CoffeeApplication).repository

    val records: LiveData<List<CoffeeRecordEntity>> = repository.allRecords

    val recentRecords: LiveData<List<CoffeeRecordEntity>> = records.map { records ->
        records.take(3)
    }

    val stats: LiveData<CoffeeStats> = records.map { records ->
        CoffeeStatsCalculator.build(records)
    }

    suspend fun getRecord(recordId: Long): CoffeeRecordEntity? {
        return repository.getRecord(recordId)
    }

    suspend fun saveRecord(record: CoffeeRecordEntity) {
        repository.saveRecord(record)
    }

    suspend fun deleteRecord(record: CoffeeRecordEntity) {
        repository.deleteRecord(record)
    }
}
