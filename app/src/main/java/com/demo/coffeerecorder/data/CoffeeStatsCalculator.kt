package com.demo.coffeerecorder.data

import com.demo.coffeerecorder.data.local.CoffeeRecordEntity

object CoffeeStatsCalculator {

    fun build(records: List<CoffeeRecordEntity>): CoffeeStats {
        if (records.isEmpty()) {
            return CoffeeStats(
                totalCount = 0,
                averageRating = 0.0,
                totalSpend = 0.0,
                averageSpend = 0.0,
                averageCupSize = 0,
                favoriteMethod = null,
                favoriteDrink = null,
                latestRecord = null
            )
        }

        val totalSpend = records.sumOf { it.priceYuan }
        val averageSpend = totalSpend / records.size
        val averageCupSize = records.map { it.cupSizeMl }.average().toInt()

        return CoffeeStats(
            totalCount = records.size,
            averageRating = records.map { it.rating }.average(),
            totalSpend = totalSpend,
            averageSpend = averageSpend,
            averageCupSize = averageCupSize,
            favoriteMethod = records.mostFrequentBy { it.brewMethod },
            favoriteDrink = records.mostFrequentBy { it.drinkType },
            latestRecord = records.maxByOrNull { it.drankAt }
        )
    }

    private fun <T> List<CoffeeRecordEntity>.mostFrequentBy(selector: (CoffeeRecordEntity) -> T): T? {
        return groupingBy(selector)
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
    }
}
