package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.interactors.PlayerInteractor;
import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.view.PlayersView;

import java.util.ArrayList;
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
    ArrayList<Player> players;

    String[] names = {"Teferi", "Nicol Bolas", "Gerrard", "Ajani", "Jace",
            "Liliana", "Elspeth", "Tezzeret", "Garruck",
            "Chandra", "Venser", "Doran", "Sorin"};

    public PlayerPresenterImpl(PlayerInteractor interactor) {
        this.interactor = interactor;
    }

    public void detachView() {
        subscription.unsubscribe();
    }

    @Override
    public void init(PlayersView view) {
        playerView = view;
    }

    @Override
    public void loadPlayers() {
        playerView.showLoading();
        Observable<ArrayList<Player>> obs = interactor.load()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe(new Subscriber<ArrayList<Player>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ArrayList<Player> players) {
                playersLoaded(players);
            }
        });
    }

    @Override
    public void addPlayer() {
        Player player = new Player(getUniqueIdForPlayer(), getUniqueNameForPlayer());
        playerView.showLoading();
        Observable<ArrayList<Player>> obs = interactor.addPlayer(player)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe(new Subscriber<ArrayList<Player>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ArrayList<Player> players) {
                playersLoaded(players);
            }
        });
    }

    @Override
    public void editPlayer(Player player) {
        playerView.showLoading();
        Observable<ArrayList<Player>> obs = interactor.editPlayer(player)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe(new Subscriber<ArrayList<Player>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ArrayList<Player> players) {
                playersLoaded(players);
            }
        });
    }

    @Override
    public void removePlayer(Player player) {
        playerView.showLoading();
        Observable<ArrayList<Player>> obs = interactor.removePlayer(player)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe(new Subscriber<ArrayList<Player>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ArrayList<Player> players) {
                playersLoaded(players);
            }
        });
    }

    private void playersLoaded(ArrayList<Player> newPlayers) {
        players = newPlayers;
        playerView.playersLoaded(players);
    }

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