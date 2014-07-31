package com.dbottillo.lifecounter;

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

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class LifeCounterFragment extends DBFragment implements DBAsyncTask.DBAsyncTaskListener, LifeCounterAdapter.OnLifeCounterListener {

    public static LifeCounterFragment newInstance() {
        return new LifeCounterFragment();
    }

    private ArrayList<Player> players;
    private LifeCounterAdapter lifeCounterAdapter;
    private SmoothProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_life_counter, container, false);

        progressBar = (SmoothProgressBar) rootView.findViewById(R.id.progress);

        if (savedInstanceState == null) {
            players = new ArrayList<Player>();
            loadPlayers();
        } else {
            progressBar.setVisibility(View.GONE);
        }

        lifeCounterAdapter = new LifeCounterAdapter(getActivity(), players, this);
        ((ListView)rootView.findViewById(R.id.life_counter_list)).setAdapter(lifeCounterAdapter);

        setHasOptionsMenu(true);

        return rootView;
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
        progressBar.setVisibility(View.VISIBLE);
        new DBAsyncTask(getActivity(), this, DBAsyncTask.TASK_PLAYER).execute();
    }

    private void addPlayer(){
        Player player = new Player(0, getString(R.string.player, players.size()));
        DB40Helper db40Helper = DB40Helper.getInstance(getActivity());
        db40Helper.openDb();
        db40Helper.storePlayer(player);
        db40Helper.closeDb();
        loadPlayers();
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
                players.add((Player) player);
            }
        }
        lifeCounterAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTaskEndWithError(String error) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemovePlayer(int position) {
        DB40Helper db40Helper = DB40Helper.getInstance(getActivity());
        db40Helper.openDb();
        db40Helper.removePlayer(players.get(position));
        db40Helper.closeDb();
        players.remove(position);
        lifeCounterAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLifeCountChange(int position, int value) {
        players.get(position).changeLife(value);
        DB40Helper db40Helper = DB40Helper.getInstance(getActivity());
        db40Helper.openDb();
        db40Helper.storePlayer(players.get(position));
        db40Helper.closeDb();
        lifeCounterAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPoisonCountChange(int position, int value) {
        players.get(position).changePoisonCount(value);
        DB40Helper db40Helper = DB40Helper.getInstance(getActivity());
        db40Helper.openDb();
        db40Helper.storePlayer(players.get(position));
        db40Helper.closeDb();
        lifeCounterAdapter.notifyDataSetChanged();
    }
}
