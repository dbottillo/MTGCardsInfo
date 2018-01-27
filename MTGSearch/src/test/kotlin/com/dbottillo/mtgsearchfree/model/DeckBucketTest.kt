package com.dbottillo.mtgsearchfree.model

import org.junit.Assert.assertTrue
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit
import java.util.*

class DeckBucketTest {

    @Rule
    @JvmField
    var mockitoRule = MockitoJUnit.rule()

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
        assertThat(underTest.numberOfCards(), `is`(NUMBER_OF_TOTAL_CARDS))
        assertThat(underTest.numberOfUniqueCards(), `is`(NUMBER_OF_UNIQUE_CARDS))
        assertThat(underTest.numberOfCardsInSideboard(), `is`(5))
        assertThat(underTest.numberOfCardsWithoutSideboard(), `is`(NUMBER_OF_TOTAL_CARDS - 5))
        assertThat(underTest.numberOfUniqueCardsInSideboard(), `is`(1))

        assertThat(underTest.other.size, `is`(3))
        assertThat(underTest.other[0], `is`(generic))
        assertThat(underTest.other[1], `is`(generic2))
        assertThat(underTest.other[2], `is`(generic3))

        assertThat(underTest.lands.size, `is`(2))
        assertThat(underTest.lands[0], `is`(land))
        assertThat(underTest.lands[1], `is`(land2))

        assertThat(underTest.creatures.size, `is`(3))
        assertThat(underTest.creatures[0], `is`(creature))
        assertThat(underTest.creatures[1], `is`(creature2))
        assertThat(underTest.creatures[2], `is`(creature3))

        assertThat(underTest.instantAndSorceries.size, `is`(3))
        assertThat(underTest.instantAndSorceries[0], `is`(instant))
        assertThat(underTest.instantAndSorceries[1], `is`(instant2))
        assertThat(underTest.instantAndSorceries[2], `is`(sorcery))

        assertThat(underTest.side.size, `is`(1))
        assertThat(underTest.side[0], `is`(generic4))
    }

    @Test
    fun returnsCorrectSizeOfEachTypeOfCards() {
        assertThat(underTest.numberOfUniqueCreatures, `is`(3))
        assertThat(underTest.numberOfCreatures, `is`(5))

        assertThat(underTest.numberOfUniqueInstantAndSorceries, `is`(3))
        assertThat(underTest.numberOfInstantAndSorceries, `is`(6))

        assertThat(underTest.numberOfUniqueLands, `is`(2))
        assertThat(underTest.numberOfLands, `is`(11))

        assertThat(underTest.numberOfUniqueOther, `is`(3))
        assertThat(underTest.numberOfOther, `is`(3))
    }

    @Test
    fun returnsSameCards() {
        val output = underTest.cards
        for (card in output) {
            assertTrue(input.contains(card))
        }
    }

}

const val NUMBER_OF_UNIQUE_CARDS = 12
const val NUMBER_OF_TOTAL_CARDS = 30