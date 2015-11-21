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
import android.widget.Toast;

import com.dbottillo.R;
import com.dbottillo.base.DBActivity;
import com.dbottillo.communication.DataManager;
import com.dbottillo.communication.events.SetEvent;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.util.AnimationUtil;
import com.dbottillo.util.MaterialWrapper;
import com.dbottillo.util.UIUtil;
import com.dbottillo.view.MTGSearchView;

import butterknife.ButterKnife;

public class SearchActivity extends DBActivity implements View.OnClickListener {

    ImageButton newSearch;
    AnimationDrawable newSearchAnimation;
    ScrollView scrollView;
    boolean searchOpen = false;
    FrameLayout resultsContainer;
    View mainContainer;

    MTGSearchView searchView;
    Toolbar secondToolbar;

    ArgbEvaluator argbEvaluator;

    int sizeBig = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_search);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setTitle(R.string.action_search);
        secondToolbar = (Toolbar) findViewById(R.id.second_toolbar);
        secondToolbar.setNavigationIcon(R.drawable.ic_close);
        secondToolbar.setTitle(R.string.search_result);
        secondToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mainContainer = findViewById(R.id.main_container);
        resultsContainer = (FrameLayout) findViewById(R.id.fragment_container);
        newSearch = (ImageButton) findViewById(R.id.action_search);
        scrollView = (ScrollView) findViewById(R.id.search_scroll_view);
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                sizeBig = scrollView.getHeight();
                UIUtil.setMarginTop(resultsContainer, sizeBig);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    scrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
        toolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                sizeToolbar = toolbar.getHeight();
                secondToolbar.setY(-sizeToolbar);
                secondToolbar.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    toolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    toolbar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    computeScrollChanged(scrollY);
                }
            });
        } else {
            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    computeScrollChanged(scrollView.getScrollY());
                }
            });
        }

        argbEvaluator = new ArgbEvaluator();

        newSearch.setOnClickListener(this);

        newSearch.setBackgroundResource(R.drawable.anim_search_icon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            newSearch.setElevation(6.0f); // TODO: pre-lollipop version
        }

        searchView = (MTGSearchView) findViewById(R.id.search_view);

        DataManager.execute(DataManager.TASK.SET_LIST);
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
        if (!event.isError()) {
            searchView.refreshSets(event.getResult());
        }
        bus.removeStickyEvent(event);
    }

    private void doSearch(SearchParams searchParams) {
        TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_SEARCH, "done", searchParams.toString());
        changeFragment(SearchFragment.newInstance(searchParams), "search", false);
        hideIme();
    }

    @Override
    public void onClick(View v) {
        final SearchParams searchParams = searchView.getSearchParams();
        if (!searchParams.isValid()) {
            Toast.makeText(this, getString(R.string.minimum_search), Toast.LENGTH_SHORT).show();
            return;
        }
        final AnimationUtil.LinearInterpolator backgroundInterpolator = AnimationUtil.createLinearInterpolator();
        if (!searchOpen) {
            newSearch.setBackgroundResource(R.drawable.anim_search_icon);
            backgroundInterpolator.fromValue(sizeBig).toValue(0);
        } else {
            newSearch.setBackgroundResource(R.drawable.anim_search_icon_reverse);
            backgroundInterpolator.fromValue(0).toValue(sizeBig);
        }
        newSearchAnimation = (AnimationDrawable) newSearch.getBackground();
        newSearchAnimation.start();
        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                int val = (int) backgroundInterpolator.getInterpolation(interpolatedTime);
                UIUtil.setHeight(scrollView, val);
                int margin = sizeBig - (sizeBig - val);
                UIUtil.setMarginTop(resultsContainer, margin);
            }
        };
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (!searchOpen) {
                    resultsContainer.setVisibility(View.VISIBLE);
                    MaterialWrapper.copyElevation(secondToolbar, toolbar);
                    secondToolbar.animate().setDuration(100).translationY(0).start();
                } else {
                    secondToolbar.animate().setDuration(100).translationY(-sizeToolbar).start();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (searchOpen) {
                    resultsContainer.setVisibility(View.GONE);
                } else {
                    doSearch(searchParams);
                }
                searchOpen = !searchOpen;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim.setDuration(200);
        scrollView.startAnimation(anim);
    }

    private void computeScrollChanged(int amount) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(amount < 200 ? 9 * ((float) amount / (float) 200) : 9);
        }
        int color = (Integer) argbEvaluator.evaluate(amount < 400 ? ((float) amount / (float) 400) : 1, getResources().getColor(R.color.color_primary), getResources().getColor(R.color.color_primary_slightly_dark));
        scrollView.setBackgroundColor(color);
    }

    @Override
    public void onBackPressed() {
        if (searchOpen) {
            newSearch.callOnClick();
        } else {
            super.onBackPressed();
        }
    }
}
