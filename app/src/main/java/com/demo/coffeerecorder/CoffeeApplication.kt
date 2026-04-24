package com.demo.coffeerecorder

import android.app.Application
import com.demo.coffeerecorder.data.CoffeeRepository
import com.demo.coffeerecorder.data.local.CoffeeDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CoffeeApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: CoffeeDatabase by lazy {
        CoffeeDatabase.getInstance(this)
    }

    val repository: CoffeeRepository by lazy {
        CoffeeRepository(database.coffeeRecordDao())
    }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            repository.seedIfEmpty()
        }
    }
}
