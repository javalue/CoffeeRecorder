package com.demo.coffeerecorder.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.demo.coffeerecorder.R
import java.time.LocalDate

data class HomeDateItem(
    val date: LocalDate,
    val weekdayLabel: String,
    val dayOfMonth: Int,
    val hasRecord: Boolean,
    val isSelected: Boolean
)

class HomeDateAdapter(
    private val onDateClicked: (LocalDate) -> Unit
) : RecyclerView.Adapter<HomeDateAdapter.DateViewHolder>() {

    private val items = mutableListOf<HomeDateItem>()

    fun submitItems(newItems: List<HomeDateItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_date, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(items[position], onDateClicked)
    }

    override fun getItemCount(): Int = items.size

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val weekdayView: TextView = itemView.findViewById(R.id.tvWeekday)
        private val dayView: TextView = itemView.findViewById(R.id.tvDayOfMonth)
        private val indicatorView: View = itemView.findViewById(R.id.viewIndicator)
        private val containerView: FrameLayout = itemView.findViewById(R.id.layoutDateContainer)

        fun bind(item: HomeDateItem, onDateClicked: (LocalDate) -> Unit) {
            weekdayView.text = item.weekdayLabel
            dayView.text = item.dayOfMonth.toString()
            indicatorView.visibility = if (item.hasRecord) View.VISIBLE else View.INVISIBLE

            val context = itemView.context
            if (item.isSelected) {
                containerView.background = ContextCompat.getDrawable(context, R.drawable.bg_home_date_selected)
                dayView.setTextColor(ContextCompat.getColor(context, R.color.white))
                weekdayView.setTextColor(ContextCompat.getColor(context, R.color.coffee_text))
            } else {
                containerView.background = null
                dayView.setTextColor(ContextCompat.getColor(context, R.color.coffee_text))
                weekdayView.setTextColor(ContextCompat.getColor(context, R.color.coffee_text_muted))
            }

            itemView.setOnClickListener {
                onDateClicked(item.date)
            }
        }
    }
}
