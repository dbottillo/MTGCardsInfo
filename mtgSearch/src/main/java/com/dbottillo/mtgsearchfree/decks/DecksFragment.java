package com.dbottillo.mtgsearchfree.decks;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.ListView;
import android.widget.TextView;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.adapters.DeckListAdapter;
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment;
import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.database.DeckDataSource;
import com.dbottillo.mtgsearchfree.helper.TrackingHelper;
import com.dbottillo.mtgsearchfree.resources.Deck;
import com.dbottillo.mtgsearchfree.util.AnimationUtil;
import com.dbottillo.mtgsearchfree.util.InputUtil;

import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class DecksFragment extends BasicFragment implements View.OnClickListener, TextView.OnEditorActionListener, LoaderManager.LoaderCallbacks<ArrayList<Deck>>, DeckListAdapter.OnDeckListener {

    public static DecksFragment newInstance() {
        return new DecksFragment();
    }

    private ArrayList<Deck> decks;
    private DeckListAdapter deckListAdapter;
    private ListView listView;
    private SmoothProgressBar progressBar;
    private TextView emptyView;
    private FloatingActionButton newDeck;
    private View newDeckOverlay;
    private View newDeckContainer;
    private AppCompatEditText newDeckName;

    private int heightNewDeckContainer = -1;

    private boolean newDeckViewOpen = false;

    private Loader decksLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_decks, container, false);

        setActionBarTitle(getString(R.string.action_decks));

        newDeck = (FloatingActionButton) rootView.findViewById(R.id.add_new_deck);

        listView = (ListView) rootView.findViewById(R.id.deck_list);
        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        emptyView.setText(R.string.empty_decks);

        progressBar = (SmoothProgressBar) rootView.findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

        newDeckName = (AppCompatEditText) rootView.findViewById(R.id.new_deck_name);
        newDeckName.setImeOptions(EditorInfo.IME_ACTION_DONE);
        newDeckName.setOnEditorActionListener(this);

        newDeckOverlay = rootView.findViewById(R.id.new_deck_overlay);
        newDeckOverlay.setAlpha(0.0f);
        newDeckOverlay.setVisibility(View.GONE);
        newDeckOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeNewDeck();
            }
        });

        newDeckContainer = rootView.findViewById(R.id.new_deck_name_container);
        newDeckContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                heightNewDeckContainer = newDeckContainer.getHeight();
                newDeckContainer.setY(-heightNewDeckContainer);
                newDeckContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });

        decks = new ArrayList<>();

        deckListAdapter = new DeckListAdapter(getActivity(), decks, this);
        View footerView = inflater.inflate(R.layout.fab_button_list_footer, listView, false);
        listView.addFooterView(footerView);
        listView.setAdapter(deckListAdapter);

        AnimationUtil.growView(newDeck);
        newDeck.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        decksLoader = getLoaderManager().initLoader(101, null, this);
        decksLoader.forceLoad();
    }

    @Override
    public void onStop() {
        super.onStop();
        decksLoader.stopLoading();
    }

    @Override
    public String getPageTrack() {
        return "/decks";
    }

    @Override
    public void onClick(View v) {
        openNewDeck();
    }

    public boolean onBackPressed() {
        if (newDeckViewOpen) {
            closeNewDeck();
            return true;
        }
        return super.onBackPressed();
    }

    private void openNewDeck() {
        newDeckOverlay.setAlpha(0.0f);
        newDeckOverlay.setVisibility(View.VISIBLE);
        newDeckOverlay.animate().alpha(1.0f).setDuration(250).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                newDeckName.requestFocus();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
        newDeckViewOpen = true;
        AnimatorSet open = new AnimatorSet();
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(newDeck,
                PropertyValuesHolder.ofFloat("scaleX", 0.0f),
                PropertyValuesHolder.ofFloat("scaleY", 0.0f));
        ObjectAnimator moveDown = ObjectAnimator.ofFloat(newDeckContainer, "Y", 0);
        open.play(scaleDown).with(moveDown);
        open.setDuration(200).start();
    }

    private void closeNewDeck() {
        AnimatorSet open = new AnimatorSet();
        ObjectAnimator scaleUp = ObjectAnimator.ofPropertyValuesHolder(newDeck,
                PropertyValuesHolder.ofFloat("scaleX", 1.0f),
                PropertyValuesHolder.ofFloat("scaleY", 1.0f));
        ObjectAnimator moveUp = ObjectAnimator.ofFloat(newDeckContainer, "Y", -heightNewDeckContainer);
        open.play(scaleUp).with(moveUp);
        open.setDuration(200).start();
        newDeckOverlay.requestFocus();
        newDeckOverlay.animate().alpha(0.0f).setDuration(250).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                newDeckOverlay.setVisibility(View.GONE);
                newDeckName.setText("");
                InputUtil.hideKeyboard(getActivity(), newDeckName.getWindowToken());
                newDeckViewOpen = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            String text = newDeckName.getText().toString();
            closeNewDeck();
            new NewDeckTask(getActivity().getApplicationContext()).execute(text);
            TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_DECK, TrackingHelper.UA_ACTION_SAVE, text);
            return true;
        }
        return false;
    }

    @Override
    public Loader<ArrayList<Deck>> onCreateLoader(int id, Bundle args) {
        return new DecksLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Deck>> loader, ArrayList<Deck> data) {
        decks.clear();
        for (Deck deck : data) {
            decks.add(deck);
        }
        if (decks.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
        deckListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onDeckSelected(Deck deck) {
        Intent intent = new Intent(getActivity(), DeckActivity.class);
        intent.putExtra("deck", deck);
        startActivity(intent);
    }

    @Override
    public void onDeckDelete(final Deck deck) {
        if (deck.getNumberOfCards() > 0) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.deck_delete_title)
                    .setMessage(R.string.deck_delete_text)
                    .setPositiveButton(R.string.deck_delete_confirmation, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new DeleteDeckTask(getActivity().getApplicationContext()).execute(deck);
                            TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_DECK, TrackingHelper.UA_ACTION_DELETE, deck.getName());
                        }

                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();

        } else {
            new DeleteDeckTask(getActivity().getApplicationContext()).execute(deck);
            TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_DECK, TrackingHelper.UA_ACTION_DELETE, deck.getName());
        }
    }

    static class DecksLoader extends AsyncTaskLoader<ArrayList<Deck>> {

        private Context context;

        public DecksLoader(Context context) {
            super(context);
            this.context = context;
        }

        public ArrayList<Deck> loadInBackground() {
            CardsInfoDbHelper dbHelper = CardsInfoDbHelper.getInstance(context);
            return DeckDataSource.getDecks(dbHelper.getReadableDatabase());
        }
    }

    private class NewDeckTask extends AsyncTask<String, Void, Void> {

        private Context context;

        public NewDeckTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... params) {
            CardsInfoDbHelper dbHelper = CardsInfoDbHelper.getInstance(context);
            DeckDataSource.addDeck(dbHelper.getWritableDatabase(), params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            decksLoader.forceLoad();
        }
    }

    private class DeleteDeckTask extends AsyncTask<Deck, Void, Void> {

        private Context context;

        public DeleteDeckTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Deck... params) {
            CardsInfoDbHelper dbHelper = CardsInfoDbHelper.getInstance(context);
            DeckDataSource.deleteDeck(dbHelper.getWritableDatabase(), params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            decksLoader.forceLoad();
        }
    }
}
