package com.demo.coffeerecorder.ui

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView

object CoffeePhotoLoader {

    @JvmStatic
    fun bindThumbnail(
        imageView: ImageView,
        fallbackView: TextView,
        photoUri: String?,
        fallbackText: String
    ) {
        if (photoUri.isNullOrBlank()) {
            imageView.setImageDrawable(null)
            imageView.visibility = android.view.View.GONE
            fallbackView.text = fallbackText
            fallbackView.visibility = android.view.View.VISIBLE
            return
        }

        runCatching {
            imageView.setImageURI(Uri.parse(photoUri))
        }.onSuccess {
            if (imageView.drawable != null) {
                imageView.visibility = android.view.View.VISIBLE
                fallbackView.visibility = android.view.View.GONE
            } else {
                imageView.visibility = android.view.View.GONE
                fallbackView.text = fallbackText
                fallbackView.visibility = android.view.View.VISIBLE
            }
        }.onFailure {
            imageView.setImageDrawable(null)
            imageView.visibility = android.view.View.GONE
            fallbackView.text = fallbackText
            fallbackView.visibility = android.view.View.VISIBLE
        }
    }
}
