package com.dbottillo.lifecounter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.dbottillo.R;
import com.dbottillo.adapters.LifeCounterAdapter;
import com.dbottillo.adapters.MTGCardListAdapter;
import com.dbottillo.base.DBFragment;
import com.dbottillo.base.MTGApp;
import com.dbottillo.common.CardsActivity;
import com.dbottillo.common.MTGCardsFragment;
import com.dbottillo.database.DB40Helper;
import com.dbottillo.helper.DBAsyncTask;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.resources.Player;

import java.util.ArrayList;
import java.util.Random;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class LifeCounterFragment extends DBFragment implements DBAsyncTask.DBAsyncTaskListener, LifeCounterAdapter.OnLifeCounterListener {

    public static LifeCounterFragment newInstance() {
        return new LifeCounterFragment();
    }

    private ArrayList<Player> players;
    private LifeCounterAdapter lifeCounterAdapter;
    private SmoothProgressBar progressBar;

    private DB40Helper db40Helper;

    private boolean scrollDownAfterLoad = false;

    String[] names = { "Teferi", "Nicol Bolas", "Gerrard", "Ajani", "Jace", "Liliana", "Elspeth", "Tezzeret", "Garruck",
            "Chandra", "Venser", "Doran", "Sorin" };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_life_counter, container, false);

        progressBar = (SmoothProgressBar) rootView.findViewById(R.id.progress);

        /*if (savedInstanceState == null) {
            players = new ArrayList<Player>();
            loadPlayers();
        } else {
            progressBar.setVisibility(View.GONE);
        }*/
        players = new ArrayList<Player>();

        lifeCounterAdapter = new LifeCounterAdapter(getActivity(), players, this);
        ((ListView)rootView.findViewById(R.id.life_counter_list)).setAdapter(lifeCounterAdapter);

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
        db40Helper = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.life_counter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i1 = item.getItemId();
        if (i1 == R.id.action_add) {
            addPlayer();
            return true;
        }

        return false;
    }

    @Override
    public String getPageTrack() {
        return null;
    }

    private void loadPlayers(){
        new DBAsyncTask(getActivity(), this, DBAsyncTask.TASK_PLAYER).execute();
    }

    private void addPlayer(){
        if (players.size() == 10){
            Toast.makeText(getActivity(), R.string.maximum_player, Toast.LENGTH_SHORT).show();
            return;
        }
        Player player = new Player(getUniqueIdForPlayer(), getString(R.string.player, getUniqueNameForPlayer()));
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
                Log.e("magic", "loaded: "+((Player)player).getId()+" - "+((Player)player).toString());
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
        db40Helper.removePlayer(players.get(position));
        loadPlayers();
    }

    @Override
    public void onLifeCountChange(int position, int value) {
        players.get(position).changeLife(value);
        db40Helper.storePlayer(players.get(position));
        loadPlayers();
    }

    @Override
    public void onPoisonCountChange(int position, int value) {
        players.get(position).changePoisonCount(value);
        db40Helper.storePlayer(players.get(position));
        loadPlayers();
    }
}
