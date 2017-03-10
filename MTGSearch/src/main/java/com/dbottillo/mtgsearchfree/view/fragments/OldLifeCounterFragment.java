package com.dbottillo.mtgsearchfree.view.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.exceptions.MTGException;
import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences;
import com.dbottillo.mtgsearchfree.presenter.PlayerPresenter;
import com.dbottillo.mtgsearchfree.util.AnimationUtil;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.TrackingManager;
import com.dbottillo.mtgsearchfree.view.PlayersView;
import com.dbottillo.mtgsearchfree.view.adapters.OldLifeCounterAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

@Deprecated
public class OldLifeCounterFragment extends BasicFragment implements OldLifeCounterAdapter.OnLifeCounterListener, View.OnClickListener, PlayersView {

    public static OldLifeCounterFragment newInstance() {
        return new OldLifeCounterFragment();
    }

    /*@BindView(R.id.life_counter_dice_scrollview)
    ScrollView diceScrollView;
    @BindView(R.id.life_counter_dice_container)
    LinearLayout diceContainer;*/
    @BindView(R.id.progress)
    SmoothProgressBar progressBar;
    /*@BindView(R.id.life_counter_list)
    ListView lifeListView;*/

    private ArrayList<Player> players;
    private OldLifeCounterAdapter oldLifeCounterAdapter;

    private boolean scrollDownAfterLoad = false;
    private boolean showPoison = false;
    private boolean diceShowed = false;
    private boolean twoHGEnabled = false;

