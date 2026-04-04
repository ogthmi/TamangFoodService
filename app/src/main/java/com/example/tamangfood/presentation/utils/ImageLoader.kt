package com.example.tamangfood.presentation.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.tamangfood.R

object ImageLoader {
    fun load(context: Context, imageView: ImageView, url: String?) {
        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.ic_default_avatar)
            .error(R.drawable.ic_default_avatar)
            .into(imageView)
    }
}