package com.module.utils.custom_views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.positron.teachers.R

class TextViewRobotoRegularBold : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context) : super(context) {
        setFontStyle(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setFontStyle(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setFontStyle(context)
    }

    private fun setFontStyle(context: Context?) {
        val typeface = ResourcesCompat.getFont(context!!, R.font.roboto_regular)
        setTypeface(typeface, Typeface.BOLD)
    }
}