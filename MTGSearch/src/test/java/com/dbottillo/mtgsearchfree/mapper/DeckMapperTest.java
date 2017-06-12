package com.dbottillo.mtgsearchfree.mapper;

import com.dbottillo.mtgsearchfree.model.CardsCollection;
import com.dbottillo.mtgsearchfree.model.DeckBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class DeckMapperTest {

    private DeckMapper underTest;

    @Before
    public void setup() {
        underTest = new DeckMapper();
    }

    @Test
    public void test_mapIsNotNull() throws Exception {
        List<MTGCard> cards = new ArrayList<>();
        DeckBucket bucket = underTest.map(new CardsCollection(cards, null, false));
        assertNotNull(bucket);
    }

    @Test
    public void test_SizeBucketIsCorrect() throws Exception {
        List<MTGCard> cards = new ArrayList<>();
        cards.add(new MTGCard(2));
        cards.add(new MTGCard(3));
        DeckBucket bucket = underTest.map(new CardsCollection(cards, null, false));
        assertThat(bucket.numberOfCards(), is(2));
    }

    @Test
    public void test_TwoSizesBucketIsCorrect() throws Exception {
        List<MTGCard> cards = new ArrayList<>();
        cards.add(new MTGCard(2));
        cards.add(new MTGCard(3));
        MTGCard card = new MTGCard(4);
        card.setSideboard(true);
        cards.add(card);
        DeckBucket bucket = underTest.map(new CardsCollection(cards, null, false));
        assertThat(bucket.numberOfCards(), is(3));
        assertThat(bucket.numberOfCardsWithoutSideboard(), is(2));
        assertThat(bucket.numberOfCardsInSideboard(), is(1));
    }
}