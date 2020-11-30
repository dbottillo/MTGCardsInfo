package com.dbottillo.mtgsearchfree.model

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit

class DeckBucketTest {

    @Rule @JvmField var mockitoRule = MockitoJUnit.rule()!!

    private val creature = MTGCard(1, 101)
    private val creature2 = MTGCard(2, 102)
    private val creature3 = MTGCard(3, 103)

    private val instant = MTGCard(4, 104)
    private val instant2 = MTGCard(5, 105)
    private val sorcery = MTGCard(6, 106)

    private val land = MTGCard(7, 107)
    private val land2 = MTGCard(8, 108)

    private val generic = MTGCard(9, 109)
    private val generic2 = MTGCard(10, 110)
    private val generic3 = MTGCard(11, 111)
    private val generic4 = MTGCard(12, 112)

    lateinit var input: ArrayList<MTGCard>
    lateinit var underTest: DeckBucket

    @Before
    fun setup() {
        input = ArrayList()

        creature.addType("Creature")
        creature.quantity = 3
        creature2.addType("Creature")
        creature3.addType("Creature")

        instant.addType("Instant")
        instant2.addType("Instant")
        instant2.quantity = 4
        sorcery.addType("Sorcery")

        land.isLand = true
        land2.isLand = true
        land2.quantity = 10

        generic4.isSideboard = true
        generic4.quantity = 5

        input.add(creature)
        input.add(creature2)
        input.add(creature3)
        input.add(instant)
        input.add(instant2)
        input.add(sorcery)
        input.add(land)
        input.add(land2)
        input.add(generic)
        input.add(generic2)
        input.add(generic3)
        input.add(generic4)

        underTest = DeckBucket()
        underTest.cards = input
    }

    @Test
    fun createsBucketProperly() {
        assertThat(underTest.numberOfCards()).isEqualTo(NUMBER_OF_TOTAL_CARDS)
        assertThat(underTest.numberOfUniqueCards()).isEqualTo(NUMBER_OF_UNIQUE_CARDS)
        assertThat(underTest.numberOfCardsInSideboard()).isEqualTo(5)
        assertThat(underTest.numberOfCardsWithoutSideboard()).isEqualTo(NUMBER_OF_TOTAL_CARDS - 5)
        assertThat(underTest.numberOfUniqueCardsInSideboard()).isEqualTo(1)

        assertThat(underTest.other.size).isEqualTo(3)
        assertThat(underTest.other[0]).isEqualTo(generic)
        assertThat(underTest.other[1]).isEqualTo(generic2)
        assertThat(underTest.other[2]).isEqualTo(generic3)

        assertThat(underTest.lands.size).isEqualTo(2)
        assertThat(underTest.lands[0]).isEqualTo(land)
        assertThat(underTest.lands[1]).isEqualTo(land2)

        assertThat(underTest.creatures.size).isEqualTo(3)
        assertThat(underTest.creatures[0]).isEqualTo(creature)
        assertThat(underTest.creatures[1]).isEqualTo(creature2)
        assertThat(underTest.creatures[2]).isEqualTo(creature3)

        assertThat(underTest.instantAndSorceries.size).isEqualTo(3)
        assertThat(underTest.instantAndSorceries[0]).isEqualTo(instant)
        assertThat(underTest.instantAndSorceries[1]).isEqualTo(instant2)
        assertThat(underTest.instantAndSorceries[2]).isEqualTo(sorcery)

        assertThat(underTest.side.size).isEqualTo(1)
        assertThat(underTest.side[0]).isEqualTo(generic4)
    }

    @Test
    fun returnsCorrectSizeOfEachTypeOfCards() {
        assertThat(underTest.numberOfUniqueCreatures).isEqualTo(3)
        assertThat(underTest.numberOfCreatures).isEqualTo(5)

        assertThat(underTest.numberOfUniqueInstantAndSorceries).isEqualTo(3)
        assertThat(underTest.numberOfInstantAndSorceries).isEqualTo(6)

        assertThat(underTest.numberOfUniqueLands).isEqualTo(2)
        assertThat(underTest.numberOfLands).isEqualTo(11)

        assertThat(underTest.numberOfUniqueOther).isEqualTo(3)
        assertThat(underTest.numberOfOther).isEqualTo(3)
    }

    @Test
    fun returnsSameCards() {
        val output = underTest.cards
        for (card in output) {
            assertThat(input.contains(card)).isTrue()
        }
    }
}

const val NUMBER_OF_UNIQUE_CARDS = 12
const val NUMBER_OF_TOTAL_CARDS = 30
