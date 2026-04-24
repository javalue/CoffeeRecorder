package com.demo.coffeerecorder.ui

import android.content.Context
import com.demo.coffeerecorder.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CoffeeFormatters {

    private val timeFormatter = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())

    @JvmStatic
    fun formatDateTime(timestamp: Long): String {
        return timeFormatter.format(Date(timestamp))
    }

    @JvmStatic
    fun formatPrice(context: Context, value: Double): String {
        return context.getString(R.string.price_format, value)
    }

    fun formatAverageRating(context: Context, value: Double): String {
        return context.getString(R.string.average_rating_format, value)
    }

    fun formatCupSize(context: Context, value: Int): String {
        return context.getString(R.string.avg_cup_size_format, value)
    }
}
