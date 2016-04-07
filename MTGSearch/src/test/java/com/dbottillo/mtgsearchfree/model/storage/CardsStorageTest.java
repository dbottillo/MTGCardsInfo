package com.dbottillo.mtgsearchfree.model.storage;

import com.dbottillo.mtgsearchfree.BaseTest;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.model.database.MTGDatabaseHelper;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CardsStorageTest extends BaseTest {

    static MTGSet set;
    static MTGDatabaseHelper mtgDatabaseHelper;
    static CardsInfoDbHelper cardsInfoDbHelper;
    static List<MTGCard> setCards = Arrays.asList(new MTGCard(5), new MTGCard(6));

    @BeforeClass
    public static void setup() {
        set = mock(MTGSet.class);
        mtgDatabaseHelper = mock(MTGDatabaseHelper.class);
        cardsInfoDbHelper = mock(CardsInfoDbHelper.class);
        when(mtgDatabaseHelper.getSet(set)).thenReturn(setCards);
    }

    @Test
    public void testLoad() {
        CardsStorage cardsStorage = new CardsStorage(mtgDatabaseHelper, cardsInfoDbHelper);
        List<MTGCard> cards = cardsStorage.load(set);
        assertThat(cards.size(), is(2));
        assertThat(cards.get(0).getId(), is(5L));
        assertThat(cards.get(1).getId(), is(6L));
    }

    /*@Test
    public void testSaveAsFavourite() {

    }

    @Test
    public void testLoadIdFav() {

    }

    @Test
    public void testRemoveFromFavourite() {

    }

    @Test
    public void testGetLuckyCards() {

    }

    @Test
    public void testGetFavourites() {

    }

    @Test
    public void testLoadDeck() {

    }

    @Test
    public void testDoSearch() {

    }*/
}