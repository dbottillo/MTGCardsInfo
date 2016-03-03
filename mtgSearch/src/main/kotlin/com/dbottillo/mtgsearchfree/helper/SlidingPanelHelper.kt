package com.dbottillo.mtgsearchfree.helper

import android.content.res.Resources
import android.view.View
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.util.AnimationUtil
import com.dbottillo.mtgsearchfree.view.SlidingUpPanelLayout

class SlidingPanelHelper(var slidingPanel: SlidingUpPanelLayout, var resources: Resources) {

    fun setDragView(dragView: View){
        slidingPanel.setDragView(dragView)
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

    fun closePanel() {
        slidingPanel.collapsePane()
    }


}