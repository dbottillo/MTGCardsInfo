package com.dbottillo.mtgsearchfree.database;

import com.dbottillo.mtgsearchfree.resources.MTGCard;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class FavouritesDataSourceTest extends BaseDatabaseTest {

    @Test
    public void generate_table_is_correct() {
        String query = FavouritesDataSource.generateCreateTable();
        assertNotNull(query);
        assertThat(query, is("CREATE TABLE IF NOT EXISTS Favourites (_id INTEGER PRIMARY KEY)"));
    }

    @Test
    public void cards_can_be_saved_as_favourites() {
        List<MTGCard> cards = mtgDatabaseHelper.getRandomCard(3);
        for (MTGCard card : cards) {
            FavouritesDataSource.saveFavourites(cardsInfoDbHelper.getWritableDatabase(), card);
        }
        List<MTGCard> favouritesCard = FavouritesDataSource.getCards(cardsInfoDbHelper.getReadableDatabase(), true);
        assertThat(favouritesCard.size(), is(cards.size()));
        assertTrue(cards.containsAll(favouritesCard));
        assertTrue(favouritesCard.containsAll(cards));
    }

    @Test
    public void cards_can_be_removed_from_favourites() {
        List<MTGCard> cards = mtgDatabaseHelper.getRandomCard(3);
        for (MTGCard card : cards) {
            FavouritesDataSource.saveFavourites(cardsInfoDbHelper.getWritableDatabase(), card);
        }
        FavouritesDataSource.removeFavourites(cardsInfoDbHelper.getWritableDatabase(), cards.get(0));
        List<MTGCard> favouritesCard = FavouritesDataSource.getCards(cardsInfoDbHelper.getReadableDatabase(), true);
        assertThat(favouritesCard.size(), is(2));
        assertTrue(cards.containsAll(favouritesCard));
        assertFalse(favouritesCard.contains(cards.get(0)));
    }

}