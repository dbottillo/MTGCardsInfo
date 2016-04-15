package com.dbottillo.mtgsearchfree.model.storage;

import com.dbottillo.mtgsearchfree.BaseTest;
import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PlayersStorageTest extends BaseTest {

    static CardsInfoDbHelper cardsInfoDbHelper;
    static Player player;
    static List<Player> players = Arrays.asList(new Player(1, "Jayce"), new Player(2, "Liliana"));

    PlayersStorage storage;

    @BeforeClass
    public static void staticSetup() {
        player = mock(Player.class);
    }

    @Before
    public void setupStorage() {
        cardsInfoDbHelper = mock(CardsInfoDbHelper.class);
        when(cardsInfoDbHelper.loadPlayers()).thenReturn(players);
        storage = new PlayersStorage(cardsInfoDbHelper);
    }

    @Test
    public void testLoad() {
        List<Player> result = storage.load();
        verify(cardsInfoDbHelper).loadPlayers();
        assertThat(result, is(players));
    }

    @Test
    public void testAddPlayer() {
        List<Player> result = storage.addPlayer(player);
        verify(cardsInfoDbHelper).savePlayer(player);
        assertThat(result, is(players));
    }

    @Test
    public void testEditPlayer() {
        List<Player> result = storage.editPlayer(player);
        verify(cardsInfoDbHelper).editPlayer(player);
        assertThat(result, is(players));
    }

    @Test
    public void testEditPlayers() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        List<Player> toEdit = Arrays.asList(player1, player2);
        List<Player> result = storage.editPlayers(toEdit);
        verify(cardsInfoDbHelper).editPlayer(player1);
        verify(cardsInfoDbHelper).editPlayer(player2);
        assertThat(result, is(players));
    }

    @Test
    public void testRemovePlayer() {
        List<Player> result = storage.removePlayer(player);
        verify(cardsInfoDbHelper).removePlayer(player);
        assertThat(result, is(players));
    }
}