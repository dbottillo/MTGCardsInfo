package com.dbottillo.decks;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.AppCompatEditText;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dbottillo.R;
import com.dbottillo.adapters.DeckListAdapter;
import com.dbottillo.base.DBFragment;
import com.dbottillo.database.DeckDataSource;
import com.dbottillo.resources.Deck;
import com.dbottillo.util.AnimationUtil;
import com.dbottillo.util.InputUtil;

import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class DecksFragment extends DBFragment implements View.OnClickListener, TextView.OnEditorActionListener, LoaderManager.LoaderCallbacks<ArrayList<Deck>>, AdapterView.OnItemClickListener {

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

    private boolean newDeckViewOpen = false;

    private int heightNewDeckContainer = -1;

    private Loader decksLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_decks, container, false);

        setActionBarTitle(getString(R.string.action_decks));

        newDeck = (FloatingActionButton) rootView.findViewById(R.id.add_new_deck);

        listView = (ListView) rootView.findViewById(R.id.deck_list);
        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        emptyView.setText(R.string.empty_saved);

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

        deckListAdapter = new DeckListAdapter(getActivity(), decks);
        View footerView = inflater.inflate(R.layout.fab_button_list_footer, null, false);
        listView.addFooterView(footerView);
        listView.setAdapter(deckListAdapter);
        listView.setOnItemClickListener(this);

        AnimationUtil.growView(newDeck);
        newDeck.setOnClickListener(this);

        if (savedInstanceState == null) {

        } else {
            newDeckViewOpen = savedInstanceState.getBoolean("newDeckViewOpen");
        }


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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("newDeckViewOpen", newDeckViewOpen);
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
            return true;
        }
        return false;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new DecksLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Deck>> loader, ArrayList<Deck> data) {
        decks.clear();
        for (Deck deck : data) {
            decks.add(deck);
        }
        deckListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), DeckActivity.class);
        intent.putExtra("deck", decks.get(position));
        startActivity(intent);
    }

    static class DecksLoader extends AsyncTaskLoader<ArrayList<Deck>> {

        public DecksLoader(Context context) {
            super(context);
        }

        public ArrayList<Deck> loadInBackground() {
            DeckDataSource deckDataSource = new DeckDataSource(getContext());
            deckDataSource.open();
            return deckDataSource.getDecks();
        }
    }

    private class NewDeckTask extends AsyncTask<String, Void, Void> {

        private Context context;

        public NewDeckTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... params) {
            DeckDataSource deckDataSource = new DeckDataSource(context);
            deckDataSource.open();
            deckDataSource.addDeck(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            decksLoader.forceLoad();
        }
    }
}
