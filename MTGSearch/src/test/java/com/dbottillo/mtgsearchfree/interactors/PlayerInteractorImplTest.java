package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.model.storage.PlayersStorage;
import com.dbottillo.mtgsearchfree.util.Logger;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.List;

import io.reactivex.observers.TestObserver;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PlayerInteractorImplTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    PlayersStorage storage;
    @Mock
    Player player;
    @Mock
    Logger logger;

    private List<Player> players = Arrays.asList(new Player(1, "Jace"), new Player(2, "Liliana"));
    private List<Player> toEditPlayers = Arrays.asList(new Player(3, "Chandra"), new Player(4, "Sorin"));

    private PlayerInteractor underTest;

    @Before
    public void setup() {
        when(storage.load()).thenReturn(players);
        when(storage.addPlayer(player)).thenReturn(players);
        when(storage.removePlayer(player)).thenReturn(players);
        when(storage.editPlayer(player)).thenReturn(players);
        when(storage.editPlayers(toEditPlayers)).thenReturn(players);
        underTest = new PlayerInteractorImpl(storage, logger);
    }

    @Test
    public void testLoad() {
        TestObserver<List<Player>> testSubscriber = new TestObserver<>();
        underTest.load().subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(players);
        verify(storage).load();
    }

    @Test
    public void testAddPlayer() {
        TestObserver<List<Player>> testSubscriber = new TestObserver<>();
        underTest.addPlayer(player).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(players);
        verify(storage).addPlayer(player);
    }

    @Test
    public void testEditPlayer() {
        TestObserver<List<Player>> testSubscriber = new TestObserver<>();
        underTest.editPlayer(player).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(players);
        verify(storage).editPlayer(player);
    }

    @Test
    public void testEditPlayers() {
        TestObserver<List<Player>> testSubscriber = new TestObserver<>();
        underTest.editPlayers(toEditPlayers).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(players);
        verify(storage).editPlayers(toEditPlayers);
    }

    @Test
    public void testRemovePlayer() {
        TestObserver<List<Player>> testSubscriber = new TestObserver<>();
        underTest.removePlayer(player).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(players);
        verify(storage).removePlayer(player);
    }
}