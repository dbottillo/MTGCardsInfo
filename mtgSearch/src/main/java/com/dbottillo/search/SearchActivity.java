package com.dbottillo.search;

import android.animation.ArgbEvaluator;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ScrollView;

import com.dbottillo.R;
import com.dbottillo.base.DBActivity;
import com.dbottillo.communication.DataManager;
import com.dbottillo.communication.events.SetEvent;
import com.dbottillo.util.AnimationUtil;
import com.dbottillo.util.UIUtil;
import com.dbottillo.view.MTGSearchView;

import butterknife.ButterKnife;

public class SearchActivity extends DBActivity implements View.OnClickListener {

    ImageButton newSearch;
    AnimationDrawable newSearchAnimation;
    ScrollView scrollView;
    boolean searchOpen = false;
    FrameLayout resultsContainer;
    ArgbEvaluator argbEvaluator;

    MTGSearchView searchView;


    int sizeBig = 0;
    //int totalHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_search);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setTitle(R.string.action_search);
        setSupportActionBar(toolbar);

        argbEvaluator = new ArgbEvaluator();

        resultsContainer = (FrameLayout) findViewById(R.id.fragment_container);
        newSearch = (ImageButton) findViewById(R.id.action_search);
        scrollView = (ScrollView) findViewById(R.id.search_scroll_view);
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                sizeBig = scrollView.getHeight();
                //totalHeight = findViewById(R.id.main_container).getHeight();
                UIUtil.setMarginTop(resultsContainer, sizeBig);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    scrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        newSearch.setOnClickListener(this);

        newSearch.setBackgroundResource(R.drawable.anim_search_icon);
        newSearch.setElevation(6.0f); // TODO: pre-lollipop version

        searchView = (MTGSearchView) findViewById(R.id.search_view);

        DataManager.execute(DataManager.TASK.SET_LIST);

/*
        setupToolbar();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
*/


        /*query = "counter";
        doSearch();*/
    }

    @Override
    public String getPageTrack() {
        return "/search_main";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onEventMainThread(SetEvent event) {
        if (!event.isError()){
            searchView.refreshSets(event.getResult());
        }
        bus.removeStickyEvent(event);
    }

    private void doSearch() {
        // TODO: check minimum information for performing a search
        /*TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_SEARCH, "done", query);
        if (query.length() < 3) {
            Toast.makeText(this, getString(R.string.minimum_search), Toast.LENGTH_SHORT).show();
            return;
        }
        if (searchEditText != null) {
            searchEditText.setText(query);
        }*/
        changeFragment(SearchFragment.newInstance(searchView.getSearchParams()), "search", false);
        hideIme();
    }

    @Override
    public void onClick(View v) {
        //ValueAnimator anim;
        final AnimationUtil.LinearInterpolator backgroundInterpolator = AnimationUtil.createLinearInterpolator();
        if (!searchOpen) {
            // anim = ValueAnimator.ofInt(sizeBig, sizeToolbar);
            newSearch.setBackgroundResource(R.drawable.anim_search_icon);
            backgroundInterpolator.fromValue(sizeBig).toValue(sizeToolbar);
            doSearch();
        } else {
            //  anim = ValueAnimator.ofInt(sizeToolbar, sizeBig);
            newSearch.setBackgroundResource(R.drawable.anim_search_icon_reverse);
            backgroundInterpolator.fromValue(sizeToolbar).toValue(sizeBig);
        }
        newSearchAnimation = (AnimationDrawable) newSearch.getBackground();
        newSearchAnimation.start();
       /* anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                UIUtil.setHeight(scrollView, val);
                int margin = totalHeight - (totalHeight - val);
                UIUtil.setMarginTop(resultsContainer, margin);
            }

        });*/

        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                int val = (int) backgroundInterpolator.getInterpolation(interpolatedTime);
                UIUtil.setHeight(scrollView, val);
                int margin = sizeBig - (sizeBig - val);
                UIUtil.setMarginTop(resultsContainer, margin);
                int color;
                float alpha;
                if (searchOpen) {
                    alpha = 1.0f - interpolatedTime;
                    color = (Integer) argbEvaluator.evaluate(1.0f - interpolatedTime, getResources().getColor(R.color.color_primary), getResources().getColor(R.color.color_accent));
                } else {
                    alpha = interpolatedTime;
                    color = (Integer) argbEvaluator.evaluate(1.0f - interpolatedTime, getResources().getColor(R.color.color_accent), getResources().getColor(R.color.color_primary));
                }
                if (interpolatedTime < 1.0f) {
                    scrollView.setBackgroundColor(color);
                }
            }
        };
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (searchOpen) {
                    toolbar.setTitle(R.string.action_search);
                } else {
                    toolbar.setTitle(R.string.search_result);
                }
                searchOpen = !searchOpen;
               // scrollView.smoothScrollTo(0,0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim.setDuration(200);
        scrollView.startAnimation(anim);
    }
}
