package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.interactors.PlayerInteractor;
import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.util.Logger;
import com.dbottillo.mtgsearchfree.view.PlayersView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PlayerPresenterImplTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private PlayerPresenter underTest;

    @Mock
    PlayerInteractor interactor;

    @Mock
    PlayersView view;

    @Mock
    Player player;

    private List<Player> players;

    @Mock
    List<Player> toEdit;

    @Mock
    Logger logger;

    @Before
    public void setup() {
        players = new ArrayList<>();
        players.add(new Player(1, "Liliana"));
        players.add(new Player(2, "Jayce"));

        when(interactor.load()).thenReturn(Observable.just(players));
        when(interactor.addPlayer(any(Player.class))).thenReturn(Observable.just(players));
        when(interactor.editPlayer(player)).thenReturn(Observable.just(players));
        when(interactor.removePlayer(player)).thenReturn(Observable.just(players));
        when(interactor.editPlayers(toEdit)).thenReturn(Observable.just(players));
        underTest = new PlayerPresenterImpl(interactor, new TestRunnerFactory(), logger);
        underTest.init(view);
    }

    @Test
    public void testLoadPlayers() {
        underTest.loadPlayers();
        verify(interactor).load();
        verify(view).showLoading();
        verify(view).playersLoaded(players);
    }

    @Test
    public void testAddPlayer() {
        underTest.loadPlayers();
        underTest.addPlayer();
        verify(interactor).addPlayer(any(Player.class));
        verify(view, times(2)).showLoading();
        verify(view, times(2)).playersLoaded(players);
    }

    @Test
    public void testEditPlayer() {
        underTest.editPlayer(player);
        verify(interactor).editPlayer(player);
        verify(view).showLoading();
        verify(view).playersLoaded(players);
    }

    @Test
    public void testEditPlayers() {
        underTest.editPlayers(toEdit);
        verify(interactor).editPlayers(toEdit);
        verify(view).showLoading();
        verify(view).playersLoaded(players);
    }

    @Test
    public void testRemovePlayer() {
        underTest.removePlayer(player);
        verify(interactor).removePlayer(player);
        verify(view).showLoading();
        verify(view).playersLoaded(players);
    }
}