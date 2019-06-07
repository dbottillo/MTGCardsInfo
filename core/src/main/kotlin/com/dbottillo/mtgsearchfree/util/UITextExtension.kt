package com.dbottillo.mtgsearchfree.util

import android.graphics.Typeface
import android.os.Build
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan

fun SpannableStringBuilder.addBold(input: String) {
    val start = length
    append(input)
    setSpan(StyleSpan(Typeface.BOLD), start, start + input.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

@Suppress("DEPRECATION")
fun String.toHtml(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }
}