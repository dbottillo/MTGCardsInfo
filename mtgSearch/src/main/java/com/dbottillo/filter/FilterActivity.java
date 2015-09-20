package com.dbottillo.filter;

import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.dbottillo.R;
import com.dbottillo.base.DBActivity;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.view.SlidingUpPanelLayout;

public class FilterActivity extends DBActivity implements SlidingUpPanelLayout.PanelSlideListener {

    ImageView arrow;
    private SlidingUpPanelLayout slidingPanel;

    private FilterFragment filterFragment;
    private SlidingUpPanelLayout.PanelSlideListener secondListener;

    protected void setupSlidingPanel() {
        slidingPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingPanel.setPanelSlideListener(this);

        filterFragment = new FilterFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.filter, filterFragment)
                .commit();

    }

    public FilterFragment getFilterFragment() {
        return filterFragment;
    }

    protected void hideSlidingPanel() {
        slidingPanel.setPanelHeight(0);
    }

    protected void startAnimation(Animation animation) {
        slidingPanel.startAnimation(animation);
    }

    public void collapseSlidingPanel() {
        slidingPanel.collapsePane();
    }

    protected void expandSlidingPanel() {
        slidingPanel.expandPane();
    }


    public SlidingUpPanelLayout getSlidingPanel() {
        return slidingPanel;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (slidingPanel.isExpanded()) {
            collapseSlidingPanel();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onToggleClicked(View view) {
        filterFragment.onToggleClicked(view);
    }

    @Override
    public String getPageTrack() {
        return null;
    }

    public void addPanelSlideListener(SlidingUpPanelLayout.PanelSlideListener listener) {
        secondListener = listener;
    }

    protected void setRotationArrow(float angle) {
        if (arrow == null) {
            arrow = (ImageView) findViewById(R.id.arrow_filter);
        }
        arrow.setRotation(angle);
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        if (secondListener != null) {
            secondListener.onPanelSlide(panel, slideOffset);
        }
        setRotationArrow(180 - (180 * slideOffset));
    }

    @Override
    public void onPanelCollapsed(View panel) {
        if (secondListener != null) {
            secondListener.onPanelCollapsed(panel);
        }
        TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_UI, "panel", "collapsed");
        setRotationArrow(0);
    }

    @Override
    public void onPanelExpanded(View panel) {
        if (secondListener != null) {
            secondListener.onPanelExpanded(panel);
        }
        TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_UI, "panel", "expanded");
        setRotationArrow(180);
    }

    @Override
    public void onPanelAnchored(View panel) {
        if (secondListener != null) {
            secondListener.onPanelAnchored(panel);
        }
    }
}
