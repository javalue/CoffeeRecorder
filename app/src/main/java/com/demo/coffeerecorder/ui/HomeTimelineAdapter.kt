package com.demo.coffeerecorder.ui

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.coffeerecorder.R
import com.demo.coffeerecorder.data.local.CoffeeRecordEntity

class HomeTimelineAdapter : RecyclerView.Adapter<HomeTimelineAdapter.HomeTimelineViewHolder>() {

    interface Listener {
        fun onRecordClicked(recordId: Long)
    }

    private val items = mutableListOf<CoffeeRecordEntity>()
    private var listener: Listener? = null

    fun submitItems(newItems: List<CoffeeRecordEntity>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeTimelineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_timeline, parent, false)
        return HomeTimelineViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeTimelineViewHolder, position: Int) {
        holder.bind(
            record = items[position],
            isLast = position == items.lastIndex,
            listener = listener
        )
    }

    override fun getItemCount(): Int = items.size

    class HomeTimelineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timeView: TextView = itemView.findViewById(R.id.tvTimelineTime)
        private val lineView: View = itemView.findViewById(R.id.viewTimelineLine)
        private val thumbnailView: TextView = itemView.findViewById(R.id.tvThumbnail)
        private val drinkTitleView: TextView = itemView.findViewById(R.id.tvDrinkTitle)
        private val ratingView: TextView = itemView.findViewById(R.id.tvRating)
        private val metaView: TextView = itemView.findViewById(R.id.tvMeta)
        private val subtitleView: TextView = itemView.findViewById(R.id.tvSubtitle)
        private val notesView: TextView = itemView.findViewById(R.id.tvNotes)

        fun bind(record: CoffeeRecordEntity, isLast: Boolean, listener: Listener?) {
            val context = itemView.context
            timeView.text = CoffeeFormatters.formatHourMinute(record.drankAt)
            lineView.visibility = if (isLast) View.INVISIBLE else View.VISIBLE

            val drinkTitle = if (record.drinkType.isBlank()) {
                record.beanName.ifBlank { context.getString(R.string.home_timeline_image_fallback) }
            } else {
                record.drinkType
            }
            drinkTitleView.text = drinkTitle
            thumbnailView.text = drinkTitle.firstOrNull()?.toString() ?: context.getString(R.string.home_icon_cup)
            ratingView.text = context.getString(
                R.string.home_timeline_rating_format,
                record.rating.toDouble()
            )
            metaView.text = context.getString(
                R.string.home_timeline_meta_format,
                record.beanName.ifBlank { drinkTitle },
                record.brewMethod.ifBlank { context.getString(R.string.summary_no_method) },
                record.cupSizeMl
            )
            subtitleView.text = context.getString(
                R.string.home_timeline_subtitle_format,
                record.roaster.ifBlank { context.getString(R.string.app_name) },
                record.origin.ifBlank { record.drinkType }
            )

            val noteText = if (record.notes.isBlank()) {
                record.beanName
            } else {
                context.getString(R.string.home_timeline_note_prefix) + record.notes
            }
            notesView.text = noteText
            notesView.visibility = if (TextUtils.isEmpty(noteText)) View.GONE else View.VISIBLE

            itemView.setOnClickListener {
                listener?.onRecordClicked(record.id)
            }
        }
    }
}
