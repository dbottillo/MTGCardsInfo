package com.dbottillo.mtgsearchfree.toolbarereveal;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.ui.BasicFragment;
import com.dbottillo.mtgsearchfree.util.AnimationUtil;
import com.dbottillo.mtgsearchfree.util.UIUtilKt;

import java.lang.ref.WeakReference;

/**
 * Helper class that contains the logic to reveal the toolbar after the user is scrolling the content
 * This class expect the concrete fragment to specify a scrollview with its content and this class will
 * handle the transition to reveal the toolbar during the scroll
 */
public class ToolbarRevealScrollHelper implements ViewTreeObserver.OnScrollChangedListener {

    private static final String CURRENT_SCROLL = "currentScroll";
    private static final String MAXIMUM_SCROLL = "maximumScroll";
    private static final float THRESHOLD_STATUS_BAR_COLOR = 0.3f;
    private static final int OFFSET_MAXIMUM_SCROLL = 100;
    private static final int TITLE_TRANSLATION_START_Y = 30;

    private ViewGroup mViewGroup;
    private AnimationUtil.LinearInterpolator alphaInterpolator;
    private AnimationUtil.LinearInterpolator elevationInterpolator;
    private AnimationUtil.LinearInterpolator translationTitle;
    private AnimationUtil.ArgbInterpolator toolbarBackgroundEvaluator;
    private AnimationUtil.ArgbInterpolator statusBarColorEvaluator;
    private AnimationUtil.ArgbInterpolator arrowToolbarEvaluator;

    private boolean scrollingEnabled = false;
    private int currentScroll = 0;
    private int maximumScroll;

    private WeakReference<BasicFragment> fragment;
    private WeakReference<Context> context;
    private int scrollviewID;
    private int backgroundColor;
    private int heightToolbar;
    private boolean statusBarIncluded;
    private int toolbarColor;
    private int statusBarColor;

    public ToolbarRevealScrollHelper(BasicFragment baseFragment, int scrollviewID,
                                     int backgroundColor, int heightToolbar,
                                     boolean statusBarIncluded) {
        this(baseFragment, scrollviewID, backgroundColor, heightToolbar, statusBarIncluded, R.color.color_primary, R.color.color_primary);
    }

    public ToolbarRevealScrollHelper(BasicFragment baseFragment, int scrollviewID,
                                     int backgroundColor, int heightToolbar,
                                     boolean statusBarIncluded, int toolbarColor, int statusBarColor) {
        this.fragment = new WeakReference<>(baseFragment);
        this.scrollviewID = scrollviewID;
        this.backgroundColor = backgroundColor;
        this.heightToolbar = heightToolbar;
        this.statusBarIncluded = statusBarIncluded;
        this.context = new WeakReference<Context>(baseFragment.getActivity());
        this.toolbarColor = ContextCompat.getColor(baseFragment.getContext(), toolbarColor);
        this.statusBarColor = ContextCompat.getColor(baseFragment.getContext(), statusBarColor);
    }

    public void setToolbarColor(int color) {
        this.toolbarColor = color;
    }

