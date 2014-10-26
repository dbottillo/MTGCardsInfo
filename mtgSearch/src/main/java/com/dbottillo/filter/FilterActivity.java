package com.dbottillo.filter;

import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.dbottillo.BuildConfig;
import com.dbottillo.R;
import com.dbottillo.base.DBActivity;
import com.dbottillo.view.SlidingUpPanelLayout;

public class FilterActivity extends DBActivity {

    ImageView arrow;
    private SlidingUpPanelLayout slidingPanel;

    private FilterFragment filterFragment;

    protected void setupSlidingPanel(SlidingUpPanelLayout.PanelSlideListener listener) {
        slidingPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingPanel.setPanelSlideListener(listener);

        if (BuildConfig.magic) {
            filterFragment = new FilterFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.filter, filterFragment)
                    .commit();
        } else {
            hideSlidingPanel();
        }

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

    protected void collapseSlidingPanel() {
        slidingPanel.collapsePane();
    }

    protected void expandSlidingPanel() {
        slidingPanel.expandPane();
    }

    protected void setRotationArrow(float angle) {
        if (arrow == null) arrow = (ImageView) findViewById(R.id.arrow_filter);
        arrow.setRotation(angle);
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
}
