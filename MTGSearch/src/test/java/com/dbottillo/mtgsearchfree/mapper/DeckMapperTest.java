package com.dbottillo.mtgsearchfree.mapper;

import android.test.suitebuilder.annotation.SmallTest;

import com.dbottillo.mtgsearchfree.BaseTest;
import com.dbottillo.mtgsearchfree.model.DeckBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@SmallTest
public class DeckMapperTest extends BaseTest {

    DeckMapper deckMapper;

    @Before
    public void setup() {
        deckMapper = new DeckMapper();
    }

    @Test
    public void test_mapIsNotNull() throws Exception {
        List<MTGCard> cards = new ArrayList<>();
        DeckBucket bucket = deckMapper.map(cards);
        assertNotNull(bucket);
    }

    @Test
    public void test_SizeBucketIsCorrect() throws Exception {
        List<MTGCard> cards = new ArrayList<>();
        cards.add(new MTGCard(2));
        cards.add(new MTGCard(3));
        DeckBucket bucket = deckMapper.map(cards);
        assertThat(bucket.size(), is(2));
    }

    @Test
    public void test_TwoSizesBucketIsCorrect() throws Exception {
        List<MTGCard> cards = new ArrayList<>();
        cards.add(new MTGCard(2));
        cards.add(new MTGCard(3));
        MTGCard card = new MTGCard(4);
        card.setSideboard(true);
        cards.add(card);
        DeckBucket bucket = deckMapper.map(cards);
        assertThat(bucket.size(), is(3));
        assertThat(bucket.sizeNoSideboard(), is(2));
        assertThat(bucket.sizeSideBoard(), is(1));
    }
}