    public void setStatusBarColor(int color) {
        this.statusBarColor = color;
    }

    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        BasicFragment instance = fragment.get();
        if (instance != null) {

            mViewGroup = view.findViewById(scrollviewID);
            instance.setupToolbar(view);
            maximumScroll = heightToolbar + UIUtilKt.dpToPx(view.getContext(), OFFSET_MAXIMUM_SCROLL);
            setupTitleAnimation(instance, view.getContext());

            if (mViewGroup instanceof ScrollView) {
                final ViewGroup mScrollViewContentLayout = (ViewGroup) mViewGroup.getChildAt(0);
                mScrollViewContentLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    @SuppressWarnings("checkstyle:parameternumber")
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        int totalScroll = v.getHeight() - mViewGroup.getHeight();
                        scrollingEnabled = totalScroll > 0;
                        mScrollViewContentLayout.setMinimumHeight(scrollingEnabled ? (mViewGroup.getHeight() + maximumScroll) : 0);

                        if (savedInstanceState != null) {
                            recalculateCurrentScrollBasedOnOldMaximumScroll(savedInstanceState);
                        }
                    }
                });
            }
            if (mViewGroup instanceof RecyclerView) {
                scrollingEnabled = true;

                if (savedInstanceState != null) {
                    recalculateCurrentScrollBasedOnOldMaximumScroll(savedInstanceState);
                }
            }
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_SCROLL, scrollingEnabled ? currentScroll : 0);
        outState.putInt(MAXIMUM_SCROLL, maximumScroll);
    }

    public void onResume() {
        if (mViewGroup != null) {
            mViewGroup.getViewTreeObserver().addOnScrollChangedListener(this);
        }
        refreshUI();
    }

    public void onPause() {
        BasicFragment instance = fragment.get();
        if (instance != null && context.get() != null) {
            UIUtilKt.setDarkStatusBar(instance.getActivity());
            if (statusBarIncluded) {
                instance.getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(context.get(), R.color.color_primary_dark));
            }
        }
        if (mViewGroup != null) {
            mViewGroup.getViewTreeObserver().removeOnScrollChangedListener(this);
        }
    }

    private void setupTitleAnimation(BasicFragment baseFragment, Context context) {
        int translationStart = UIUtilKt.dpToPx(context, TITLE_TRANSLATION_START_Y);
        int translationEnd = 0;
        baseFragment.getToolbar().setElevation(0);
        baseFragment.getToolbarTitle().setAlpha(0);
        baseFragment.getToolbarTitle().setTranslationY(translationStart);
        if (baseFragment.getToolbar().getNavigationIcon() != null) {
            baseFragment.getToolbar().getNavigationIcon().setTint(ContextCompat.getColor(context, R.color.color_primary));
        }
        setChildrenToolbarColor(baseFragment.getToolbar(), ContextCompat.getColor(context, R.color.color_primary));
        baseFragment.getToolbar().getOverflowIcon().setColorFilter(ContextCompat.getColor(context, R.color.color_primary), PorterDuff.Mode.SRC_IN);
        if (statusBarIncluded) {
            baseFragment.getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(baseFragment.getContext(), R.color.main_bg));
            UIUtilKt.setLightStatusBar(baseFragment.getActivity());
        }

        alphaInterpolator = AnimationUtil.INSTANCE.createLinearInterpolator().fromValue(0.0f).toValue(1.0f);
        elevationInterpolator = AnimationUtil.INSTANCE.createLinearInterpolator().fromValue(0.0f).toValue(context.getResources().getDimension(R.dimen.default_elevation_toolbar));
        translationTitle = AnimationUtil.INSTANCE.createLinearInterpolator().fromValue(translationStart).toValue(translationEnd);
        toolbarBackgroundEvaluator = AnimationUtil.INSTANCE.createArgbInterpolator().fromValue(ContextCompat.getColor(context, backgroundColor)).toValue(toolbarColor);
        statusBarColorEvaluator = AnimationUtil.INSTANCE.createArgbInterpolator().fromValue(ContextCompat.getColor(context, R.color.white)).toValue(statusBarColor);
        arrowToolbarEvaluator = AnimationUtil.INSTANCE.createArgbInterpolator().fromValue(ContextCompat.getColor(context, R.color.color_primary)).toValue(ContextCompat.getColor(context, R.color.white));

        if (mViewGroup instanceof ScrollView) {
            mViewGroup.setVerticalFadingEdgeEnabled(false);
            mViewGroup.setHorizontalFadingEdgeEnabled(false);
            mViewGroup.setFadingEdgeLength(0);
            mViewGroup.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
        }
    }

    private void setChildrenToolbarColor(ViewGroup viewGroup, int color) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ImageView) {
                ((ImageView) view).setColorFilter(color);
            } else if (view instanceof LinearLayout) {
                setChildrenToolbarColor((LinearLayout) view, color);
            }
        }
    }

    private void refreshUI() {
        BasicFragment instance = fragment.get();
        if (instance != null) {
            float interval = calculateInterval();
            instance.getToolbarTitle().setAlpha(alphaInterpolator.getInterpolation(interval));
            instance.getToolbar().setElevation(elevationInterpolator.getInterpolation(interval));
            instance.getToolbarTitle().setTranslationY(translationTitle.getInterpolation(interval));
            instance.getToolbar().setBackgroundColor(toolbarBackgroundEvaluator.getInterpolation(interval));
            setChildrenToolbarColor(instance.getToolbar(), arrowToolbarEvaluator.getInterpolation(interval));
            if (instance.getToolbar().getNavigationIcon() != null) {
                instance.getToolbar().getNavigationIcon().setTint(arrowToolbarEvaluator.getInterpolation(interval));
            }
            instance.getToolbar().getOverflowIcon().setColorFilter(arrowToolbarEvaluator.getInterpolation(interval), PorterDuff.Mode.SRC_IN);
            if (statusBarIncluded) {
                instance.getActivity().getWindow().setStatusBarColor(statusBarColorEvaluator.getInterpolation(interval));
                if (interval <= THRESHOLD_STATUS_BAR_COLOR) {
                    UIUtilKt.setLightStatusBar(instance.getActivity());
                } else {
                    UIUtilKt.setDarkStatusBar(instance.getActivity());
                }
            }
        }
    }

    private float calculateInterval() {
        float interval = (float) currentScroll / (float) maximumScroll;
        if (interval < 0.0f) {
            interval = 0.0f;
        } else if (interval > 1.0f) {
            interval = 1.0f;
        }
        if (!scrollingEnabled) {
            interval = 0.0f;
        }
        return interval;
    }

    private void recalculateCurrentScrollBasedOnOldMaximumScroll(Bundle bundle) {
        int oldCurrentScroll = bundle.getInt(CURRENT_SCROLL);
        int oldMaximumScroll = bundle.getInt(MAXIMUM_SCROLL);
        if (oldMaximumScroll == maximumScroll) {
            // after a backstack or a full restore we don't need to do anything
            currentScroll = oldCurrentScroll;
        } else {
            // after a rotation change the offset it's different because the maximum scroll is different (depends on the app bar height)
            float interval = (float) oldCurrentScroll / (float) oldMaximumScroll;
            currentScroll = (int) (maximumScroll * interval);
        }
        refreshUI();
    }

    @Override
    public void onScrollChanged() {
        if (mViewGroup instanceof RecyclerView) {
            currentScroll = ((RecyclerView) mViewGroup).computeVerticalScrollOffset();
        } else {
            currentScroll = mViewGroup.getScrollY();
        }
        refreshUI();
    }

    public void updateTitle(@org.jetbrains.annotations.Nullable String name) {
        BasicFragment instance = fragment.get();
        if (instance != null) {
            instance.setTitle(name);
        }
    }
}
