package com.dbottillo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.dbottillo.R;
import com.dbottillo.base.DBFragment;
import com.dbottillo.database.DeckDataSource;
import com.dbottillo.resources.Deck;
import com.dbottillo.resources.MTGCard;

import java.util.ArrayList;

public class AddToDeckFragment extends DBFragment implements View.OnClickListener {

    Spinner chooseDeck;
    Spinner chooseQuantity;
    CheckBox sideboard;

    String[] decksChoose;
    String[] quantityChoose;

    ArrayList<Deck> decks;
    MTGCard card;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_to_deck, container, false);

        card = getArguments().getParcelable("card");

        chooseDeck = (Spinner) v.findViewById(R.id.choose_deck);
        chooseQuantity = (Spinner) v.findViewById(R.id.choose_quantity);
        sideboard = (CheckBox) v.findViewById(R.id.add_to_deck_sideboard);

        v.findViewById(R.id.add_to_deck_save).setOnClickListener(this);

        setupQuantitySpinner();

        if (savedInstanceState == null) {
            new LoadDecks(getActivity().getApplicationContext()).execute();
        } else {
            decks = savedInstanceState.getParcelableArrayList("decks");
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("decks", decks);
    }

    private void setupQuantitySpinner() {
        quantityChoose = new String[]{"Choose quantity", "1", "2", "3", "4"};
        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), R.layout.add_to_deck_spinner_item, quantityChoose);
        adapter.setDropDownViewResource(R.layout.add_to_deck_dropdown_item);
        chooseQuantity.setAdapter(adapter);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public String getPageTrack() {
        return "/add_to_deck";
    }

    public static DialogFragment newInstance(MTGCard card) {
        AddToDeckFragment instance = new AddToDeckFragment();
        Bundle args = new Bundle();
        args.putParcelable("card", card);
        instance.setArguments(args);
        return instance;
    }

    private void setupDecksSpinner(ArrayList<Deck> decks) {
        this.decks = decks;
        decksChoose = new String[decks.size() + 1];
        decksChoose[0] = "Choose deck";
        int i = 1;
        for (Deck deck : decks) {
            decksChoose[i] = deck.getName();
            i++;
        }
        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), R.layout.add_to_deck_spinner_item, decksChoose);
        adapter.setDropDownViewResource(R.layout.add_to_deck_dropdown_item);
        chooseDeck.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (chooseQuantity.getSelectedItemPosition() > 0 && chooseDeck.getSelectedItemPosition() > 0) {
            int quantity = Integer.parseInt(quantityChoose[chooseQuantity.getSelectedItemPosition()]);
            Deck deck = decks.get(chooseDeck.getSelectedItemPosition() - 1);
            boolean side = sideboard.isChecked();
            saveCard(quantity, deck, side);
            dismiss();
        }
    }

    private void saveCard(final int quantity, final Deck deck, final boolean side) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                DeckDataSource deckDataSource = new DeckDataSource(getActivity().getApplicationContext());
                deckDataSource.open();
                deckDataSource.addCardToDeck(deck, card, quantity, side);
            }
        };
        thread.start();
    }

    private class LoadDecks extends AsyncTask<Void, Void, ArrayList<Deck>> {

        private Context context;

        public LoadDecks(Context context) {
            this.context = context;
        }

        @Override
        protected ArrayList<Deck> doInBackground(Void... params) {
            DeckDataSource deckDataSource = new DeckDataSource(context);
            deckDataSource.open();
            return deckDataSource.getDecks();
        }

        @Override
        protected void onPostExecute(ArrayList<Deck> decks) {
            super.onPostExecute(decks);
            setupDecksSpinner(decks);
        }
    }

}
