package com.dbottillo.mtgsearchfree.view.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.adapters.LifeCounterAdapter;
import com.dbottillo.mtgsearchfree.helper.TrackingHelper;
import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.presenter.PlayerPresenter;
import com.dbottillo.mtgsearchfree.util.AnimationUtil;
import com.dbottillo.mtgsearchfree.view.PlayersView;

import java.util.ArrayList;
import java.util.Random;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


public class LifeCounterFragment extends BasicFragment implements LifeCounterAdapter.OnLifeCounterListener, View.OnClickListener, PlayersView {

    public static LifeCounterFragment newInstance() {
        return new LifeCounterFragment();
    }

    @Bind(R.id.life_counter_dice_scrolliew)
    ScrollView diceScrollView;
    @Bind(R.id.life_counter_dice_container)
    LinearLayout diceContainer;
    @Bind(R.id.progress)
    SmoothProgressBar progressBar;
    @Bind(R.id.life_counter_list)
    ListView lifeListView;
    @Bind(R.id.new_player)
    FloatingActionButton newPlayerButton;

    private ArrayList<Player> players;
    private LifeCounterAdapter lifeCounterAdapter;

    private boolean scrollDownAfterLoad = false;
    private boolean showPoison = false;
    private boolean diceShowed = false;
    private boolean twoHGEnabled = false;

    @Inject
    PlayerPresenter playerPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_life_counter, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {

        setActionBarTitle(getString(R.string.action_life_counter));

        showPoison = sharedPreferences.getBoolean("poison", false);

        View footerView = LayoutInflater.from(getContext()).inflate(R.layout.fab_button_list_footer, lifeListView, false);
        lifeListView.addFooterView(footerView);

        twoHGEnabled = sharedPreferences.getBoolean(BasicFragment.PREF_TWO_HG_ENABLED, false);

        newPlayerButton.setOnClickListener(this);

        players = new ArrayList<>();

        lifeCounterAdapter = new LifeCounterAdapter(getActivity(), players, this, showPoison);
        lifeListView.setAdapter(lifeCounterAdapter);

        AnimationUtil.growView(newPlayerButton);

        setHasOptionsMenu(true);

        MTGApp.dataGraph.inject(this);
        playerPresenter.init(this);

        playerPresenter.loadPlayers();
    }

    @Override
    public void onResume() {
        super.onResume();
        setScreenOn(sharedPreferences.getBoolean(BasicFragment.PREF_SCREEN_ON, false));
    }

    private void setScreenOn(boolean screenOn) {
        if (getView() != null) {
            getView().setKeepScreenOn(screenOn);
        }
    }

    private void resetLifeCounter() {
        for (Player player : players) {
            player.setLife(twoHGEnabled ? 30 : 20);
            player.setPoisonCount(twoHGEnabled ? 15 : 10);
        }
        playerPresenter.editPlayer(players);
    }


    @Override
    public String getPageTrack() {
        return "/life_counter";
    }


    private void addPlayer() {
        if (players.size() == 10) {
            Toast.makeText(getActivity(), R.string.maximum_player, Toast.LENGTH_SHORT).show();
            return;
        }
        playerPresenter.addPlayer();
        scrollDownAfterLoad = true;
    }

