package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.interactors.PlayerInteractor;
import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.Logger;
import com.dbottillo.mtgsearchfree.util.StringUtil;
import com.dbottillo.mtgsearchfree.view.PlayersView;

import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import rx.Subscription;

public class PlayerPresenterImpl implements PlayerPresenter, Runner.RxWrapperListener<List<Player>> {

    private final PlayerInteractor interactor;
    private final Runner<List<Player>> runner;
    private final Logger logger;

    private PlayersView playerView;
    private Subscription subscription = null;
    private List<Player> players;

    private String[] names = {"Teferi", "Nicol Bolas", "Gerrard", "Ajani", "Jace",
            "Liliana", "Elspeth", "Tezzeret", "Garruck",
            "Chandra", "Venser", "Doran", "Sorin"};

    @Inject
    public PlayerPresenterImpl(PlayerInteractor interactor, RunnerFactory runnerFactory, Logger logger) {
        this.logger = logger;
        this.interactor = interactor;
        this.runner = runnerFactory.simple();
        logger.d("created");
    }

    public void detachView() {
        logger.d();
        subscription.unsubscribe();
    }

    @Override
    public void init(PlayersView view) {
        logger.d();
        playerView = view;
    }

    @Override
    public void loadPlayers() {
        logger.d();
        playerView.showLoading();
        subscription = runner.run(interactor.load(), this);
    }

    @Override
    public void addPlayer() {
        logger.d();
        Player player = new Player(getUniqueIdForPlayer(), getUniqueNameForPlayer());
        playerView.showLoading();
        subscription = runner.run(interactor.addPlayer(player), this);
    }

    @Override
    public void editPlayer(Player player) {
        logger.d();
        playerView.showLoading();
        subscription = runner.run(interactor.editPlayer(player), this);
    }

    @Override
    public void editPlayers(List<Player> players) {
        logger.d();
        playerView.showLoading();
        subscription = runner.run(interactor.editPlayers(players), this);
    }

    @Override
    public void removePlayer(Player player) {
        logger.d();
        playerView.showLoading();
        subscription = runner.run(interactor.removePlayer(player), this);
    }

    @Override
    public void onNext(List<Player> newPlayers) {
        logger.d();
        players = newPlayers;
        playerView.playersLoaded(players);
    }

    @Override
    public void onError(Throwable e) {
        LOG.e(e.getLocalizedMessage());
    }

    @Override
    public void onCompleted() {
        logger.d();
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