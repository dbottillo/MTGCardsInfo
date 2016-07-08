package com.dbottillo.mtgsearchfree.view.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.DeckBucket;
import com.dbottillo.mtgsearchfree.presenter.DecksPresenter;
import com.dbottillo.mtgsearchfree.util.AnimationUtil;
import com.dbottillo.mtgsearchfree.util.InputUtil;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.PermissionUtil;
import com.dbottillo.mtgsearchfree.util.TrackingManager;
import com.dbottillo.mtgsearchfree.view.DecksView;
import com.dbottillo.mtgsearchfree.view.activities.DeckActivity;
import com.dbottillo.mtgsearchfree.view.adapters.DeckListAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class DecksFragment extends BasicFragment implements View.OnClickListener, TextView.OnEditorActionListener, DecksView, DeckListAdapter.OnDeckListener, PermissionUtil.PermissionListener {

    private ArrayList<Deck> decks;
    private DeckListAdapter deckListAdapter;

    private static final int READ_REQUEST_CODE = 42;
    private int heightNewDeckContainer = -1;
    private boolean newDeckViewOpen = false;

    @Bind(R.id.add_new_deck)
    FloatingActionButton newDeck;

    @Bind(R.id.deck_list)
    ListView listView;

    @Bind(R.id.progress)
    SmoothProgressBar progressBar;

    @Bind(R.id.empty_view)
    TextView emptyView;

    @Bind(R.id.new_deck_overlay)
    View newDeckOverlay;

    @Bind(R.id.new_deck_name_container)
    View newDeckContainer;

    @Bind(R.id.new_deck_name)
    AppCompatEditText newDeckName;

    @Inject
    DecksPresenter decksPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_decks, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setActionBarTitle(getString(R.string.action_decks));
        setHasOptionsMenu(true);

        emptyView.setText(R.string.empty_decks);
        progressBar.setVisibility(View.GONE);
        newDeckName.setImeOptions(EditorInfo.IME_ACTION_DONE);
        newDeckName.setOnEditorActionListener(this);
        newDeckOverlay.setAlpha(0.0f);
        newDeckOverlay.setVisibility(View.GONE);
        newDeckOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeNewDeck();
            }
        });
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
        View footerView = LayoutInflater.from(getContext()).inflate(R.layout.fab_button_list_footer, listView, false);
        listView.addFooterView(footerView);
        listView.setAdapter(deckListAdapter);

        AnimationUtil.growView(newDeck);
        newDeck.setOnClickListener(this);

        getMTGApp().getUiGraph().inject(this);
        decksPresenter.init(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        decksPresenter.loadDecks();
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
        LOG.d();
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
        LOG.d();
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
            LOG.d();
            String text = newDeckName.getText().toString();
            closeNewDeck();
            decksPresenter.addDeck(text);
            TrackingManager.trackNewDeck(text);
            return true;
        }
        return false;
    }

    @Override
    public void onDeckSelected(Deck deck) {
        LOG.d();
        Intent intent = new Intent(getActivity(), DeckActivity.class);
        intent.putExtra("deck", deck);
        startActivity(intent);
    }

    @Override
    public void onDeckDelete(final Deck deck) {
        LOG.d();
        if (deck.getNumberOfCards() > 0) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.deck_delete_title)
                    .setMessage(R.string.deck_delete_text)
                    .setPositiveButton(R.string.deck_delete_confirmation, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            decksPresenter.deleteDeck(deck);
                            TrackingManager.trackDeleteDeck(deck.getName());
                        }

                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();

        } else {
            decksPresenter.deleteDeck(deck);
            TrackingManager.trackDeleteDeck(deck.getName());
        }
    }

    @Override
    public void decksLoaded(List<Deck> newDecks) {
        LOG.d();
        decks.clear();
        for (Deck deck : newDecks) {
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
    public void deckLoaded(DeckBucket bucket) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.decks, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i1 = item.getItemId();
        if (i1 == R.id.action_import) {
            importDeck();
            return true;
        }
        return false;
    }

    private void importDeck() {
        LOG.d();
        dbActivity.requestPermission(PermissionUtil.TYPE.READ_STORAGE, this);
    }

    @Override
    public void permissionGranted() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void permissionNotGranted() {
        Toast.makeText(getContext(), R.string.error_export_db, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                decksPresenter.importDeck(uri);
            }
        }
    }
}
