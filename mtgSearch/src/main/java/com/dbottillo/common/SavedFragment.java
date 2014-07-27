package com.dbottillo.common;

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
import com.dbottillo.adapters.MTGCardListAdapter;
import com.dbottillo.base.DBFragment;
import com.dbottillo.base.MTGApp;
import com.dbottillo.helper.DBAsyncTask;
import com.dbottillo.resources.MTGCard;

import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class SavedFragment extends DBFragment implements AdapterView.OnItemClickListener, DBAsyncTask.DBAsyncTaskListener {

    private ArrayList<MTGCard> savedCards;
    private ListView listView;
    private MTGCardListAdapter adapter;
    private SmoothProgressBar progressBar;

    public static SavedFragment newInstance() {
        return new SavedFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        listView = (ListView) rootView.findViewById(R.id.set_list);

        progressBar = (SmoothProgressBar) rootView.findViewById(R.id.progress);

        savedCards = new ArrayList<MTGCard>();

        adapter = new MTGCardListAdapter(getActivity(), savedCards, false);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCards();
    }

    private void loadCards() {
        Log.e("card", "load cards called");
        progressBar.setVisibility(View.VISIBLE);
        new DBAsyncTask(getActivity(), this, DBAsyncTask.TASK_SAVED).execute();
    }

    @Override
    public String getPageTrack() {
        return "/saved";
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("savedCards", savedCards);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        trackEvent(MTGApp.UA_CATEGORY_UI, MTGApp.UA_ACTION_CLICK, "saved_card_at_pos:"+position);
        Intent cardsView = new Intent(getActivity(), CardsActivity.class);
        cardsView.putParcelableArrayListExtra(MTGCardsFragment.CARDS, savedCards);
        cardsView.putExtra(MTGCardsFragment.POSITION, position);
        cardsView.putExtra(MTGCardsFragment.SET_NAME, getString(R.string.action_saved));
        startActivity(cardsView);
    }

    @Override
    public void onTaskFinished(ArrayList<?> objects) {
        savedCards.clear();
        for (Object card : objects){
            savedCards.add((MTGCard) card);
        }
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onTaskEndWithError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i1 = item.getItemId();
        if (i1 == R.id.action_refresh) {
            loadCards();
            return true;
        }

        return false;
    }
}
