package com.dbottillo.mtgsearchfree.view.activities

import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.base.MTGApp
import com.dbottillo.mtgsearchfree.presenter.CardFilterPresenter
import com.dbottillo.mtgsearchfree.resources.CardFilter
import com.dbottillo.mtgsearchfree.view.CardFilterView
import com.dbottillo.mtgsearchfree.view.SlidingUpPanelLayout
import com.dbottillo.mtgsearchfree.view.views.FilterPickerView
import javax.inject.Inject

abstract class FilterActivity : BasicActivity(), SlidingUpPanelLayout.PanelSlideListener {

    var slidingPanel: SlidingUpPanelLayout? = null

    var filterView: FilterPickerView? = null
    private var secondListener: SlidingUpPanelLayout.PanelSlideListener? = null

    protected fun setupSlidingPanel(dragView: View) {
        slidingPanel = findViewById(R.id.sliding_layout) as SlidingUpPanelLayout
        slidingPanel?.setPanelSlideListener(this)
        slidingPanel?.setDragView(dragView)
        filterView = findViewById(R.id.filter) as FilterPickerView
    }

    protected fun hideSlidingPanel() {
        slidingPanel?.panelHeight = 0
    }

    protected fun startAnimation(animation: Animation) {
        slidingPanel?.startAnimation(animation)
    }

    fun collapseSlidingPanel() {
        slidingPanel?.collapsePane()
    }

    protected fun expandSlidingPanel() {
        slidingPanel?.expandPane()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (slidingPanel!!.isExpanded) {
            collapseSlidingPanel()
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

    /*fun onToggleClicked(view: View) {
        filterFragment!!.onToggleClicked(view)
    }*/

    fun addPanelSlideListener(listener: SlidingUpPanelLayout.PanelSlideListener) {
        secondListener = listener
    }

    override fun onPanelSlide(panel: View, offset: Float) {
        secondListener?.onPanelSlide(panel, offset)
        setRotationArrow(180 - 180 * offset)
    }

    override fun onPanelCollapsed(panel: View) {
        secondListener?.onPanelCollapsed(panel)
        setRotationArrow(0f)
    }

    override fun onPanelExpanded(panel: View) {
        secondListener?.onPanelExpanded(panel)
        setRotationArrow(180f)
    }

    override fun onPanelAnchored(panel: View) {
        secondListener?.onPanelAnchored(panel)
    }

    protected fun setRotationArrow(angle: Float) {
        filterView?.setRotationArrow(angle)
    }
}

