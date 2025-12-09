package com.positron.teachers.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.positron.teachers.R


class MyProgressBar(context: Context) : Dialog(context) {
    init {
        setContentView(R.layout.progress_bar_design)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        val imageView = findViewById<ImageView>(R.id.imageViewProgress)
        Glide.with(context).load(R.drawable.progress_unscreen).into(imageView)
        if (window != null) window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}