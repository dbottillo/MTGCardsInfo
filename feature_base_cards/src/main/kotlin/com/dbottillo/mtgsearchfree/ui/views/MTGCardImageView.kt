package com.dbottillo.mtgsearchfree.ui.views

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import android.util.AttributeSet
import com.dbottillo.mtgsearchfree.Constants.RATIO_CARD

class MTGCardImageView : AppCompatImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = (measuredWidth * RATIO_CARD).toInt()
        setMeasuredDimension(measuredWidth, height)
    }
}