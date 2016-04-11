package com.dbottillo.mtgsearchfree.model;

import android.test.suitebuilder.annotation.SmallTest;

import com.dbottillo.mtgsearchfree.BaseTest;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SmallTest
public class CardsBucketTest extends BaseTest{

    static MTGSet set;
    CardsBucket cardsSetBucket;
    CardsBucket genericBucket;
    List<MTGCard> setCards = Arrays.asList(new MTGCard(5), new MTGCard(6));
    List<MTGCard> genericCards = Arrays.asList(new MTGCard(8), new MTGCard(9));


    @BeforeClass
    public static void setupModel(){
        set = mock(MTGSet.class);
        when(set.getName()).thenReturn("Zendikar");
    }

    @Before
    public void setup(){
        cardsSetBucket = new CardsBucket(set, setCards);
        genericBucket = new CardsBucket("fav", genericCards);
    }

    @Test
    public void testGetCards() throws Exception {
        List<MTGCard> cards = cardsSetBucket.getCards();
        assertThat(cards, is(setCards));
        cards = genericBucket.getCards();
        assertThat(cards, is(genericCards));
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(cardsSetBucket.isValid(set.getName()));
        assertFalse(cardsSetBucket.isValid("fav"));
        assertFalse(genericBucket.isValid(set.getName()));
        assertTrue(genericBucket.isValid("fav"));
    }

    @Test
    public void testSetCards() throws Exception {
        cardsSetBucket.setCards(genericCards);
        assertThat(cardsSetBucket.getCards(), is(genericCards));
    }

    @Test
    public void testGetKey() throws Exception {
        assertThat(cardsSetBucket.getKey(), is(set.getName()));
        assertThat(genericBucket.getKey(), is("fav"));
    }
}