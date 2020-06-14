package com.dbottillo.mtgsearchfree.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.dbottillo.mtgsearchfree.Constants

class MTGCardImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = (measuredWidth * Constants.RATIO_CARD).toInt()
        setMeasuredDimension(measuredWidth, height)
    }
}