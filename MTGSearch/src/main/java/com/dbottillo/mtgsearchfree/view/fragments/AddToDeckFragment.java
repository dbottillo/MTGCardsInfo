package com.dbottillo.mtgsearchfree.view.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.presenter.DecksPresenter;
import com.dbottillo.mtgsearchfree.util.TrackingManager;
import com.dbottillo.mtgsearchfree.view.DecksView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddToDeckFragment extends BasicFragment implements DecksView {

    @Bind(R.id.choose_deck)
    Spinner chooseDeck;
    @Bind(R.id.choose_quantity)
    Spinner chooseQuantity;
    @Bind(R.id.add_to_deck_sideboard)
    CheckBox sideboard;

    String[] decksChoose;
    String[] quantityChoose;

    @Bind(R.id.new_deck_name_input_layout)
    TextInputLayout cardNameInputLayout;
    @Bind(R.id.new_deck_name)
    EditText deckName;
    @Bind(R.id.new_deck_quantity_input_layout)
    TextInputLayout cardQuantityInputLayout;
    @Bind(R.id.new_deck_quantity)
    EditText cardQuantity;

    List<Deck> decks;
    MTGCard card;

    @Inject
    DecksPresenter decksPresenter;

    public static DialogFragment newInstance(MTGCard card) {
        AddToDeckFragment instance = new AddToDeckFragment();
        Bundle args = new Bundle();
        args.putParcelable("card", card);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_to_deck, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        card = getArguments().getParcelable("card");
        cardQuantity.setFilters(new InputFilter[]{new InputFilterMinMax(1, 30)});

        setupQuantitySpinner();

        MTGApp.dataGraph.inject(this);
        decksPresenter.init(this);
        decksPresenter.loadDecks();
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

    private void setupDecksSpinner(final List<Deck> decks) {
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

    @OnClick(R.id.add_to_deck_save)
    public void addToDeck(View view) {
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
                saveCard(quantity, deck, side);
                dismiss();
            }
            if (chooseDeck.getVisibility() == View.GONE && deckName.getText().length() > 0) {
                boolean side = sideboard.isChecked();
                saveCard(quantity, deckName.getText().toString(), side);
                dismiss();
            }
        }
    }

    private void saveCard(final int quantity, final Deck deck, final boolean side) {
        card.setSideboard(side);
        decksPresenter.addCardToDeck(deck, card, quantity);
        TrackingManager.trackAddCardToDeck(quantity + " - existing");
    }

    private void saveCard(final int quantity, final String deck, final boolean side) {
        card.setSideboard(side);
        decksPresenter.addCardToDeck(deck, card, quantity);
        TrackingManager.trackNewDeck(deck);
        TrackingManager.trackAddCardToDeck(quantity + " - existing");
    }

    @Override
    public void decksLoaded(List<Deck> decks) {
        setupDecksSpinner(decks);
    }

    @Override
    public void deckLoaded(List<MTGCard> cards) {
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
