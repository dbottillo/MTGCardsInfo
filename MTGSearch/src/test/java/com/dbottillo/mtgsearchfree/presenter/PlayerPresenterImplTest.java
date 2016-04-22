package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.BaseTest;
import com.dbottillo.mtgsearchfree.interactors.PlayerInteractor;
import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.view.PlayersView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PlayerPresenterImplTest extends BaseTest {
    
    PlayerPresenter presenter;
    
    PlayerInteractor interactor;
    
    PlayersView view;
    
    @Mock
    Player player;

    List<Player> players;

    @Mock
    List<Player> toEdit;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        interactor = mock(PlayerInteractor.class);
        view = mock(PlayersView.class);

        players = new ArrayList<>();
        players.add(new Player(1, "Liliana"));
        players.add(new Player(2, "Jayce"));

        when(interactor.load()).thenReturn(Observable.just(players));
        when(interactor.addPlayer(any(Player.class))).thenReturn(Observable.just(players));
        when(interactor.editPlayer(player)).thenReturn(Observable.just(players));
        when(interactor.removePlayer(player)).thenReturn(Observable.just(players));
        when(interactor.editPlayers(toEdit)).thenReturn(Observable.just(players));
        presenter = new PlayerPresenterImpl(interactor);
        presenter.init(view);
    }

    @Test
    public void testLoadPlayers() {
        presenter.loadPlayers();
        verify(interactor).load();
        verify(view).showLoading();
        verify(view).playersLoaded(players);
    }

    @Test
    public void testAddPlayer() {
        presenter.loadPlayers();
        presenter.addPlayer();
        verify(interactor).addPlayer(any(Player.class));
        verify(view, times(2)).showLoading();
        verify(view, times(2)).playersLoaded(players);
    }

    @Test
    public void testEditPlayer() {
        presenter.editPlayer(player);
        verify(interactor).editPlayer(player);
        verify(view).showLoading();
        verify(view).playersLoaded(players);
    }

    @Test
    public void testEditPlayers() {
        presenter.editPlayers(toEdit);
        verify(interactor).editPlayers(toEdit);
        verify(view).showLoading();
        verify(view).playersLoaded(players);
    }

    @Test
    public void testRemovePlayer() {
        presenter.removePlayer(player);
        verify(interactor).removePlayer(player);
        verify(view).showLoading();
        verify(view).playersLoaded(players);
    }
}