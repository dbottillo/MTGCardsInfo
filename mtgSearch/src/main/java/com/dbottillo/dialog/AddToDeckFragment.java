package com.dbottillo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.dbottillo.R;
import com.dbottillo.base.DBFragment;
import com.dbottillo.database.DeckDataSource;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.resources.Deck;
import com.dbottillo.resources.MTGCard;

import java.util.ArrayList;

public class AddToDeckFragment extends DBFragment implements View.OnClickListener {

    Spinner chooseDeck;
    Spinner chooseQuantity;
    CheckBox sideboard;

    String[] decksChoose;
    String[] quantityChoose;

    TextInputLayout cardNameInputLayout;
    EditText deckName;
    TextInputLayout cardQuantityInputLayout;
    EditText cardQuantity;

    ArrayList<Deck> decks;
    MTGCard card;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_to_deck, container, false);

        card = getArguments().getParcelable("card");

        chooseDeck = (Spinner) v.findViewById(R.id.choose_deck);
        chooseQuantity = (Spinner) v.findViewById(R.id.choose_quantity);
        sideboard = (CheckBox) v.findViewById(R.id.add_to_deck_sideboard);

        cardNameInputLayout = (TextInputLayout) v.findViewById(R.id.new_deck_name_input_layout);
        deckName = (EditText) v.findViewById(R.id.new_deck_name);
        cardQuantityInputLayout = (TextInputLayout) v.findViewById(R.id.new_deck_quantity_input_layout);
        cardQuantity = (EditText) v.findViewById(R.id.new_deck_quantity);
        cardQuantity.setFilters(new InputFilter[]{new InputFilterMinMax(1, 30)});

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
        quantityChoose = new String[]{getString(R.string.deck_choose_quantity), "1", "2", "3", "4", getString(R.string.deck_specify)};
        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), R.layout.add_to_deck_spinner_item, quantityChoose);
        adapter.setDropDownViewResource(R.layout.add_to_deck_dropdown_item);
        chooseQuantity.setAdapter(adapter);
        chooseQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 5) {
                    chooseQuantity.setVisibility(View.GONE);
                    cardQuantityInputLayout.setVisibility(View.VISIBLE);
                    cardQuantity.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

    private void setupDecksSpinner(final ArrayList<Deck> decks) {
        this.decks = decks;
        decksChoose = new String[decks.size() + 2];
        decksChoose[0] = getString(R.string.deck_choose);
        int i = 1;
        for (Deck deck : decks) {
            decksChoose[i] = deck.getName();
            i++;
        }
        decksChoose[decks.size() + 1] = getString(R.string.deck_new);
        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), R.layout.add_to_deck_spinner_item, decksChoose);
        adapter.setDropDownViewResource(R.layout.add_to_deck_dropdown_item);
        chooseDeck.setAdapter(adapter);
        chooseDeck.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == decks.size() + 1) {
                    chooseDeck.setVisibility(View.GONE);
                    cardNameInputLayout.setVisibility(View.VISIBLE);
                    deckName.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int quantity = -1;
        if (chooseQuantity.getVisibility() == View.VISIBLE && chooseQuantity.getSelectedItemPosition() > 0) {
            quantity = Integer.parseInt(quantityChoose[chooseQuantity.getSelectedItemPosition()]);
        }
        if (chooseQuantity.getVisibility() == View.GONE && cardQuantity.getText().length() > 0) {
            quantity = Integer.parseInt(cardQuantity.getText().toString());
        }
        if (quantity > -1) {
            if (chooseDeck.getVisibility() == View.VISIBLE && chooseDeck.getSelectedItemPosition() > 0) {
                Deck deck = decks.get(chooseDeck.getSelectedItemPosition() - 1);
                boolean side = sideboard.isChecked();
                saveCard(getActivity().getApplicationContext(), quantity, deck, side);
                dismiss();
            }
            if (chooseDeck.getVisibility() == View.GONE && deckName.getText().length() > 0) {
                boolean side = sideboard.isChecked();
                saveCard(getActivity().getApplicationContext(), quantity, deckName.getText().toString(), side);
                dismiss();
            }
        }
    }

    private void saveCard(final Context context, final int quantity, final Deck deck, final boolean side) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    DeckDataSource deckDataSource = new DeckDataSource(context);
                    deckDataSource.open();
                    deckDataSource.addCardToDeck(deck.getId(), card, quantity, side);
                }
            }
        };
        thread.start();
        TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_DECK, TrackingHelper.UA_ACTION_ADD_CARD, quantity + " - existing");
    }

    private void saveCard(final Context context, final int quantity, final String deck, final boolean side) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                DeckDataSource deckDataSource = new DeckDataSource(context);
                deckDataSource.open();
                deckDataSource.addCardToDeck(deck, card, quantity, side);
            }
        };
        thread.start();
        TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_DECK, TrackingHelper.UA_ACTION_SAVE, deck);
        TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_DECK, TrackingHelper.UA_ACTION_ADD_CARD, quantity + " - new");
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

    public static class InputFilterMinMax implements InputFilter {

        private int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input)) {
                    return null;
                }
            } catch (NumberFormatException ignored) {
                return "";
            }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }

}
