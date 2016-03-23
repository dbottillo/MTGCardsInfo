package com.dbottillo.mtgsearchfree.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.adapters.CardListAdapter;
import com.dbottillo.mtgsearchfree.adapters.OnCardListener;
import com.dbottillo.mtgsearchfree.communication.DataManager;
import com.dbottillo.mtgsearchfree.communication.events.SavedCardsEvent;
import com.dbottillo.mtgsearchfree.helper.TrackingHelper;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.presenter.CardsPresenter;
import com.dbottillo.mtgsearchfree.view.CardsView;
import com.dbottillo.mtgsearchfree.view.activities.CardsActivity;
import com.dbottillo.mtgsearchfree.view.activities.MainActivity;
import com.dbottillo.mtgsearchfree.view.helpers.CardsHelper;
import com.dbottillo.mtgsearchfree.view.helpers.DialogHelper;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class SavedFragment extends BasicFragment implements OnCardListener, MainActivity.MainActivityListener, CardsView {

    private ArrayList<MTGCard> savedCards;
    private ArrayList<MTGCard> savedFilteredCards;
    private CardListAdapter adapter;

    @Bind(R.id.progress)
    SmoothProgressBar progressBar;
    @Bind((R.id.empty_view))
    TextView emptyView;
    @Bind(R.id.card_list)
    ListView listView;

    MainActivity mainActivity;

    @Inject
    CardsPresenter cardsPresenter;

    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
        mainActivity.setMainActivityListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_set, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emptyView.setText(R.string.empty_saved);
        setActionBarTitle(getString(R.string.action_saved));

        savedCards = new ArrayList<>();
        savedFilteredCards = new ArrayList<>();

        adapter = new CardListAdapter(getActivity(), savedFilteredCards, false, R.menu.card_saved_option, this);
        listView.setAdapter(adapter);

        setHasOptionsMenu(true);

        MTGApp.dataGraph.inject(this);
        cardsPresenter.init(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        cardsPresenter.loadFavourites();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i1 = item.getItemId();
        if (i1 == R.id.action_refresh) {
            cardsPresenter.loadFavourites();
            return true;
        }
        return false;
    }

    @Override
    public void onCardSelected(MTGCard card, int position) {
        TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_CARD, TrackingHelper.UA_ACTION_OPEN, "saved pos:" + position);
        startActivity(CardsActivity.newFavInstance(getContext(), position));
    }

    @Override
    public void onOptionSelected(MenuItem menuItem, MTGCard card, int position) {
        if (menuItem.getItemId() == R.id.action_add_to_deck) {
            DialogHelper.open(dbActivity, "add_to_deck", AddToDeckFragment.newInstance(card));

        } else if (menuItem.getItemId() == R.id.action_remove) {
            cardsPresenter.saveAsFavourite(card);
        }
    }

    @Override
    public void updateContent() {
        savedFilteredCards.clear();
        CardsHelper.filterCards(mainActivity.getCurrentFilter(), null, savedCards, savedFilteredCards);
        adapter.notifyDataSetChanged();
        emptyView.setVisibility(savedFilteredCards.size() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void cardLoaded(CardsBucket bucket) {
        progressBar.setVisibility(View.GONE);
        savedCards.clear();
        savedCards.addAll(bucket.getCards());
        updateContent();
    }


    @Override
    public void favIdLoaded(int[] favourites) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "saved-main", message);
    }

}
