package com.dbottillo.saved;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dbottillo.R;
import com.dbottillo.adapters.CardListAdapter;
import com.dbottillo.base.DBFragment;
import com.dbottillo.base.MTGApp;
import com.dbottillo.common.CardsActivity;
import com.dbottillo.common.MTGCardsFragment;
import com.dbottillo.helper.DBAsyncTask;
import com.dbottillo.resources.GameCard;

import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class SavedFragment extends DBFragment implements AdapterView.OnItemClickListener, DBAsyncTask.DBAsyncTaskListener {

    private ArrayList<GameCard> savedCards;
    private ListView listView;
    private CardListAdapter adapter;
    private SmoothProgressBar progressBar;
    private TextView emptyView;


    public static SavedFragment newInstance() {
        return new SavedFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_set, container, false);

        listView = (ListView) rootView.findViewById(R.id.set_list);
        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        emptyView.setText(R.string.empty_saved);

        progressBar = (SmoothProgressBar) rootView.findViewById(R.id.progress);

        savedCards = new ArrayList<GameCard>();

        adapter = new CardListAdapter(getActivity(), savedCards, false);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCards();
    }

    private void loadCards() {
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
        trackEvent(MTGApp.UA_CATEGORY_UI, MTGApp.UA_ACTION_CLICK, "saved_card_at_pos:" + position);
        Intent cardsView = new Intent(getActivity(), CardsActivity.class);
        cardsView.putParcelableArrayListExtra(MTGCardsFragment.CARDS, savedCards);
        cardsView.putExtra(MTGCardsFragment.POSITION, position);
        cardsView.putExtra(MTGCardsFragment.SET_NAME, getString(R.string.action_saved));
        startActivity(cardsView);
    }

    @Override
    public void onTaskFinished(ArrayList<?> objects) {
        savedCards.clear();
        for (Object card : objects) {
            savedCards.add((GameCard) card);
        }
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);

        emptyView.setVisibility(objects.size() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onTaskEndWithError(String error) {
        progressBar.setVisibility(View.GONE);
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
