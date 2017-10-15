package com.dbottillo.mtgsearchfree.util

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.dbottillo.mtgsearchfree.ui.views.MTGCardView

fun Context.dpToPx(value: Int): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), this.resources.displayMetrics).toInt()
}

fun View.setHeight(height: Int) {
    val lp = this.layoutParams
    if (lp.height != height) {
        lp.height = height
        this.layoutParams = lp
    }
}

fun View.setMarginTop(value: Int) {
    val lp = this.layoutParams as ViewGroup.MarginLayoutParams
    if (lp.topMargin != value) {
        lp.topMargin = value
        this.layoutParams = lp
    }
}

fun ImageView.calculateSizeCardImage(widthAvailable: Int, isTablet: Boolean) {
    var wImage = widthAvailable
    var hImage = (widthAvailable * MTGCardView.RATIO_CARD).toInt()
    if (isTablet) {
        wImage = (wImage * 0.8).toInt()
        hImage = (hImage * 0.8).toInt()
    }
    val par = this.layoutParams as RelativeLayout.LayoutParams
    par.width = wImage
    par.height = hImage
    this.layoutParams = par
}

fun TextView.setBoldAndItalic(input: String) {
    val spannable = SpannableStringBuilder(input)
    spannable.setSpan(StyleSpan(Typeface.BOLD), 0, input.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    spannable.setSpan(StyleSpan(Typeface.ITALIC), 0, input.length, 0)
    text = spannable
}

fun SpannableStringBuilder.addBold(input: String) {
    val start = length
    append(input)
    setSpan(StyleSpan(Typeface.BOLD), start, start + input.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun SpannableStringBuilder.newLine(total: Int = 1) {
    (1..total).forEach {
        append("\n")
    }
}

fun SpannableStringBuilder.boldTitledEntry(title: String, entry: String) {
    addBold(title)
    append(":").append(" ").append(entry).newLine(2)
}