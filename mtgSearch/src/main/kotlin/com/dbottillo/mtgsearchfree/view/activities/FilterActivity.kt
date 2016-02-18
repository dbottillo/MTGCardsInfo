package com.dbottillo.mtgsearchfree.view.activities

import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.helper.TrackingHelper
import com.dbottillo.mtgsearchfree.view.SlidingUpPanelLayout
import com.dbottillo.mtgsearchfree.view.fragments.FilterFragment

abstract class FilterActivity : BasicActivity(), SlidingUpPanelLayout.PanelSlideListener {

    internal var arrow: ImageView? = null
    public var slidingPanel: SlidingUpPanelLayout? = null

    var filterFragment: FilterFragment? = null
    private var secondListener: SlidingUpPanelLayout.PanelSlideListener? = null

    protected fun setupSlidingPanel() {
        slidingPanel = findViewById(R.id.sliding_layout) as SlidingUpPanelLayout
        slidingPanel!!.setPanelSlideListener(this)

        filterFragment = FilterFragment()
        supportFragmentManager.beginTransaction().replace(R.id.filter, filterFragment).commit()
    }

    protected fun hideSlidingPanel() {
        slidingPanel!!.panelHeight = 0
    }

    protected fun startAnimation(animation: Animation) {
        slidingPanel!!.startAnimation(animation)
    }

    fun collapseSlidingPanel() {
        slidingPanel!!.collapsePane()
    }

    protected fun expandSlidingPanel() {
        slidingPanel!!.expandPane()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (slidingPanel!!.isExpanded) {
            collapseSlidingPanel()
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

    fun onToggleClicked(view: View) {
        filterFragment!!.onToggleClicked(view)
    }

    fun addPanelSlideListener(listener: SlidingUpPanelLayout.PanelSlideListener) {
        secondListener = listener
    }

    protected fun setRotationArrow(angle: Float) {
        if (arrow == null) {
            arrow = findViewById(R.id.arrow_filter) as ImageView
        }
        arrow!!.rotation = angle
    }

    override fun onPanelSlide(panel: View, slideOffset: Float) {
        if (secondListener != null) {
            secondListener!!.onPanelSlide(panel, slideOffset)
        }
        setRotationArrow(180 - 180 * slideOffset)
    }

    override fun onPanelCollapsed(panel: View) {
        if (secondListener != null) {
            secondListener!!.onPanelCollapsed(panel)
        }
        TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_UI, "panel", "collapsed")
        setRotationArrow(0f)
    }

    override fun onPanelExpanded(panel: View) {
        if (secondListener != null) {
            secondListener!!.onPanelExpanded(panel)
        }
        TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_UI, "panel", "expanded")
        setRotationArrow(180f)
    }

    override fun onPanelAnchored(panel: View) {
        if (secondListener != null) {
            secondListener!!.onPanelAnchored(panel)
        }
    }
}

