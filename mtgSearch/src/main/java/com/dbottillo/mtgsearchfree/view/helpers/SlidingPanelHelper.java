package com.dbottillo.mtgsearchfree.view.helpers;

import android.content.res.Resources;
import android.view.View;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.util.AnimationUtil;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.views.SlidingUpPanelLayout;

public class SlidingPanelHelper implements SlidingUpPanelLayout.PanelSlideListener {

    public interface SlidingPanelHelperListener {
        void onPanelChangeOffset(float Float);
    }

    SlidingUpPanelLayout slidingPanel;
    Resources resources;
    SlidingPanelHelperListener listener;

    public SlidingPanelHelper(SlidingUpPanelLayout slidingPanel,
                              Resources resources,
                              SlidingPanelHelperListener listener) {
        this.slidingPanel = slidingPanel;
        this.resources = resources;
        this.listener = listener;
    }


    public void init(View dragView) {
        LOG.d();
        slidingPanel.setDragView(dragView);
        slidingPanel.setPanelSlideListener(this);
    }

    public void hidePanel(Boolean animate) {
        if (animate) {
            AnimationUtil.animateSlidingPanelHeight(slidingPanel, 0);
        } else {
            slidingPanel.setPanelHeight(0);
        }
    }

    public void showPanel() {
        AnimationUtil.animateSlidingPanelHeight(slidingPanel, resources.getDimensionPixelSize(R.dimen.collapsedHeight));
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        listener.onPanelChangeOffset(slideOffset);
    }

    @Override
    public void onPanelCollapsed(View panel) {
        listener.onPanelChangeOffset(1.0f);
    }

    @Override
    public void onPanelExpanded(View panel) {
        listener.onPanelChangeOffset(0.0f);
    }

    @Override
    public void onPanelAnchored(View panel) {

    }

    public boolean isPanelOpen(){
        return slidingPanel.isExpanded();
    }

    public void closePanel(){
        slidingPanel.collapsePane();
    }

    public boolean onBackPressed() {
        if (slidingPanel.isExpanded()) {
            slidingPanel.collapsePane();
            return true;
        }
        return false;
    }

}