    @Override
    public void onRemovePlayer(int position) {
        TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_LIFE_COUNTER, "removePlayer");
        playerPresenter.removePlayer(players.get(position));
    }

    @Override
    public void onEditPlayer(final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.MTGDialogTheme);

        alert.setTitle(getString(R.string.edit_player));

        final EditText input = new EditText(getActivity());
        input.setText(players.get(position).getName());
        input.setSelection(players.get(position).getName().length());
        alert.setView(input);

        alert.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                players.get(position).setName(value);
                playerPresenter.editPlayer(players.get(position));
                TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_LIFE_COUNTER, "editPlayer");
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();

    }

    @Override
    public void onLifeCountChange(int position, int value) {
        TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_LIFE_COUNTER, "lifeCountChanged");
        players.get(position).changeLife(value);
        playerPresenter.editPlayer(players.get(position));
    }

    @Override
    public void onPoisonCountChange(int position, int value) {
        TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_LIFE_COUNTER, "poisonCountChange");
        players.get(position).changePoisonCount(value);
        playerPresenter.editPlayer(players.get(position));
    }

    private void launchDice() {
        if (diceShowed) {
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                player.setDiceResult(-1);
            }
        } else {
        /*diceScrollView.setVisibility(View.VISIBLE);
        diceContainer.removeAllViews();
        int heightRow = lifeListView.getChildAt(0).getHeight();
        ArrayList<TextView> playerResults = new ArrayList<TextView>(players.size());*/
            //int[] results = new int[players.size()];
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                Random rand = new Random();
                //results[i] = rand.nextInt(20) + 1;
                player.setDiceResult(rand.nextInt(20) + 1);
           /* View dice = LayoutInflater.from(getActivity()).inflate(R.layout.life_counter_dice, null);
            dice.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, heightRow));
            TextView playerName = (TextView) dice.findViewById(R.id.player_name);
            playerName.setText(player.getName());
            playerResults.add((TextView) dice.findViewById(R.id.player_result));
            diceContainer.addView(dice);
            dice.setOnClickListener(tapOnDice);*/
            }
        }
       /* Button closeBtn = new Button(getActivity(), null, R.style.BtnGeneric);
        closeBtn.setBackgroundResource(R.drawable.btn_common);
        closeBtn.setTextColor(getResources().getColor(android.R.color.white));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 20;
        params.gravity = Gravity.CENTER_HORIZONTAL;
        closeBtn.setLayoutParams(params);
        closeBtn.setText(getString(R.string.dice_close));
        closeBtn.setPadding(20, 20, 20, 20);
        closeBtn.setOnClickListener(tapOnDice);
        diceContainer.addView(closeBtn);
        for (int i = 0; i < players.size(); i++) {
            playerResults.get(i).setText(results[i] + "");
        }
        diceShowed = true;*/
        diceShowed = !diceShowed;
        lifeCounterAdapter.notifyDataSetChanged();
    }

    private void hideDice() {
        diceContainer.removeAllViews();
        diceScrollView.setVisibility(View.GONE);
        diceShowed = false;
    }

    @Override
    public void onClick(View v) {
        addPlayer();
        TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_LIFE_COUNTER, "addPlayer");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.life_counter, menu);

        MenuItem poison = menu.findItem(R.id.action_poison);
        if (showPoison) {
            poison.setChecked(true);
        } else {
            poison.setChecked(false);
        }

        MenuItem screenOn = menu.findItem(R.id.action_screen_on);
        screenOn.setChecked(sharedPreferences.getBoolean(BasicFragment.PREF_SCREEN_ON, false));

        MenuItem twoHg = menu.findItem(R.id.action_two_hg);
        twoHg.setChecked(twoHGEnabled);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i1 = item.getItemId();
        if (i1 == R.id.action_reset) {
            resetLifeCounter();
            TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_LIFE_COUNTER, "resetLifeCounter");
            return true;
        }
        if (i1 == R.id.action_dice) {
            launchDice();
            TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_LIFE_COUNTER, "launchDice");
            return true;
        }
        if (i1 == R.id.action_poison) {
            TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_LIFE_COUNTER, "poisonSetting");
            sharedPreferences.edit().putBoolean("poison", !showPoison).apply();
            showPoison = !showPoison;
            getActivity().invalidateOptionsMenu();
            lifeCounterAdapter.setShowPoison(showPoison);
            lifeCounterAdapter.notifyDataSetChanged();
            return true;
        }
        if (i1 == R.id.action_screen_on) {
            boolean screenOn = sharedPreferences.getBoolean(BasicFragment.PREF_SCREEN_ON, false);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(BasicFragment.PREF_SCREEN_ON, !screenOn);
            editor.apply();
            getActivity().invalidateOptionsMenu();
            setScreenOn(!screenOn);
            TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_LIFE_COUNTER, "screenOn");
        }
        if (i1 == R.id.action_two_hg) {
            twoHGEnabled = !twoHGEnabled;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(BasicFragment.PREF_TWO_HG_ENABLED, twoHGEnabled);
            editor.apply();
            getActivity().invalidateOptionsMenu();
            resetLifeCounter();
            TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_LIFE_COUNTER, "two_hg");
        }

        return false;
    }

    @Override
    public void playersLoaded(ArrayList<Player> players) {
        progressBar.setVisibility(View.GONE);
        if (players.size() == 0) {
            // need at least one player
            addPlayer();
        } else {
            this.players.clear();
            this.players.addAll(players);
        }
        lifeCounterAdapter.notifyDataSetChanged();
        if (scrollDownAfterLoad && getView() != null) {
            ((ListView) getView().findViewById(R.id.life_counter_list)).setSelection(players.size() - 1);
        }
        scrollDownAfterLoad = false;
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String message) {
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
