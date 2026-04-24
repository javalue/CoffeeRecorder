package com.demo.coffeerecorder.data

import androidx.lifecycle.LiveData
import com.demo.coffeerecorder.data.local.CoffeeRecordDao
import com.demo.coffeerecorder.data.local.CoffeeRecordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CoffeeRepository(
    private val coffeeRecordDao: CoffeeRecordDao
) {

    val allRecords: LiveData<List<CoffeeRecordEntity>> = coffeeRecordDao.observeAllRecords()

    suspend fun seedIfEmpty() {
        withContext(Dispatchers.IO) {
            if (coffeeRecordDao.countRecords() > 0) {
                return@withContext
            }

            coffeeRecordDao.insert(createRecord("云南 SOE 日晒", "手冲", "V60", 5, 240, 32.0, "莓果感干净，尾段有可可。", "独立烘焙店", "云南 / 日晒", System.currentTimeMillis() - 2 * 60 * 60 * 1000))
            coffeeRecordDao.insert(createRecord("店里今日拼配", "拿铁", "Espresso", 4, 300, 28.0, "奶感顺滑，很适合上午第一杯。", "街角咖啡店", "拼配 / 中深烘", System.currentTimeMillis() - 22 * 60 * 60 * 1000))
            coffeeRecordDao.insert(createRecord("哥伦比亚 水洗", "美式", "AeroPress", 4, 220, 24.0, "", "家里库存", "哥伦比亚 / 水洗", System.currentTimeMillis() - 48 * 60 * 60 * 1000))
        }
    }

    suspend fun getRecord(recordId: Long): CoffeeRecordEntity? {
        return withContext(Dispatchers.IO) {
            coffeeRecordDao.getRecordById(recordId)
        }
    }

    suspend fun saveRecord(record: CoffeeRecordEntity) {
        withContext(Dispatchers.IO) {
            if (record.id == 0L) {
                coffeeRecordDao.insert(record)
            } else {
                coffeeRecordDao.update(record)
            }
        }
    }

    suspend fun deleteRecord(record: CoffeeRecordEntity) {
        withContext(Dispatchers.IO) {
            coffeeRecordDao.delete(record)
        }
    }

    private fun createRecord(
        beanName: String,
        drinkType: String,
        brewMethod: String,
        rating: Int,
        cupSizeMl: Int,
        priceYuan: Double,
        notes: String,
        roaster: String,
        origin: String,
        drankAt: Long
    ): CoffeeRecordEntity {
        return CoffeeRecordEntity().apply {
            this.beanName = beanName
            this.drinkType = drinkType
            this.brewMethod = brewMethod
            this.rating = rating
            this.cupSizeMl = cupSizeMl
            this.priceYuan = priceYuan
            this.notes = notes
            this.roaster = roaster
            this.origin = origin
            this.drankAt = drankAt
        }
    }
}