    @Inject
    PlayerPresenter playerPresenter;
    @Inject
    CardsPreferences cardsPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_life_counter, container, false);
        ButterKnife.bind(this, rootView);
        getMTGApp().getUiGraph().inject(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {

        setActionBarTitle(getString(R.string.action_life_counter));

        showPoison = cardsPreferences.showPoison();

        /*View footerView = LayoutInflater.from(getContext()).inflate(R.layout.fab_button_list_footer, lifeListView, false);
        lifeListView.addFooterView(footerView);
*/
        twoHGEnabled = cardsPreferences.twoHGEnabled();


        players = new ArrayList<>();

        oldLifeCounterAdapter = new OldLifeCounterAdapter(getActivity(), players, this, showPoison);
        //lifeListView.setAdapter(oldLifeCounterAdapter);

        setHasOptionsMenu(true);

        playerPresenter.init(this);

        playerPresenter.loadPlayers();
    }

    @Override
    public void onResume() {
        super.onResume();
        setScreenOn(cardsPreferences.screenOn());
    }

    private void setScreenOn(boolean screenOn) {
        LOG.d();
        if (getView() != null) {
            getView().setKeepScreenOn(screenOn);
        }
    }

    private void resetLifeCounter() {
        LOG.d();
        for (Player player : players) {
            player.setLife(twoHGEnabled ? 30 : 20);
            player.setPoisonCount(twoHGEnabled ? 15 : 10);
        }
        playerPresenter.editPlayers(players);
    }


    @Override
    public String getPageTrack() {
        return "/life_counter";
    }


    private void addPlayer() {
        LOG.d();
        if (players.size() == 10) {
            Toast.makeText(getActivity(), R.string.maximum_player, Toast.LENGTH_SHORT).show();
            return;
        }
        playerPresenter.addPlayer();
        scrollDownAfterLoad = true;
    }

    @Override
    public void onRemovePlayer(int position) {
        LOG.d();
        playerPresenter.removePlayer(players.get(position));
        TrackingManager.trackRemovePlayer();
    }

    @Override
    public void onEditPlayer(final int position) {
        LOG.d();
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.MTGDialogTheme);

        alert.setTitle(getString(R.string.edit_player));


        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") View view = layoutInflater.inflate(R.layout.dialog_edit_deck, null);
        final EditText editText = (EditText) view.findViewById(R.id.edit_text);
        editText.setText(players.get(position).getName());
        editText.setSelection(players.get(position).getName().length());
        alert.setView(view);

        alert.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = editText.getText().toString();
                players.get(position).setName(value);
                playerPresenter.editPlayer(players.get(position));
                TrackingManager.trackEditPlayer();
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();

    }

    @Override
    public void onLifeCountChange(int position, int value) {
        LOG.d();
        TrackingManager.trackLifeCountChanged();
        players.get(position).changeLife(value);
        playerPresenter.editPlayer(players.get(position));
    }

    @Override
    public void onPoisonCountChange(int position, int value) {
        LOG.d();
        TrackingManager.trackPoisonCountChanged();
        players.get(position).changePoisonCount(value);
        playerPresenter.editPlayer(players.get(position));
    }

    private void launchDice() {
        LOG.d();
        if (diceShowed) {
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                player.setDiceResult(-1);
            }
        } else {
        /*diceScrollView.setVisibility(View.VISIBLE);
        diceContainer.removeAllViews();
        int heightRow = lifeListView.getChildAt(0).getHeight();
        ArrayList<TextView> playerResults = new ArrayList<TextView>(players.size());*/
            //int[] results = new int[players.size()];
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                Random rand = new Random();
                //results[i] = rand.nextInt(20) + 1;
                player.setDiceResult(rand.nextInt(20) + 1);
           /* View dice = LayoutInflater.from(getActivity()).inflate(R.layout.life_counter_dice, null);
            dice.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, heightRow));
            TextView playerName = (TextView) dice.findViewById(R.id.player_name);
            playerName.setText(player.getName());
            playerResults.add((TextView) dice.findViewById(R.id.player_result));
            diceContainer.addView(dice);
            dice.setOnClickListener(tapOnDice);*/
            }
        }
       /* Button closeBtn = new Button(getActivity(), null, R.style.BtnGeneric);
        closeBtn.setBackgroundResource(R.drawable.btn_common);
        closeBtn.setTextColor(getResources().getColor(android.R.color.white));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 20;
        params.gravity = Gravity.CENTER_HORIZONTAL;
        closeBtn.setLayoutParams(params);
        closeBtn.setText(getString(R.string.dice_close));
        closeBtn.setPadding(20, 20, 20, 20);
        closeBtn.setOnClickListener(tapOnDice);
        diceContainer.addView(closeBtn);
        for (int i = 0; i < players.size(); i++) {
            playerResults.get(i).setText(results[i] + "");
        }
        diceShowed = true;*/
        diceShowed = !diceShowed;
        oldLifeCounterAdapter.notifyDataSetChanged();
    }

    private void hideDice() {
        LOG.d();
        /*diceContainer.removeAllViews();
        diceScrollView.setVisibility(View.GONE);*/
        diceShowed = false;
    }

    @Override
    public void onClick(View v) {
        LOG.d();
        addPlayer();
        TrackingManager.trackAddPlayer();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.life_counter, menu);

        MenuItem poison = menu.findItem(R.id.action_poison);
        if (showPoison) {
            poison.setChecked(true);
        } else {
            poison.setChecked(false);
        }

        MenuItem screenOn = menu.findItem(R.id.action_screen_on);
        screenOn.setChecked(cardsPreferences.screenOn());

        MenuItem twoHg = menu.findItem(R.id.action_two_hg);
        twoHg.setChecked(twoHGEnabled);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i1 = item.getItemId();
        if (i1 == R.id.action_reset) {
            resetLifeCounter();
            TrackingManager.trackResetLifeCounter();
            return true;
        }
        if (i1 == R.id.action_dice) {
            launchDice();
            TrackingManager.trackLunchDice();
            return true;
        }
        if (i1 == R.id.action_poison) {
            TrackingManager.trackChangePoisonSetting();
            cardsPreferences.showPoison(!showPoison);
            showPoison = !showPoison;
            getActivity().invalidateOptionsMenu();
            oldLifeCounterAdapter.setShowPoison(showPoison);
            oldLifeCounterAdapter.notifyDataSetChanged();
            return true;
        }
        if (i1 == R.id.action_screen_on) {
            boolean screenOn = cardsPreferences.screenOn();
            cardsPreferences.setScreenOn(!screenOn);
            getActivity().invalidateOptionsMenu();
            setScreenOn(!screenOn);
            TrackingManager.trackScreenOn();
        }
        if (i1 == R.id.action_two_hg) {
            twoHGEnabled = !twoHGEnabled;
            cardsPreferences.setTwoHGEnabled(twoHGEnabled);
            getActivity().invalidateOptionsMenu();
            resetLifeCounter();
            TrackingManager.trackHGLifeCounter();
        }

        return false;
    }

   /* @Override
    public void playersLoaded(List<Player> players) {

    }*/

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String message) {
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(MTGException exception) {

    }

    @Override
    public void playersLoaded(@NotNull List<? extends Player> players) {
        LOG.d();
        progressBar.setVisibility(View.GONE);
        if (players.size() == 0) {
            // need at least one player
            addPlayer();
        } else {
            this.players.clear();
            this.players.addAll(players);
        }
        oldLifeCounterAdapter.notifyDataSetChanged();
        if (scrollDownAfterLoad && getView() != null) {
            //   ((ListView) getView().findViewById(R.id.life_counter_list)).setSelection(players.size() - 1);
        }
        scrollDownAfterLoad = false;
    }
}
