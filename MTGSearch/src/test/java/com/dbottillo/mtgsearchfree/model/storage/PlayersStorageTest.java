package com.dbottillo.mtgsearchfree.model.storage;

import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.model.database.PlayerDataSource;
import com.dbottillo.mtgsearchfree.util.Logger;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PlayersStorageTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    PlayerDataSource playerDataSource;

    @Mock
    Player player;

    @Mock
    Logger logger;

    private List<Player> players = Arrays.asList(new Player(1, "Jayce"), new Player(2, "Liliana"));

    private PlayersStorage underTest;

    @Before
    public void setup() {
        when(playerDataSource.getPlayers()).thenReturn(players);
        underTest = new PlayersStorage(playerDataSource, logger);
    }

    @Test
    public void testLoad() {
        List<Player> result = underTest.load();
        verify(playerDataSource).getPlayers();
        assertThat(result, is(players));
    }

    @Test
    public void testAddPlayer() {
        List<Player> result = underTest.addPlayer(player);
        verify(playerDataSource).savePlayer(player);
        assertThat(result, is(players));
    }

    @Test
    public void testEditPlayer() {
        List<Player> result = underTest.editPlayer(player);
        verify(playerDataSource).savePlayer(player);
        assertThat(result, is(players));
    }

    @Test
    public void testEditPlayers() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        List<Player> toEdit = Arrays.asList(player1, player2);
        List<Player> result = underTest.editPlayers(toEdit);
        verify(playerDataSource).savePlayer(player1);
        verify(playerDataSource).savePlayer(player2);
        assertThat(result, is(players));
    }

    @Test
    public void testRemovePlayer() {
        List<Player> result = underTest.removePlayer(player);
        verify(playerDataSource).removePlayer(player);
        assertThat(result, is(players));
    }
}