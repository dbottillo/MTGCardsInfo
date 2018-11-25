package com.dbottillo.mtgsearchfree.model.storage

import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.Rarity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit

class CardsHelperTest {

    @JvmField @Rule val mockitoRule = MockitoJUnit.rule()!!

    lateinit var underTest: CardsHelper

    @Before
    fun setUp() {
        underTest = CardsHelper()
    }

    @Test
    fun `filter cards should return all cards if filter is default`() {
        val cardFilter = CardFilter()
        val cards = generateCards()

        val result = underTest.filterCards(cardFilter, cards)

        assertThat(result.size, `is`(cards.size))
        assertTrue(result.containsAll(cards))
    }

    @Test
    fun `filter cards should filter cards by color or rarity`() {
        checkCondition({ it.white = false }, { assertFalse(it.isWhite) })
        checkCondition({ it.blue = false }, { assertFalse(it.isBlue) })
        checkCondition({ it.red = false }, { assertFalse(it.isRed) })
        checkCondition({ it.green = false }, { assertFalse(it.isGreen) })
        checkCondition({ it.black = false }, { assertFalse(it.isBlack) })
        checkCondition({ it.common = false }, { assertFalse(it.isCommon) })
        checkCondition({ it.uncommon = false }, { assertFalse(it.isUncommon) })
        checkCondition({ it.rare = false }, { assertFalse(it.isRare) })
        checkCondition({ it.mythic = false }, { assertFalse(it.isMythicRare) })
    }

    @Test
    fun `filter cards should filter cards by color and rarity`() {
        checkCondition(prepareFilter = {
            it.white = false
            it.common = false
        }, validateCards = {
            assertFalse(it.isWhite)
            assertFalse(it.isCommon)
        })

        checkCondition(prepareFilter = {
            it.blue = false
            it.uncommon = false
        }, validateCards = {
            assertFalse(it.isBlue)
            assertFalse(it.isUncommon)
        })

        checkCondition(prepareFilter = {
            it.red = false
            it.rare = false
        }, validateCards = {
            assertFalse(it.isRed)
            assertFalse(it.isRare)
        })

        checkCondition(prepareFilter = {
            it.green = false
            it.mythic = false
        }, validateCards = {
            assertFalse(it.isGreen)
            assertFalse(it.isMythicRare)
        })

        checkCondition(prepareFilter = {
            it.black = false
            it.common = false
            it.uncommon = false
        }, validateCards = {
            assertFalse(it.isBlack)
            assertFalse(it.isCommon)
            assertFalse(it.isUncommon)
        })
    }

    @Test
    fun `filter cards should filter and ordered cards by color`() {
        val cardFilter = CardFilter()
        cardFilter.sortWUBGR = true
        val first = generateCard(name = "ZYX", colors = listOf(0))
        val second = generateCard(name = "ABC", colors = listOf(4))
        val cards = listOf(first, second)

        val result = underTest.filterCards(cardFilter, cards)

        assertThat(result[0], `is`(first))
        assertThat(result[1], `is`(second))
    }

    @Test
    fun `filter cards should filter and ordered cards by name`() {
        val cardFilter = CardFilter()
        cardFilter.sortWUBGR = false
        val first = generateCard(name = "ZYX", colors = listOf(0))
        val second = generateCard(name = "ABC", colors = listOf(4))
        val cards = listOf(first, second)

        val result = underTest.filterCards(cardFilter, cards)

        assertThat(result[0], `is`(second))
        assertThat(result[1], `is`(first))
    }

    @Test
    fun `should sort all type of cards`() {
        val cards = listOf(
                generateCard(name = "Multicolor", colors = listOf(0, 2), colorsIdentity = listOf("W", "B"), isMulticolor = true),
                generateCard(name = "Colorless", colors = listOf(), colorsIdentity = listOf("R"), cost = "{3}{R}"),
                generateCard(name = "Artifact", isArtifact = true),
                generateCard(name = "Land", colorsIdentity = listOf("G"), isLand = true),
                generateCard(name = "Green card", colors = listOf(4), colorsIdentity = listOf("G")),
                generateCard(name = "Red card", colors = listOf(3), colorsIdentity = listOf("R")),
                generateCard(name = "Black card", colors = listOf(2), colorsIdentity = listOf("B")),
                generateCard(name = "White Artifact", isArtifact = true, colors = listOf(0), colorsIdentity = listOf("W")),
                generateCard(name = "Blue card", colors = listOf(1), colorsIdentity = listOf("U")),
                generateCard(name = "Eldrazi"),
                generateCard(name = "White card", colors = listOf(0), colorsIdentity = listOf("W")))

        underTest.sortCards(CardFilter().also { it.sortWUBGR = true }, cards)

        assertThat(cards.size, `is`(11))
        assertThat(cards[0].name, `is`("Eldrazi"))
        assertThat(cards[1].name, `is`("White Artifact"))
        assertThat(cards[2].name, `is`("White card"))
        assertThat(cards[3].name, `is`("Blue card"))
        assertThat(cards[4].name, `is`("Black card"))
        assertThat(cards[5].name, `is`("Colorless"))
        assertThat(cards[6].name, `is`("Red card"))
        assertThat(cards[7].name, `is`("Green card"))
        assertThat(cards[8].name, `is`("Multicolor"))
        assertThat(cards[9].name, `is`("Artifact"))
        assertThat(cards[10].name, `is`("Land"))
    }

    private fun checkCondition(prepareFilter: (CardFilter) -> Unit, validateCards: (MTGCard) -> Unit) {
        val cardFilter = CardFilter()
        prepareFilter(cardFilter)
        val cards = generateCards()
        val result = underTest.filterCards(cardFilter, cards)

        result.forEach {
            validateCards(it)
        }
    }

    private fun generateCards(): List<MTGCard> {
        val list = mutableListOf<MTGCard>()
        val colors = listOf("W", "U", "B", "R", "G")
        val rarity = listOf(Rarity.COMMON, Rarity.UNCOMMON, Rarity.RARE, Rarity.MYTHIC)
        colors.forEachIndexed { index, color ->
            rarity.forEach {
                list.add(generateCard(cost = color, rarity = it, colors = listOf(index)))
            }
        }
        val artifactCard = MTGCard()
        artifactCard.isArtifact = true
        artifactCard.setCardName("Card")
        artifactCard.rarity = Rarity.COMMON
        list.add(artifactCard)
        val landCard = MTGCard()
        landCard.isLand = true
        landCard.setCardName("Card")
        landCard.rarity = Rarity.UNCOMMON
        list.add(landCard)
        val eldraziCard = MTGCard()
        eldraziCard.setCardName("Card")
        eldraziCard.rarity = Rarity.RARE
        list.add(eldraziCard)
        return list
    }

    private fun generateCard(
        name: String = "Card",
        cost: String = "WU",
        rarity: Rarity = Rarity.COMMON,
        colors: List<Int> = listOf(),
        colorsIdentity: List<String> = listOf(),
        isLand: Boolean = false,
        isArtifact: Boolean = false,
        isMulticolor: Boolean = false
    ): MTGCard {
        val card = MTGCard()
        card.setCardName(name)
        card.manaCost = cost
        card.rarity = rarity
        card.colors = colors.toMutableList()
        card.colorsIdentity = colorsIdentity
        card.isLand = isLand
        card.isArtifact = isArtifact
        card.isMultiColor = isMulticolor
        return card
    }
}