package com.dbottillo.mtgsearchfree.home.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.dbottillo.mtgsearchfree.home.R
import com.dbottillo.mtgsearchfree.util.inflate
import kotlinx.android.synthetic.main.view_new_update_banner.view.*

class NewUpdateBannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var closeListener: (() -> Unit)? = null
    var actionListener: (() -> Unit)? = null

    init {
        inflate(resource = R.layout.view_new_update_banner, attachToRoot = true)

        text_title.text = context.getString(R.string.release_note_title_push)

        findViewById<View>(R.id.close).setOnClickListener {
            closeListener?.invoke()
        }
        findViewById<View>(R.id.bg).setOnClickListener {
            actionListener?.invoke()
        }
    }
}
