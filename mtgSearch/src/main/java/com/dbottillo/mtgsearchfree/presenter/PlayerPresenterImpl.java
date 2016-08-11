package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.interactors.PlayerInteractor;
import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.StringUtil;
import com.dbottillo.mtgsearchfree.view.PlayersView;

import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import rx.Subscription;

public class PlayerPresenterImpl implements PlayerPresenter, Runner.RxWrapperListener<List<Player>> {

    PlayerInteractor interactor;

    private PlayersView playerView;
    private Subscription subscription = null;
    private List<Player> players;
    private Runner<List<Player>> runner;

    private String[] names = {"Teferi", "Nicol Bolas", "Gerrard", "Ajani", "Jace",
            "Liliana", "Elspeth", "Tezzeret", "Garruck",
            "Chandra", "Venser", "Doran", "Sorin"};

    @Inject
    public PlayerPresenterImpl(PlayerInteractor interactor, RunnerFactory runnerFactory) {
        LOG.d("created");
        this.interactor = interactor;
        this.runner = runnerFactory.simple();
    }

    public void detachView() {
        LOG.d();
        subscription.unsubscribe();
    }

    @Override
    public void init(PlayersView view) {
        LOG.d();
        playerView = view;
    }

    @Override
    public void loadPlayers() {
        LOG.d();
        playerView.showLoading();
        subscription = runner.run(interactor.load(), this);
    }

    @Override
    public void addPlayer() {
        LOG.d();
        Player player = new Player(getUniqueIdForPlayer(), getUniqueNameForPlayer());
        playerView.showLoading();
        subscription = runner.run(interactor.addPlayer(player), this);
    }

    @Override
    public void editPlayer(Player player) {
        LOG.d();
        playerView.showLoading();
        subscription = runner.run(interactor.editPlayer(player), this);
    }

    @Override
    public void editPlayers(List<Player> players) {
        LOG.d();
        playerView.showLoading();
        subscription = runner.run(interactor.editPlayers(players), this);
    }

    @Override
    public void removePlayer(Player player) {
        LOG.d();
        playerView.showLoading();
        subscription = runner.run(interactor.removePlayer(player), this);
    }

    @Override
    public void onNext(List<Player> newPlayers) {
        LOG.d();
        players = newPlayers;
        playerView.playersLoaded(players);
    }

    @Override
    public void onError(Throwable e) {
        LOG.e(e.getLocalizedMessage());
    }

    @Override
    public void onCompleted() {
        LOG.d();
    }


    private String getUniqueNameForPlayer() {
        boolean unique = false;
        int pickedNumber = 0;
        if (players == null || players.size() <= 0) {
            return names[pickedNumber];
        }
        while (!unique) {
            Random rand = new Random();
            pickedNumber = rand.nextInt(names.length);
            boolean founded = false;
            for (Player player : players) {
                if (StringUtil.contains(player.getName(), names[pickedNumber])) {
                    founded = true;
                    break;
                }
            }
            if (!founded) {
                unique = true;
            }
        }
        return names[pickedNumber];
    }

    private int getUniqueIdForPlayer() {
        int id = 0;
        if (players == null || players.size() <= 0) {
            return id;
        }
        for (Player player : players) {
            if (id == player.getId()) {
                id++;
            }
        }
        return id;
    }

}