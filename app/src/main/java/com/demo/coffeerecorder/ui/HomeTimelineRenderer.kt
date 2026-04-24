package com.demo.coffeerecorder.ui

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.demo.coffeerecorder.R
import com.demo.coffeerecorder.data.local.CoffeeRecordEntity

object HomeTimelineRenderer {

    fun bind(
        itemView: View,
        record: CoffeeRecordEntity,
        isLast: Boolean,
        onRecordClicked: (Long) -> Unit
    ) {
        val context = itemView.context
        val timeView: TextView = itemView.findViewById(R.id.tvTimelineTime)
        val lineView: View = itemView.findViewById(R.id.viewTimelineLine)
        val imageView: ImageView = itemView.findViewById(R.id.imageThumbnail)
        val thumbnailView: TextView = itemView.findViewById(R.id.tvThumbnail)
        val drinkTitleView: TextView = itemView.findViewById(R.id.tvDrinkTitle)
        val ratingView: TextView = itemView.findViewById(R.id.tvRating)
        val metaView: TextView = itemView.findViewById(R.id.tvMeta)
        val subtitleView: TextView = itemView.findViewById(R.id.tvSubtitle)
        val notesView: TextView = itemView.findViewById(R.id.tvNotes)

        timeView.text = CoffeeFormatters.formatHourMinute(record.drankAt)
        lineView.visibility = if (isLast) View.INVISIBLE else View.VISIBLE

        val drinkType = record.drinkType.orEmpty()
        val beanName = record.beanName.orEmpty()
        val brewMethod = record.brewMethod.orEmpty()
        val roaster = record.roaster.orEmpty()
        val origin = record.origin.orEmpty()
        val notes = record.notes.orEmpty()

        val drinkTitle = if (drinkType.isBlank()) {
            beanName.ifBlank { context.getString(R.string.home_timeline_image_fallback) }
        } else {
            drinkType
        }
        drinkTitleView.text = drinkTitle
        CoffeePhotoLoader.bindThumbnail(
            imageView = imageView,
            fallbackView = thumbnailView,
            photoUri = record.photoUri,
            fallbackText = drinkTitle.firstOrNull()?.toString() ?: context.getString(R.string.home_icon_cup)
        )
        ratingView.text = context.getString(
            R.string.home_timeline_rating_format,
            record.rating.toDouble()
        )
        metaView.text = context.getString(
            R.string.home_timeline_meta_format,
            beanName.ifBlank { drinkTitle },
            brewMethod.ifBlank { context.getString(R.string.summary_no_method) },
            record.cupSizeMl
        )
        subtitleView.text = context.getString(
            R.string.home_timeline_subtitle_format,
            roaster.ifBlank { context.getString(R.string.app_name) },
            origin.ifBlank { drinkType }
        )

        val noteText = if (notes.isBlank()) {
            beanName
        } else {
            context.getString(R.string.home_timeline_note_prefix) + notes
        }
        notesView.text = noteText
        notesView.visibility = if (TextUtils.isEmpty(noteText)) View.GONE else View.VISIBLE

        itemView.setOnClickListener {
            onRecordClicked(record.id)
        }
    }
}
