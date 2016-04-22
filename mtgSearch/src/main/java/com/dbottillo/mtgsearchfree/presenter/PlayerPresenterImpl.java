package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.interactors.PlayerInteractor;
import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.PlayersView;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

import rx.Subscription;

public class PlayerPresenterImpl implements PlayerPresenter, RxWrapper.RxWrapperListener<List<Player>> {

    PlayerInteractor interactor;

    PlayersView playerView;
    Subscription subscription = null;
    List<Player> players;

    @Inject
    RxWrapper rxWrapper;

    String[] names = {"Teferi", "Nicol Bolas", "Gerrard", "Ajani", "Jace",
            "Liliana", "Elspeth", "Tezzeret", "Garruck",
            "Chandra", "Venser", "Doran", "Sorin"};

    public PlayerPresenterImpl(PlayerInteractor interactor) {
        LOG.d("created");
        MTGApp.graph.inject(this);
        this.interactor = interactor;
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
        System.out.println("load players " + subscription);
        playerView.showLoading();
        subscription = rxWrapper.run(interactor.load(), this);
    }

    @Override
    public void addPlayer() {
        System.out.println("add player " + subscription);
        LOG.d();
        Player player = new Player(getUniqueIdForPlayer(), getUniqueNameForPlayer());
        playerView.showLoading();
        subscription = rxWrapper.run(interactor.addPlayer(player), this);
    }

    @Override
    public void editPlayer(Player player) {
        LOG.d();
        playerView.showLoading();
        subscription = rxWrapper.run(interactor.editPlayer(player), this);
    }

    @Override
    public void editPlayers(List<Player> players) {
        LOG.d();
        playerView.showLoading();
        subscription = rxWrapper.run(interactor.editPlayers(players), this);
    }

    @Override
    public void removePlayer(Player player) {
        LOG.d();
        playerView.showLoading();
        subscription = rxWrapper.run(interactor.removePlayer(player), this);
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
                if (player.getName().toLowerCase(Locale.getDefault()).contains(names[pickedNumber].toLowerCase(Locale.getDefault()))) {
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