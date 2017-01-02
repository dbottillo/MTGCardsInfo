package com.dbottillo.mtgsearchfree.model;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CardsBucketTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    MTGSet set;

    private CardsBucket cardsSetBucket;
    private CardsBucket genericBucket;
    private List<MTGCard> setCards = Arrays.asList(new MTGCard(5), new MTGCard(6));
    private List<MTGCard> genericCards = Arrays.asList(new MTGCard(8), new MTGCard(9));


    @Before
    public void setup() {
        when(set.getName()).thenReturn("Zendikar");
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