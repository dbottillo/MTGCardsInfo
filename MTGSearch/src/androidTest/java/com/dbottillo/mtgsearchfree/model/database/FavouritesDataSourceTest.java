package com.dbottillo.mtgsearchfree.model.database;

import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.util.BaseContextTest;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class FavouritesDataSourceTest extends BaseContextTest {

    private MTGCardDataSource mtgCardDataSource;
    private FavouritesDataSource underTest;

    @Before
    public void setup(){
        CardDataSource cardDataSource = new CardDataSource(cardsInfoDbHelper.getWritableDatabase(), new Gson());
        mtgCardDataSource = new MTGCardDataSource(mtgDatabaseHelper.getReadableDatabase(), cardDataSource);
        underTest = new FavouritesDataSource(cardsInfoDbHelper.getWritableDatabase(), cardDataSource);
    }

    @Test
    public void generate_table_is_correct() {
        String query = FavouritesDataSource.generateCreateTable();
        assertNotNull(query);
        assertThat(query, is("CREATE TABLE IF NOT EXISTS Favourites (_id INTEGER PRIMARY KEY)"));
    }

    @Test
    public void cards_can_be_saved_as_favourites() {
        List<MTGCard> cards = mtgCardDataSource.getRandomCard(3);
        for (MTGCard card : cards) {
            underTest.saveFavourites(card);
        }
        List<MTGCard> favouritesCard = underTest.getCards(true);
        assertThat(favouritesCard.size(), is(cards.size()));
        assertTrue(cards.containsAll(favouritesCard));
        assertTrue(favouritesCard.containsAll(cards));
    }

    @Test
    public void cards_can_be_removed_from_favourites() {
        List<MTGCard> cards = mtgCardDataSource.getRandomCard(3);
        for (MTGCard card : cards) {
            underTest.saveFavourites(card);
        }
        underTest.removeFavourites(cards.get(0));
        List<MTGCard> favouritesCard = underTest.getCards(true);
        assertThat(favouritesCard.size(), is(2));
        assertTrue(cards.containsAll(favouritesCard));
        assertFalse(favouritesCard.contains(cards.get(0)));
    }

}