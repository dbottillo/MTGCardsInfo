package com.dbottillo.mtgsearchfree.util

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment
import androidx.core.app.NavUtils
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.appcompat.app.AppCompatActivity
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.dbottillo.mtgsearchfree.ui.views.RATIO_CARD

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
    var hImage = (widthAvailable * RATIO_CARD).toInt()
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

fun SpannableStringBuilder.newLine(total: Int = 1) {
    (1..total).forEach {
        append("\n")
    }
}

fun SpannableStringBuilder.boldTitledEntry(title: String, entry: String) {
    addBold(title)
    append(":").append(" ").append(entry).newLine(2)
}

fun Activity.goToParentActivity() {
    NavUtils.getParentActivityIntent(this)?.let {
        if (NavUtils.shouldUpRecreateTask(this, it) || isTaskRoot) {
            TaskStackBuilder.create(this).addNextIntentWithParentStack(it).startActivities()
        } else {
            NavUtils.navigateUpTo(this, it)
        }
    }
}

fun <T : View> Activity.bind(@IdRes idRes: Int): Lazy<T> {
    @Suppress("UNCHECKED_CAST")
    return unsafeLazy { findViewById<T>(idRes) }
}

private fun <T> unsafeLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun MenuItem.setTintColor(context: Context, color: Int) {
    val wrapDrawable = DrawableCompat.wrap(icon)
    DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(context, color))
    icon = wrapDrawable
}

fun Activity?.setLightStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}

fun Activity?.setDarkStatusBar() {
    this?.window?.decorView?.systemUiVisibility = 0
}

fun AppCompatActivity.show(tag: String, fragment: DialogFragment) {
    LOG.d()
    val ft = supportFragmentManager.beginTransaction()
    val prev = supportFragmentManager.findFragmentByTag(tag)
    if (prev != null) {
        ft.remove(prev)
    }
    ft.addToBackStack(null)
    fragment.show(ft, tag)
}