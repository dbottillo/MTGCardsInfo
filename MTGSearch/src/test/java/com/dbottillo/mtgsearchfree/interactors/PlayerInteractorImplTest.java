package com.dbottillo.mtgsearchfree.interactors;

import android.test.suitebuilder.annotation.SmallTest;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.model.storage.DecksStorage;
import com.dbottillo.mtgsearchfree.model.storage.PlayersStorage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.observers.TestSubscriber;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SmallTest
@RunWith(RobolectricTestRunner.class)
public class PlayerInteractorImplTest {
    
    static PlayersStorage storage;
    static Player player;
    static List<Player> players = Arrays.asList(new Player(1, "Jace"), new Player(2, "Liliana"));
    static List<Player> toEditPlayers = Arrays.asList(new Player(3, "Chandra"), new Player(4, "Sorin"));

    private PlayerInteractor playerInteractor;

    @BeforeClass
    public static void setup(){
        storage = mock(PlayersStorage.class);
        player = mock(Player.class);
        when(storage.load()).thenReturn(players);
        when(storage.addPlayer(player)).thenReturn(players);
        when(storage.removePlayer(player)).thenReturn(players);
        when(storage.editPlayer(player)).thenReturn(players);
        when(storage.editPlayers(toEditPlayers)).thenReturn(players);
    }
    @Before
    public void init() {
        playerInteractor = new PlayerInteractorImpl(storage);
    }


    @Test
    public void testLoad() {
        TestSubscriber<List<Player>> testSubscriber = new TestSubscriber<>();
        playerInteractor.load().subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(players));
        verify(storage).load();
    }

    @Test
    public void testAddPlayer() {
        TestSubscriber<List<Player>> testSubscriber = new TestSubscriber<>();
        playerInteractor.addPlayer(player).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(players));
        verify(storage).addPlayer(player);
    }

    @Test
    public void testEditPlayer() {
        TestSubscriber<List<Player>> testSubscriber = new TestSubscriber<>();
        playerInteractor.editPlayer(player).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(players));
        verify(storage).editPlayer(player);
    }

    @Test
    public void testEditPlayers() {
        TestSubscriber<List<Player>> testSubscriber = new TestSubscriber<>();
        playerInteractor.editPlayers(toEditPlayers).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(players));
        verify(storage).editPlayers(toEditPlayers);
    }

    @Test
    public void testRemovePlayer() {
        TestSubscriber<List<Player>> testSubscriber = new TestSubscriber<>();
        playerInteractor.removePlayer(player).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(players));
        verify(storage).removePlayer(player);
    }
}