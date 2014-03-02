package com.dbottillo.mtgsearch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.dbottillo.adapters.MTGCardListAdapter;
import com.dbottillo.helper.DBAsyncTask;
import com.dbottillo.helper.FilterHelper;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.resources.MTGSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class MTGSetFragment extends DBFragment implements DBAsyncTask.DBAsyncTaskListener, AdapterView.OnItemClickListener {

    private static final String SET_CHOSEN = "set_chosen";

    private MTGSet mtgSet;

    private ListView listView;
    private ArrayList<MTGCard> cards;
    private MTGCardListAdapter adapter;

    public static MTGSetFragment newInstance(MTGSet set) {
        MTGSetFragment fragment = new MTGSetFragment();
        Bundle args = new Bundle();
        args.putParcelable(SET_CHOSEN, set);
        fragment.setArguments(args);
        return fragment;
    }

    public MTGSetFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mtgSet = getArguments().getParcelable(SET_CHOSEN);

        listView = (ListView) rootView.findViewById(R.id.set_list);
        cards = new ArrayList<MTGCard>();
        adapter = new MTGCardListAdapter(getActivity(), cards);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        showRefresh();
        String packageName = getActivity().getApplication().getPackageName();
        new DBAsyncTask(getActivity(), this, DBAsyncTask.TASK_SINGLE_SET).setPackageName(packageName).execute(mtgSet.getCode());
    }

    @Override
    public void onTaskFinished(ArrayList<?> result) {
        mtgSet.getCards().clear();
        for (Object set : result){
            mtgSet.getCards().add((MTGCard) set);
        }
        result.clear();
        populateCardsWithFilter();
    }

    private void populateCardsWithFilter(){
        cards.clear();
        for (MTGCard card : mtgSet.getCards()){
            boolean toAdd = false;
            if (card.getColors().contains(MTGCard.WHITE) && getSharedPreferences().getBoolean(FilterHelper.FILTER_WHITE, true)) toAdd = true;
            if (card.getColors().contains(MTGCard.BLUE) && getSharedPreferences().getBoolean(FilterHelper.FILTER_BLUE, true)) toAdd = true;
            if (card.getColors().contains(MTGCard.BLACK) && getSharedPreferences().getBoolean(FilterHelper.FILTER_BLACK, true)) toAdd = true;
            if (card.getColors().contains(MTGCard.RED) && getSharedPreferences().getBoolean(FilterHelper.FILTER_RED, true)) toAdd = true;
            if (card.getColors().contains(MTGCard.GREEN) && getSharedPreferences().getBoolean(FilterHelper.FILTER_GREEN, true)) toAdd = true;

            if (card.isALand() && getSharedPreferences().getBoolean(FilterHelper.FILTER_ARTIFACT, true)) toAdd = true;
            if (card.isAnArtifact() && getSharedPreferences().getBoolean(FilterHelper.FILTER_LAND, true)) toAdd = true;

            if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_COMMON) &&
                    !getSharedPreferences().getBoolean(FilterHelper.FILTER_COMMON, true)) toAdd = false;
            if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_UNCOMMON) &&
                    !getSharedPreferences().getBoolean(FilterHelper.FILTER_UNCOMMON, true)) toAdd = false;
            if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_RARE) &&
                    !getSharedPreferences().getBoolean(FilterHelper.FILTER_RARE, true)) toAdd = false;
            if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_COMMON) &&
                    !getSharedPreferences().getBoolean(FilterHelper.FILTER_COMMON, true)) toAdd = false;

            if (toAdd) cards.add(card);
        }
        Collections.sort(cards, new Comparator<Object>() {
            public int compare(Object o1, Object o2) {
                MTGCard card = (MTGCard) o1;
                MTGCard card2 = (MTGCard) o2;
                return card.compareTo(card2);
            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onTaskEndWithError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent cardsView = new Intent(getActivity(), CardsActivity.class);
        cardsView.putParcelableArrayListExtra(MTGCardsFragment.CARDS, cards);
        cardsView.putExtra(MTGCardsFragment.POSITION, position);
        cardsView.putExtra(MTGCardsFragment.SET_NAME, mtgSet.getName());
        startActivity(cardsView);
    }

    public void refreshUI() {
        populateCardsWithFilter();
    }

}
