package com.dbottillo.mtgsearchfree.model.storage

import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.SearchParams
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Assert.assertFalse
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

class CardsHelperTest {

    @JvmField @Rule
    val mockitoRule = MockitoJUnit.rule()!!

    @Mock lateinit var searchParams: SearchParams

    lateinit var underTest: CardsHelper

    @Before
    fun setUp() {
        underTest = CardsHelper()
    }

    @Test
    fun `filter cards should return all cards if filter is default and search is null`() {
        val cardFilter = CardFilter()
        val cards = generateCards()

        val result = underTest.filterCards(cardFilter, null, cards)

        assertThat(result, `is`(cards))
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

        checkCondition(prepareFilter ={
            it.red = false
            it.rare = false
        }, validateCards = {
            assertFalse(it.isRed)
            assertFalse(it.isRare)
        })

        checkCondition(prepareFilter ={
            it.green = false
            it.mythic = false
        }, validateCards = {
            assertFalse(it.isGreen)
            assertFalse(it.isMythicRare)
        })

        checkCondition(prepareFilter ={
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
    fun `filter cards by color or rarity should return all cards if search is provided`() {
        checkCards(searchParams){ it.white = false }
        checkCards(searchParams){ it.blue = false }
        checkCards(searchParams){ it.red = false }
        checkCards(searchParams){ it.green = false }
        checkCards(searchParams){ it.black = false }
        checkCards(searchParams){ it.common = false }
        checkCards(searchParams){ it.uncommon = false }
        checkCards(searchParams){ it.rare = false }
        checkCards(searchParams){ it.mythic = false }
    }

    @Test
    fun `filter cards by color and rarity should return all cards if search is provided`() {
        checkCards(searchParams) {
            it.white = false
            it.common = false
        }
        checkCards(searchParams) {
            it.blue = false
            it.uncommon = false
        }
        checkCards(searchParams) {
            it.red = false
            it.rare = false
        }
        checkCards(searchParams) {
            it.green = false
            it.mythic = false
        }
        checkCards(searchParams) {
            it.black = false
            it.common = false
            it.uncommon = false
        }
    }

    internal fun checkCondition(prepareFilter: (CardFilter) -> Unit, validateCards: (MTGCard) -> Unit){
        val cardFilter = CardFilter()
        prepareFilter(cardFilter)
        val cards = generateCards()
        val result = underTest.filterCards(cardFilter, null, cards)
        result.forEach{
            validateCards(it)
            assertFalse(it.isBlue)
        }
    }

    internal fun checkCards(searchParams: SearchParams, withFilter: (CardFilter) -> Unit){
        val cardFilter = CardFilter()
        withFilter(cardFilter)
        val cards = generateCards()
        val result = underTest.filterCards(cardFilter, searchParams, cards)
        assertThat(result, `is`(cards))
    }

    internal fun generateCards() : List<MTGCard>{
        val list = mutableListOf<MTGCard>()
        val colors = listOf("W", "B", "R", "G", "B")
        val rarity = listOf("Common", "Uncommon", "Rare", "Mythic rare")
        colors.forEachIndexed { index, color ->
            rarity.forEach {
                val card = MTGCard()
                card.manaCost = color
                card.rarity = it
                card.colors = listOf(index)
                list.add(card)
            }
        }
        return list
    }
}