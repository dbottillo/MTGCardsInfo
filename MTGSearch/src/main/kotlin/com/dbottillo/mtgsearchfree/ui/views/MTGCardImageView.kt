package com.dbottillo.mtgsearchfree.ui.views

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet

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
