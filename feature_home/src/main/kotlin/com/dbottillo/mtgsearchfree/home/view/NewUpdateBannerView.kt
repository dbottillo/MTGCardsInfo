package com.dbottillo.mtgsearchfree.home.view

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.dbottillo.mtgsearchfree.home.R
import com.dbottillo.mtgsearchfree.util.inflate

class NewUpdateBannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        inflate(resource = R.layout.view_new_update_banner, attachToRoot = true)

        findViewById<TextView>(R.id.text_title).text = context.getString(R.string.app_closed_title)
        findViewById<TextView>(R.id.text).text = context.getString(R.string.app_closed_text)
    }
}
