package com.dbottillo.mtgsearchfree.toolbarereveal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.ui.BaseDrawerActivity;
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment;

/**
 * Abstract class that let fragments access the {@link ToolbarRevealScrollHelper} seamless
 */
public abstract class ToolbarRevealScrollFragment extends BasicFragment {

    private ToolbarRevealScrollHelper toolbarRevealScrollHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbarRevealScrollHelper = new ToolbarRevealScrollHelper(this, getScrollViewId(),
                R.color.white, heightToolbar, isStatusBarIncludedInReveal());
    }

    /**
     * The concrete fragment needs to provide the scrollview id of its content
     *
     * @return id of the scrollview
     */
    public abstract int getScrollViewId();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbarRevealScrollHelper.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        toolbarRevealScrollHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbarRevealScrollHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        toolbarRevealScrollHelper.onPause();
    }

    boolean isStatusBarIncludedInReveal() {
        return true;
    }
}

