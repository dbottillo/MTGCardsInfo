package com.dbottillo.mtgsearchfree.saved;

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

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.adapters.CardListAdapter;
import com.dbottillo.mtgsearchfree.adapters.OnCardListener;
import com.dbottillo.mtgsearchfree.base.DBFragment;
import com.dbottillo.mtgsearchfree.base.MTGApp;
import com.dbottillo.mtgsearchfree.cards.CardsActivity;
import com.dbottillo.mtgsearchfree.cards.CardsHelper;
import com.dbottillo.mtgsearchfree.cards.MTGCardsFragment;
import com.dbottillo.mtgsearchfree.communication.DataManager;
import com.dbottillo.mtgsearchfree.communication.events.SavedCardsEvent;
import com.dbottillo.mtgsearchfree.dialog.AddToDeckFragment;
import com.dbottillo.mtgsearchfree.helper.DialogHelper;
import com.dbottillo.mtgsearchfree.helper.TrackingHelper;
import com.dbottillo.mtgsearchfree.persistence.MigrationPreferences;
import com.dbottillo.mtgsearchfree.resources.MTGCard;
import com.dbottillo.mtgsearchfree.view.SlidingUpPanelLayout;
import com.dbottillo.mtgsearchfree.view.activities.FilterActivity;

import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class SavedFragment extends DBFragment implements AdapterView.OnItemClickListener, OnCardListener, SlidingUpPanelLayout.PanelSlideListener {

    private ArrayList<MTGCard> savedCards;
    private ArrayList<MTGCard> savedFilteredCards;
    private CardListAdapter adapter;
    private SmoothProgressBar progressBar;
    private TextView emptyView;


    public static SavedFragment newInstance() {
        return new SavedFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((FilterActivity) activity).addPanelSlideListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_set, container, false);

        setActionBarTitle(getString(R.string.action_saved));

        ListView listView = (ListView) rootView.findViewById(R.id.card_list);
        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        emptyView.setText(R.string.empty_saved);

        progressBar = (SmoothProgressBar) rootView.findViewById(R.id.progress);

        savedCards = new ArrayList<>();
        savedFilteredCards = new ArrayList<>();

        adapter = new CardListAdapter(getActivity(), savedFilteredCards, false, R.menu.card_saved_option, this);
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
        MigrationPreferences migrationPreferences = new MigrationPreferences(getContext());
        if (migrationPreferences.migrationInProgress()) {
            emptyView.setText(getString(R.string.favourite_migration_in_progress));
            emptyView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            DataManager.execute(DataManager.TASK.SAVED_CARDS, true);
        }
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
        MTGApp.Companion.setCardsToDisplay(savedCards);
        cardsView.putExtra(MTGCardsFragment.POSITION, position);
        cardsView.putExtra(MTGCardsFragment.TITLE, getString(R.string.action_saved));
        startActivity(cardsView);
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
        MTGApp.Companion.setCardsToDisplay(savedFilteredCards);
        cardsView.putExtra(MTGCardsFragment.POSITION, position);
        cardsView.putExtra(MTGCardsFragment.TITLE, getString(R.string.action_saved));
        startActivity(cardsView);
    }

    @Override
    public void onOptionSelected(MenuItem menuItem, MTGCard card, int position) {
        if (menuItem.getItemId() == R.id.action_add_to_deck) {
            DialogHelper.Companion.open(getDBActivity(), "add_to_deck", AddToDeckFragment.newInstance(card));

        } else if (menuItem.getItemId() == R.id.action_remove) {
            DataManager.execute(DataManager.TASK.UN_SAVE_CARD, card);
        }
    }

    public void onEventMainThread(SavedCardsEvent event) {
        progressBar.setVisibility(View.GONE);
        if (event.isError()) {
            Toast.makeText(getActivity(), event.getErrorMessage(), Toast.LENGTH_SHORT).show();
            TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "saved-main", event.getErrorMessage());
        } else {
            savedCards = event.getResult();
            refreshUI();
        }
        bus.removeStickyEvent(event);
    }

    private void refreshUI() {
        savedFilteredCards.clear();
        CardsHelper.filterCards(getSharedPreferences(), null, savedCards, savedFilteredCards);
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        emptyView.setVisibility(savedFilteredCards.size() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    @Override
    public void onPanelCollapsed(View panel) {
        refreshUI();
    }

    @Override
    public void onPanelExpanded(View panel) {

    }

    @Override
    public void onPanelAnchored(View panel) {

    }
}
