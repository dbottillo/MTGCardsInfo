package com.dbottillo.lifecounter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dbottillo.R;
import com.dbottillo.adapters.LifeCounterAdapter;
import com.dbottillo.base.DBFragment;
import com.dbottillo.base.MTGApp;
import com.dbottillo.database.DB40Helper;
import com.dbottillo.helper.DBAsyncTask;
import com.dbottillo.resources.Player;
import com.dbottillo.view.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Random;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class LifeCounterFragment extends DBFragment implements DBAsyncTask.DBAsyncTaskListener, LifeCounterAdapter.OnLifeCounterListener {

    public static LifeCounterFragment newInstance() {
        return new LifeCounterFragment();
    }

    private ScrollView diceScrollView;
    private LinearLayout diceContainer;
    private ListView lifeListView;
    private ArrayList<Player> players;
    private LifeCounterAdapter lifeCounterAdapter;
    private SmoothProgressBar progressBar;

    private DB40Helper db40Helper;

    private boolean scrollDownAfterLoad = false;

    private boolean showPoison = false;
    private boolean diceShowed = false;

    String[] names = { "Teferi", "Nicol Bolas", "Gerrard", "Ajani", "Jace", "Liliana", "Elspeth", "Tezzeret", "Garruck",
            "Chandra", "Venser", "Doran", "Sorin" };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_life_counter, container, false);

        diceScrollView = (ScrollView) rootView.findViewById(R.id.life_counter_dice_scrolliew);
        diceContainer = (LinearLayout) rootView.findViewById(R.id.life_counter_dice_container);
        progressBar = (SmoothProgressBar) rootView.findViewById(R.id.progress);
        lifeListView = (ListView) rootView.findViewById(R.id.life_counter_list);
        showPoison = getSharedPreferences().getBoolean("poison", false);

        /*if (savedInstanceState == null) {
            players = new ArrayList<Player>();
            loadPlayers();
        } else {
            progressBar.setVisibility(View.GONE);
        }*/
        players = new ArrayList<Player>();

        lifeCounterAdapter = new LifeCounterAdapter(getActivity(), players, this, showPoison);
        lifeListView.setAdapter(lifeCounterAdapter);

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

    private void resetLifeCounter() {
        for (Player player : players){
            player.setLife(20);
            player.setPoisonCount(10);
            db40Helper.storePlayer(player);
        }
        loadPlayers();
    }


    @Override
    public String getPageTrack() {
        return "/life_counter";
    }

    private void loadPlayers(){
        new DBAsyncTask(getActivity(), this, DBAsyncTask.TASK_PLAYER).execute();
    }

    private void addPlayer(){
        if (players.size() == 10){
            Toast.makeText(getActivity(), R.string.maximum_player, Toast.LENGTH_SHORT).show();
            return;
        }
        Player player = new Player(getUniqueIdForPlayer(), getUniqueNameForPlayer());
        db40Helper.storePlayer(player);
        scrollDownAfterLoad = true;
        loadPlayers();
    }

    private String getUniqueNameForPlayer(){
        boolean unique = false;
        int pickedNumber = 0;
        while (!unique) {
            Random rand = new Random();
            pickedNumber = rand.nextInt(names.length);
            boolean founded = false;
            for (Player player : players) {
                if (player.getName().toLowerCase().contains(names[pickedNumber].toLowerCase())) {
                    founded = true;
                    break;
                }
            }
            if (!founded) unique = true;
        }
        return names[pickedNumber];
    }

    private int getUniqueIdForPlayer(){
        int id = 0;
        for (Player player : players){
            if (id == player.getId()){
                id++;
            }
        }
        return id;
    }

    @Override
    public void onTaskFinished(ArrayList<?> objects) {
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
        if (scrollDownAfterLoad){
            ((ListView) getView().findViewById(R.id.life_counter_list)).setSelection(players.size()-1);
        }
        scrollDownAfterLoad = false;
    }

    @Override
    public void onTaskEndWithError(String error) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemovePlayer(int position) {
        trackEvent(MTGApp.UA_CATEGORY_UI, MTGApp.UA_ACTION_LIFE_COUNTER, "removePlayer");
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
                trackEvent(MTGApp.UA_CATEGORY_UI, MTGApp.UA_ACTION_LIFE_COUNTER, "editPlayer");
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
        trackEvent(MTGApp.UA_CATEGORY_UI, MTGApp.UA_ACTION_LIFE_COUNTER, "lifeCountChanged");
        players.get(position).changeLife(value);
        db40Helper.storePlayer(players.get(position));
        loadPlayers();
    }

    @Override
    public void onPoisonCountChange(int position, int value) {
        trackEvent(MTGApp.UA_CATEGORY_UI, MTGApp.UA_ACTION_LIFE_COUNTER, "poisonCountChange");
        players.get(position).changePoisonCount(value);
        db40Helper.storePlayer(players.get(position));
        loadPlayers();
    }

    private View.OnClickListener tapOnDice = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            hideDice();
        }
    };

    private void launchDice(){
        diceScrollView.setVisibility(View.VISIBLE);
        diceContainer.removeAllViews();
        int heightRow = lifeListView.getChildAt(0).getHeight();
        ArrayList<TextView> playerResults = new ArrayList<TextView>(players.size());
        int[] results = new int[players.size()];
        for (int i=0; i<players.size(); i++){
            Player player = players.get(i);
            Random rand = new Random();
            results[i] = rand.nextInt(20) + 1;
            View dice = LayoutInflater.from(getActivity()).inflate(R.layout.life_counter_dice, null);
            dice.setLayoutParams(new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, heightRow));
            TextView playerName = (TextView) dice.findViewById(R.id.player_name);
            playerName.setText(player.getName());
            playerResults.add((TextView) dice.findViewById(R.id.player_result));
            diceContainer.addView(dice);
            dice.setOnClickListener(tapOnDice);
        }
        Button closeBtn = new Button(getActivity(), null, R.style.BtnGeneric);
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
        for (int i=0; i<players.size(); i++){
            playerResults.get(i).setText(results[i]+"");
        }
        diceShowed = true;
    }

    private void hideDice(){
        diceContainer.removeAllViews();
        diceScrollView.setVisibility(View.GONE);
        diceShowed = false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.life_counter, menu);

        MenuItem poison = menu.findItem(R.id.action_poison);
        if (showPoison){
            poison.setChecked(true);
        } else {
            poison.setChecked(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i1 = item.getItemId();
        if (i1 == R.id.action_add) {
            addPlayer();
            trackEvent(MTGApp.UA_CATEGORY_UI, MTGApp.UA_ACTION_LIFE_COUNTER, "addPlayer");
            return true;
        }
        if (i1 == R.id.action_reset) {
            resetLifeCounter();
            trackEvent(MTGApp.UA_CATEGORY_UI, MTGApp.UA_ACTION_LIFE_COUNTER, "resetLifeCounter");
            return true;
        }
        if (i1 == R.id.action_dice) {
            launchDice();
            trackEvent(MTGApp.UA_CATEGORY_UI, MTGApp.UA_ACTION_LIFE_COUNTER, "launchDice");
            return true;
        }
        if (i1 == R.id.action_poison) {
            trackEvent(MTGApp.UA_CATEGORY_UI, MTGApp.UA_ACTION_LIFE_COUNTER, "poisonSetting");
            getSharedPreferences().edit().putBoolean("poison", !showPoison).apply();
            showPoison = !showPoison;
            getActivity().invalidateOptionsMenu();
            lifeCounterAdapter.setShowPoison(showPoison);
            lifeCounterAdapter.notifyDataSetChanged();
            return true;
        }

        return false;
    }


    public boolean onBackPressed() {
        if (diceShowed){
            hideDice();
            return true;
        }
        return false;
    }
}
