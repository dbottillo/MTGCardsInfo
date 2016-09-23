package com.dbottillo.mtgsearchfree.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.exceptions.MTGException;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.DeckBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.presenter.CardsPresenter;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.TrackingManager;
import com.dbottillo.mtgsearchfree.view.CardsView;
import com.dbottillo.mtgsearchfree.view.activities.CardsActivity;
import com.dbottillo.mtgsearchfree.view.activities.MainActivity;
import com.dbottillo.mtgsearchfree.view.adapters.CardsAdapter;
import com.dbottillo.mtgsearchfree.view.adapters.OnCardListener;
import com.dbottillo.mtgsearchfree.view.helpers.CardsHelper;
import com.dbottillo.mtgsearchfree.view.helpers.DialogHelper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class SavedFragment extends BasicFragment implements OnCardListener, MainActivity.MainActivityListener, CardsView {

    private CardsBucket savedBucket;

    @BindView(R.id.progress)
    SmoothProgressBar progressBar;
    @BindView((R.id.empty_view))
    TextView emptyView;
    @BindView(R.id.card_list)
    RecyclerView listView;

    MainActivity mainActivity;

    @Inject
    CardsPresenter cardsPresenter;

    @Inject
    CardsHelper cardsHelper;

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

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        listView.setLayoutManager(llm);

        setHasOptionsMenu(true);

        getMTGApp().getUiGraph().inject(this);
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
    public void onCardSelected(MTGCard card, int position, View view) {
        LOG.d();
        TrackingManager.trackOpenCard(position);
        startActivity(CardsActivity.newFavInstance(getContext(), position));
    }

    @Override
    public void onOptionSelected(MenuItem menuItem, MTGCard card, int position) {
        LOG.d();
        if (menuItem.getItemId() == R.id.action_add_to_deck) {
            DialogHelper.open(dbActivity, "add_to_deck", AddToDeckFragment.newInstance(card));

        } else if (menuItem.getItemId() == R.id.action_remove) {
            cardsPresenter.removeFromFavourite(card, false);
            cardsPresenter.loadFavourites();
        }
    }

    @Override
    public void updateContent() {
        LOG.d();
        CardsBucket bucket = cardsHelper.filterCards(mainActivity.getCurrentFilter(), null, savedBucket);
        CardsAdapter adapter = CardsAdapter.list(bucket, false, R.menu.card_saved_option);
        adapter.setOnCardListener(this);
        listView.setAdapter(adapter);
        emptyView.setVisibility(bucket.getCards().size() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void cardLoaded(CardsBucket bucket) {
        LOG.d();
        progressBar.setVisibility(View.GONE);
        savedBucket = bucket;
        updateContent();
    }

    @Override
    public void deckLoaded(DeckBucket bucket) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void favIdLoaded(int[] favourites) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cardTypePreferenceChanged(boolean grid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void showError(String message) {
        LOG.d();
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        TrackingManager.trackSearchError(message);
    }

    @Override
    public void showError(MTGException exception) {

    }

}
