package com.dbottillo.saved;

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
import com.dbottillo.adapters.OnCardListener;
import com.dbottillo.base.DBFragment;
import com.dbottillo.base.MTGApp;
import com.dbottillo.cards.CardsActivity;
import com.dbottillo.cards.MTGCardsFragment;
import com.dbottillo.dialog.AddToDeckFragment;
import com.dbottillo.helper.DBAsyncTask;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.resources.MTGCard;

import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class SavedFragment extends DBFragment implements AdapterView.OnItemClickListener, DBAsyncTask.DBAsyncTaskListener, OnCardListener {

    private ArrayList<MTGCard> savedCards;
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

        setActionBarTitle(getString(R.string.action_saved));

        listView = (ListView) rootView.findViewById(R.id.card_list);
        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        emptyView.setText(R.string.empty_saved);

        progressBar = (SmoothProgressBar) rootView.findViewById(R.id.progress);

        savedCards = new ArrayList<>();

        adapter = new CardListAdapter(getActivity(), savedCards, false, R.menu.card_saved_option, this);
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
        TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_CARD, TrackingHelper.UA_ACTION_OPEN, "saved pos:" + position);
        Intent cardsView = new Intent(getActivity(), CardsActivity.class);
        MTGApp.cardsToDisplay = savedCards;
        cardsView.putExtra(MTGCardsFragment.POSITION, position);
        cardsView.putExtra(MTGCardsFragment.TITLE, getString(R.string.action_saved));
        startActivity(cardsView);
    }

    @Override
    public void onTaskFinished(int type, ArrayList<?> objects) {
        savedCards.clear();
        for (Object card : objects) {
            savedCards.add((MTGCard) card);
        }
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);

        emptyView.setVisibility(objects.size() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onTaskEndWithError(int type, String error) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "saved-main", error);
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

    @Override
    public void onCardSelected(MTGCard card, int position) {
        Intent cardsView = new Intent(getActivity(), CardsActivity.class);
        MTGApp.cardsToDisplay = savedCards;
        cardsView.putExtra(MTGCardsFragment.POSITION, position);
        cardsView.putExtra(MTGCardsFragment.TITLE, getString(R.string.action_saved));
        startActivity(cardsView);
    }

    @Override
    public void onOptionSelected(MenuItem menuItem, MTGCard card, int position) {
        if (menuItem.getItemId() == R.id.action_add_to_deck) {
            getDBActivity().openDialog("add_to_deck", AddToDeckFragment.newInstance(card));

        } else if (menuItem.getItemId() == R.id.action_remove){
            new DBAsyncTask(getActivity(), SavedFragment.this, DBAsyncTask.TASK_REMOVE_CARD).execute(card);
        }
    }
}
