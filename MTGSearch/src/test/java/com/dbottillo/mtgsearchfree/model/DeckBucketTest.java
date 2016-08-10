package com.dbottillo.mtgsearchfree.model;

import android.test.suitebuilder.annotation.SmallTest;

import com.dbottillo.mtgsearchfree.BaseTest;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@SmallTest
public class DeckBucketTest extends BaseTest {

    private static final int NUMBER_OF_UNIQUE_CARDS = 12;
    private static final int NUMBER_OF_TOTAL_CARDS = 30;

    MTGCard creature = new MTGCard(1, 101);
    MTGCard creature2 = new MTGCard(2, 102);
    MTGCard creature3 = new MTGCard(3, 103);

    MTGCard instant = new MTGCard(4, 104);
    MTGCard instant2 = new MTGCard(5, 105);
    MTGCard sorcery = new MTGCard(6, 106);

    MTGCard land = new MTGCard(7, 107);
    MTGCard land2 = new MTGCard(8, 108);

    MTGCard generic = new MTGCard(9, 109);
    MTGCard generic2 = new MTGCard(10, 110);
    MTGCard generic3 = new MTGCard(11, 111);
    MTGCard generic4 = new MTGCard(12, 112);

    List<MTGCard> input;
    DeckBucket underTest;

    @Before
    public void setup() {
        input = new ArrayList<>();

        creature.addType("Creature");
        creature.setQuantity(3);
        creature2.addType("Creature");
        creature3.addType("Creature");

        instant.addType("Instant");
        instant2.addType("Instant");
        instant2.setQuantity(4);
        sorcery.addType("Sorcery");

        land.setAsALand(true);
        land2.setAsALand(true);
        land2.setQuantity(10);

        generic4.setSideboard(true);
        generic4.setQuantity(5);

        input.add(creature);
        input.add(creature2);
        input.add(creature3);
        input.add(instant);
        input.add(instant2);
        input.add(sorcery);
        input.add(land);
        input.add(land2);
        input.add(generic);
        input.add(generic2);
        input.add(generic3);
        input.add(generic4);

        underTest = new DeckBucket();
        underTest.setCards(input);
    }

    @Test
    public void createsBucketProperly() {
        assertThat(underTest.numberOfCards(), is(NUMBER_OF_TOTAL_CARDS));
        assertThat(underTest.numberOfUniqueCards(), is(NUMBER_OF_UNIQUE_CARDS));
        assertThat(underTest.numberOfCardsInSideboard(), is(5));
        assertThat(underTest.numberOfCardsWithoutSideboard(), is(NUMBER_OF_TOTAL_CARDS - 5));
        assertThat(underTest.numberOfUniqueCardsInSideboard(), is(1));

        assertThat(underTest.getOther().size(), is(3));
        assertThat(underTest.getOther().get(0), is(generic));
        assertThat(underTest.getOther().get(1), is(generic2));
        assertThat(underTest.getOther().get(2), is(generic3));

        assertThat(underTest.getLands().size(), is(2));
        assertThat(underTest.getLands().get(0), is(land));
        assertThat(underTest.getLands().get(1), is(land2));

        assertThat(underTest.getCreatures().size(), is(3));
        assertThat(underTest.getCreatures().get(0), is(creature));
        assertThat(underTest.getCreatures().get(1), is(creature2));
        assertThat(underTest.getCreatures().get(2), is(creature3));

        assertThat(underTest.getInstantAndSorceries().size(), is(3));
        assertThat(underTest.getInstantAndSorceries().get(0), is(instant));
        assertThat(underTest.getInstantAndSorceries().get(1), is(instant2));
        assertThat(underTest.getInstantAndSorceries().get(2), is(sorcery));

        assertThat(underTest.getSide().size(), is(1));
        assertThat(underTest.getSide().get(0), is(generic4));
    }

    @Test
    public void returnsCorrectSizeOfEachTypeOfCards() {
        assertThat(underTest.getNumberOfUniqueCreatures(), is(3));
        assertThat(underTest.getNumberOfCreatures(), is(5));

        assertThat(underTest.getNumberOfUniqueInstantAndSorceries(), is(3));
        assertThat(underTest.getNumberOfInstantAndSorceries(), is(6));

        assertThat(underTest.getNumberOfUniqueLands(), is(2));
        assertThat(underTest.getNumberOfLands(), is(11));

        assertThat(underTest.getNumberOfUniqueOther(), is(3));
        assertThat(underTest.getNumberOfOther(), is(3));
    }

    @Test
    public void returnsSameCards() {
        List<MTGCard> output = underTest.getCards();
        for (MTGCard card : output) {
            assertTrue(input.contains(card));
        }
    }

}