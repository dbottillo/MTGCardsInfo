package com.dbottillo.mtgsearchfree.model.storage

import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.model.MTGCard
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit

class CardsHelperTest {

    @JvmField @Rule
    val mockitoRule = MockitoJUnit.rule()!!

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

    internal fun checkCondition(prepareFilter: (CardFilter) -> Unit, validateCards: (MTGCard) -> Unit) {
        val cardFilter = CardFilter()
        prepareFilter(cardFilter)
        val cards = generateCards()
        val result = underTest.filterCards(cardFilter, cards)

        result.forEach {
            validateCards(it)
        }
    }

    internal fun generateCards(): List<MTGCard> {
        val list = mutableListOf<MTGCard>()
        val colors = listOf("W", "U", "B", "R", "G")
        val rarity = listOf("Common", "Uncommon", "Rare", "Mythic rare")
        colors.forEachIndexed { index, color ->
            rarity.forEach {
                list.add(generateCard(cost = color, rarity = it, colors = listOf(index)))
            }
        }
        val artifactCard = MTGCard()
        artifactCard.isArtifact = true
        artifactCard.setCardName("Card")
        artifactCard.rarity = "Common"
        list.add(artifactCard)
        val landCard = MTGCard()
        landCard.isLand = true
        landCard.setCardName("Card")
        landCard.rarity = "Uncommon"
        list.add(landCard)
        val eldraziCard = MTGCard()
        eldraziCard.setCardName("Card")
        eldraziCard.rarity = "Rare"
        list.add(eldraziCard)
        return list
    }

    private fun generateCard(name: String = "Card",
                             cost: String = "WU",
                             rarity: String = "Common",
                             colors: List<Int> = listOf()): MTGCard {
        val card = MTGCard()
        card.setCardName(name)
        card.manaCost = cost
        card.rarity = rarity
        card.colors = colors.toMutableList()
        return card
    }
}