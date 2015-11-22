package com.dbottillo.base;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dbottillo.R;
import com.dbottillo.adapters.GameSetAdapter;
import com.dbottillo.cards.MTGSetFragment;
import com.dbottillo.communication.DataManager;
import com.dbottillo.communication.events.SetEvent;
import com.dbottillo.filter.FilterActivity;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.resources.MTGSet;
import com.dbottillo.util.DialogUtil;
import com.dbottillo.view.SlidingUpPanelLayout;

import java.util.ArrayList;

public class MainFragment extends MTGSetFragment implements SlidingUpPanelLayout.PanelSlideListener, DialogUtil.SortDialogListener {

    private ArrayList<MTGSet> sets;
    private GameSetAdapter setAdapter;
    private ImageView setArrow;
    private View setListBg;
    private ListView setList;
    private int currentSetPosition = -1;
    private View container;
    TextView chooserName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        setActionBarTitle(getString(R.string.app_long_name));
        setupSetFragment(rootView);

        this.container = rootView.findViewById(R.id.container);
        setListBg = rootView.findViewById(R.id.set_list_bg);
        setList = (ListView) rootView.findViewById(R.id.set_list);
        setArrow = (ImageView) rootView.findViewById(R.id.set_arrow);
        chooserName = ((TextView) rootView.findViewById(R.id.set_chooser_name));

        setListBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setList.getHeight() > 0) {
                    showHideSetList(false);
                }
            }
        });

        rootView.findViewById(R.id.cards_sort).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.chooseSortDialog(getContext(), getSharedPreferences(), MainFragment.this);
            }
        });


        if (savedInstanceState == null) {
            sets = new ArrayList<>();
            DataManager.execute(DataManager.TASK.SET_LIST);

        } else {
            sets = savedInstanceState.getParcelableArrayList("SET");
            currentSetPosition = savedInstanceState.getInt("currentSetPosition");
            if (currentSetPosition < 0) {
                DataManager.execute(DataManager.TASK.SET_LIST);
            } else {
                loadSet();
            }
        }

        setAdapter = new GameSetAdapter(getActivity(), sets);
        setAdapter.setCurrent(currentSetPosition);
        setList.setAdapter(setAdapter);
        setList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentSetPosition != position) {
                    currentSetPosition = position;
                    showHideSetList(true);
                } else {
                    showHideSetList(false);
                }
            }
        });

        rootView.findViewById(R.id.set_chooser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHideSetList(false);
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((FilterActivity) activity).addPanelSlideListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentSetPosition", currentSetPosition);
        outState.putParcelableArrayList("SET", sets);
    }

    private void showHideSetList(final boolean loadSet) {
        final int startHeight = setList.getHeight();
        final int targetHeight = (startHeight == 0) ? container.getHeight() : 0;
        final float startRotation = setArrow.getRotation();
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                if (targetHeight > startHeight) {
                    int newHeight = (int) (startHeight + (interpolatedTime * targetHeight));
                    setHeightView(setList, newHeight);
                    setHeightView(setListBg, newHeight);
                    setArrow.setRotation(startRotation + (180 * interpolatedTime));
                } else {
                    int newHeight = (int) (startHeight - startHeight * interpolatedTime);
                    setHeightView(setList, newHeight);
                    setHeightView(setListBg, newHeight);
                    setArrow.setRotation(startRotation - (180 * interpolatedTime));
                }
            }
        };
        animation.setDuration(200);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (loadSet) {
                    /*if (!getApp().isPremium() && position > 2){
                        showGoToPremium();
                        return false;
                    }*/
                    TrackingHelper.getInstance(getActivity().getApplicationContext()).trackEvent(TrackingHelper.UA_CATEGORY_SET, TrackingHelper.UA_ACTION_SELECT, sets.get(currentSetPosition).getCode());
                    SharedPreferences.Editor editor = getSharedPreferences().edit();
                    editor.putInt("setPosition", currentSetPosition);
                    editor.apply();
                    setAdapter.setCurrent(currentSetPosition);
                    setAdapter.notifyDataSetChanged();
                    loadSet();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        getView().startAnimation(animation);
    }

    private void loadSet() {
        ((FilterActivity) getActivity()).collapseSlidingPanel();
        chooserName.setText(sets.get(currentSetPosition).getName());
        loadSet(sets.get(currentSetPosition));
    }

    private void setHeightView(View view, int value) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.height = value;
        view.setLayoutParams(params);
    }

    @Override
    public String getPageTrack() {
        return null;
    }

    public void onEventMainThread(SetEvent event) {
        if (event.isError()) {
            Toast.makeText(getActivity(), event.getErrorMessage(), Toast.LENGTH_SHORT).show();
            TrackingHelper.getInstance(getActivity().getApplicationContext()).trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "set-main", event.getErrorMessage());
        } else {
            currentSetPosition = getSharedPreferences().getInt("setPosition", 0);
            setAdapter.setCurrent(currentSetPosition);
            sets.clear();
            for (MTGSet set : event.getResult()) {
                sets.add(set);
            }
            setAdapter.notifyDataSetChanged();
            loadSet();
        }
        bus.removeStickyEvent(event);
    }


    @Override
    public void onPanelSlide(View panel, float slideOffset) {
    }

    @Override
    public void onPanelCollapsed(View panel) {
        updateSetFragment();
    }

    @Override
    public void onPanelExpanded(View panel) {
    }

    @Override
    public void onPanelAnchored(View panel) {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onSortSelected() {
        updateSetFragment();
    }
}
