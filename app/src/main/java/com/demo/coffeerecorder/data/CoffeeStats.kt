package com.demo.coffeerecorder.data

import com.demo.coffeerecorder.data.local.CoffeeRecordEntity

data class CoffeeStats(
    val totalCount: Int,
    val averageRating: Double,
    val totalSpend: Double,
    val averageSpend: Double,
    val averageCupSize: Int,
    val favoriteMethod: String?,
    val favoriteDrink: String?,
    val latestRecord: CoffeeRecordEntity?
)
