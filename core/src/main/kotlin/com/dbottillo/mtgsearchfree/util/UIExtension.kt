package com.dbottillo.mtgsearchfree.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun Context.dpToPx(value: Int): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), this.resources.displayMetrics).toInt()
}

fun Activity?.setLightStatusBar() {
    this?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
}

fun Activity?.setDarkStatusBar() {
    this?.window?.decorView?.systemUiVisibility = 0
}

fun View.setHeight(height: Int) {
    val lp = this.layoutParams
    if (lp.height != height) {
        lp.height = height
        this.layoutParams = lp
    }
}

@ColorInt
fun Context.themeColor(@AttrRes attribute: Int) = TypedValue().let {
    theme.resolveAttribute(attribute, it, true); it.data
}