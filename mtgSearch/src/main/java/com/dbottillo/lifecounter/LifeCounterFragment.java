package com.dbottillo.lifecounter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.dbottillo.R;
import com.dbottillo.adapters.LifeCounterAdapter;
import com.dbottillo.base.DBFragment;
import com.dbottillo.database.DB40Helper;
import com.dbottillo.helper.DBAsyncTask;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.resources.Player;
import com.dbottillo.util.AnimationUtil;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class LifeCounterFragment extends DBFragment implements DBAsyncTask.DBAsyncTaskListener, LifeCounterAdapter.OnLifeCounterListener, View.OnClickListener {

    public static LifeCounterFragment newInstance() {
        return new LifeCounterFragment();
    }

    private ScrollView diceScrollView;
    private LinearLayout diceContainer;
    private ListView lifeListView;
    private ArrayList<Player> players;
    private LifeCounterAdapter lifeCounterAdapter;
    private SmoothProgressBar progressBar;
    private FloatingActionButton newPlayerButton;

    private DB40Helper db40Helper;

    private boolean scrollDownAfterLoad = false;

    private boolean showPoison = false;
    private boolean diceShowed = false;
    private boolean twoHGEnabled = false;

    String[] names = {"Teferi", "Nicol Bolas", "Gerrard", "Ajani", "Jace", "Liliana", "Elspeth", "Tezzeret", "Garruck",
            "Chandra", "Venser", "Doran", "Sorin"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_life_counter, container, false);

        setActionBarTitle(getString(R.string.action_life_counter));

        diceScrollView = (ScrollView) rootView.findViewById(R.id.life_counter_dice_scrolliew);
        diceContainer = (LinearLayout) rootView.findViewById(R.id.life_counter_dice_container);
        progressBar = (SmoothProgressBar) rootView.findViewById(R.id.progress);
        lifeListView = (ListView) rootView.findViewById(R.id.life_counter_list);
        showPoison = getSharedPreferences().getBoolean("poison", false);

        View footerView = inflater.inflate(R.layout.fab_button_list_footer, lifeListView, false);
        lifeListView.addFooterView(footerView);

        twoHGEnabled = getSharedPreferences().getBoolean(PREF_TWO_HG_ENABLED, false);

        newPlayerButton = (FloatingActionButton) rootView.findViewById(R.id.new_player);
        newPlayerButton.setOnClickListener(this);

        players = new ArrayList<>();

        lifeCounterAdapter = new LifeCounterAdapter(getActivity(), players, this, showPoison);
        lifeListView.setAdapter(lifeCounterAdapter);

        AnimationUtil.growView(newPlayerButton);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        db40Helper = DB40Helper.getInstance(activity);
    }

    @Override
    public void onStart() {
        super.onStart();

        db40Helper.openDb();
        progressBar.setVisibility(View.VISIBLE);
        loadPlayers();
    }

    @Override
    public void onStop() {
        super.onStop();

        db40Helper.closeDb();
    }

    @Override
    public void onResume() {
        super.onResume();
        setScreenOn(getSharedPreferences().getBoolean(PREF_SCREEN_ON, false));
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
            db40Helper.storePlayer(player);
        }
        loadPlayers();
    }


    @Override
    public String getPageTrack() {
        return "/life_counter";
    }

    private void loadPlayers() {
        new DBAsyncTask(getActivity(), this, DBAsyncTask.TASK_PLAYER).execute();
    }

    private void addPlayer() {
        if (players.size() == 10) {
            Toast.makeText(getActivity(), R.string.maximum_player, Toast.LENGTH_SHORT).show();
            return;
        }
        Player player = new Player(getUniqueIdForPlayer(), getUniqueNameForPlayer());
        db40Helper.storePlayer(player);
        scrollDownAfterLoad = true;
        loadPlayers();
    }

    private String getUniqueNameForPlayer() {
        boolean unique = false;
        int pickedNumber = 0;
        while (!unique) {
            Random rand = new Random();
            pickedNumber = rand.nextInt(names.length);
            boolean founded = false;
            for (Player player : players) {
                if (player.getName().toLowerCase(Locale.getDefault()).contains(names[pickedNumber].toLowerCase(Locale.getDefault()))) {
                    founded = true;
                    break;
                }
            }
            if (!founded) unique = true;
        }
        return names[pickedNumber];
    }

    private int getUniqueIdForPlayer() {
        int id = 0;
        for (Player player : players) {
            if (id == player.getId()) {
                id++;
            }
        }
        return id;
    }

    @Override
    public void onTaskFinished(int type, ArrayList<?> objects) {
        progressBar.setVisibility(View.GONE);
        if (objects.size() == 0) {
            // need at least one player
            addPlayer();
        } else {
            players.clear();
            for (Object player : objects) {
                //Log.e("magic", "loaded: "+((Player)player).getId()+" - "+((Player)player).toString());
                players.add((Player) player);
            }
        }
        lifeCounterAdapter.notifyDataSetChanged();
        if (scrollDownAfterLoad) {
            ((ListView) getView().findViewById(R.id.life_counter_list)).setSelection(players.size() - 1);
        }
        scrollDownAfterLoad = false;
    }

    @Override
    public void onTaskEndWithError(int type, String error) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemovePlayer(int position) {
        TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_LIFE_COUNTER, "removePlayer");
        db40Helper.removePlayer(players.get(position));
        loadPlayers();
    }

    @Override
    public void onEditPlayer(final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle(getString(R.string.edit_player));

        final EditText input = new EditText(getActivity());
        input.setText(players.get(position).getName());
        alert.setView(input);

        alert.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                players.get(position).setName(value);
                db40Helper.storePlayer(players.get(position));
                loadPlayers();
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
        db40Helper.storePlayer(players.get(position));
        loadPlayers();
    }

    @Override
    public void onPoisonCountChange(int position, int value) {
        TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_LIFE_COUNTER, "poisonCountChange");
        players.get(position).changePoisonCount(value);
        db40Helper.storePlayer(players.get(position));
        loadPlayers();
    }

    private View.OnClickListener tapOnDice = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideDice();
        }
    };

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
        screenOn.setChecked(getSharedPreferences().getBoolean(PREF_SCREEN_ON, false));

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
            getSharedPreferences().edit().putBoolean("poison", !showPoison).apply();
            showPoison = !showPoison;
            getActivity().invalidateOptionsMenu();
            lifeCounterAdapter.setShowPoison(showPoison);
            lifeCounterAdapter.notifyDataSetChanged();
            return true;
        }
        if (i1 == R.id.action_screen_on) {
            boolean screenOn = getSharedPreferences().getBoolean(PREF_SCREEN_ON, false);
            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putBoolean(PREF_SCREEN_ON, !screenOn);
            editor.apply();
            getActivity().invalidateOptionsMenu();
            setScreenOn(!screenOn);
            TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_LIFE_COUNTER, "screenOn");
        }
        if (i1 == R.id.action_two_hg) {
            twoHGEnabled = !twoHGEnabled;
            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putBoolean(PREF_TWO_HG_ENABLED, twoHGEnabled);
            editor.apply();
            getActivity().invalidateOptionsMenu();
            resetLifeCounter();
            TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_LIFE_COUNTER, "two_hg");
        }

        return false;
    }

}
