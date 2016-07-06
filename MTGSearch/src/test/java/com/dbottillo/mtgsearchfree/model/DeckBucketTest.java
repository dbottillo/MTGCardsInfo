package com.dbottillo.mtgsearchfree.model;

import android.test.suitebuilder.annotation.SmallTest;

import com.dbottillo.mtgsearchfree.BaseTest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@SmallTest
public class DeckBucketTest extends BaseTest {

    @Test
    public void test_BucketIsCreatedCorrectly() {
        List<MTGCard> input = new ArrayList<>();
        MTGCard card = new MTGCard(1);
        input.add(card);
        MTGCard card2 = new MTGCard(2);
        card2.setAsALand(true);
        input.add(card2);
        MTGCard card3 = new MTGCard(3);
        card3.addType("Creature");
        input.add(card3);

        MTGCard card4 = new MTGCard(4);
        card4.addType("Instant");
        input.add(card4);

        MTGCard card5 = new MTGCard(5);
        card5.addType("Sorcery");
        input.add(card5);

        MTGCard card6 = new MTGCard(6);
        card6.setSideboard(true);
        input.add(card6);

        DeckBucket bucket = new DeckBucket();
        bucket.setCards(input);

        assertThat(bucket.size(), is(6));
        assertThat(bucket.sizeNoSideboard(), is(5));
        assertThat(bucket.sizeSideBoard(), is(1));

        assertThat(bucket.other.size(), is(1));
        assertThat(bucket.other.get(0), is(card));

        assertThat(bucket.lands.size(), is(1));
        assertThat(bucket.lands.get(0), is(card2));

        assertThat(bucket.creatures.size(), is(1));
        assertThat(bucket.creatures.get(0), is(card3));

        assertThat(bucket.instantAndSorceries.size(), is(2));
        assertThat(bucket.instantAndSorceries.get(0), is(card4));
        assertThat(bucket.instantAndSorceries.get(1), is(card5));

        assertThat(bucket.side.size(), is(1));
        assertThat(bucket.side.get(0), is(card6));

    }

    @Test
    public void test_BucketWillReturnSameCards() {
        List<MTGCard> input = new ArrayList<>();
        MTGCard card = new MTGCard(1);
        card.setMultiVerseId(100);
        input.add(card);
        MTGCard card2 = new MTGCard(2);
        card2.setMultiVerseId(101);
        input.add(card2);
        MTGCard card3 = new MTGCard(3);
        card3.setMultiVerseId(102);
        input.add(card3);

        DeckBucket bucket = new DeckBucket();
        bucket.setCards(input);

        List<MTGCard> output = bucket.getCards();

        assertEquals(output, input);
    }

}