package com.dbottillo.mtgsearchfree.helper

import android.content.res.Resources
import android.view.View
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.util.AnimationUtil
import com.dbottillo.mtgsearchfree.view.SlidingUpPanelLayout

class SlidingPanelHelper(var slidingPanel: SlidingUpPanelLayout,
                         var resources: Resources,
                         var listener: SlidingPanelHelperListener) : SlidingUpPanelLayout.PanelSlideListener {

    interface SlidingPanelHelperListener {
        fun onPanelChangeOffset(offset: Float)
    }

    fun init(dragView: View) {
        slidingPanel.setDragView(dragView)
        slidingPanel.setPanelSlideListener(this)
    }

    fun hidePanel(animate: Boolean) {
        if (animate) {
            AnimationUtil.animateSlidingPanelHeight(slidingPanel, 0)
        } else {
            slidingPanel.panelHeight = 0
        }
    }

    fun showPanel() {
        AnimationUtil.animateSlidingPanelHeight(slidingPanel, resources.getDimensionPixelSize(R.dimen.collapsedHeight))
    }


    override fun onPanelSlide(panel: View?, slideOffset: Float) {
        listener.onPanelChangeOffset(slideOffset)
    }

    override fun onPanelCollapsed(panel: View?) {
        listener.onPanelChangeOffset(1.0f)
    }

    override fun onPanelExpanded(panel: View?) {
        listener.onPanelChangeOffset(0.0f)
    }

    override fun onPanelAnchored(panel: View?) {

    }

    fun onBackPressed(): Boolean {
        if (slidingPanel.isExpanded){
            slidingPanel.collapsePane()
            return true
        }
        return false
    }

}