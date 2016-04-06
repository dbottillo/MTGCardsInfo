package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.interactors.PlayerInteractor;
import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.PlayersView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PlayerPresenterImpl implements PlayerPresenter {

    PlayerInteractor interactor;

    PlayersView playerView;
    Subscription subscription = null;
    List<Player> players;

    String[] names = {"Teferi", "Nicol Bolas", "Gerrard", "Ajani", "Jace",
            "Liliana", "Elspeth", "Tezzeret", "Garruck",
            "Chandra", "Venser", "Doran", "Sorin"};

    public PlayerPresenterImpl(PlayerInteractor interactor) {

        LOG.d("created");
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
        playerView.showLoading();
        Observable<List<Player>> obs = interactor.load()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe(playersSubscription);
    }

    @Override
    public void addPlayer() {
        LOG.d();
        Player player = new Player(getUniqueIdForPlayer(), getUniqueNameForPlayer());
        playerView.showLoading();
        Observable<List<Player>> obs = interactor.addPlayer(player)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe(playersSubscription);
    }

    @Override
    public void editPlayer(Player player) {
        LOG.d();
        playerView.showLoading();
        Observable<List<Player>> obs = interactor.editPlayer(player)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe(playersSubscription);
    }

    @Override
    public void editPlayer(ArrayList<Player> players) {
        LOG.d();
        playerView.showLoading();
        Observable<List<Player>> obs = interactor.editPlayers(players)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe(playersSubscription);
    }

    @Override
    public void removePlayer(Player player) {
        LOG.d();
        playerView.showLoading();
        Observable<List<Player>> obs = interactor.removePlayer(player)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe(playersSubscription);
    }

    private Subscriber<List<Player>> playersSubscription = new Subscriber<List<Player>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(List<Player> newPlayers) {
            LOG.d();
            players = newPlayers;
            playerView.playersLoaded(players);
        }
    };

    private String getUniqueNameForPlayer() {
        boolean unique = false;
        int pickedNumber = 0;
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
        for (Player player : players) {
            if (id == player.getId()) {
                id++;
            }
        }
        return id;
    }
}