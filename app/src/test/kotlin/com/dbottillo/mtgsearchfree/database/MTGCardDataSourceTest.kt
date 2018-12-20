package com.dbottillo.mtgsearchfree.database

import android.content.res.Resources
import com.dbottillo.mtgsearchfree.BuildConfig
import com.dbottillo.mtgsearchfree.model.CMCParam
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.PTParam
import com.dbottillo.mtgsearchfree.model.Rarity
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.readSetListJSON
import com.dbottillo.mtgsearchfree.util.readSingleSetFile
import com.google.gson.Gson
import org.hamcrest.CoreMatchers.anyOf
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.lessThan
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class MTGCardDataSourceTest {

    lateinit var mtgDatabaseHelper: MTGDatabaseHelper
    lateinit var cardsInfoDbHelper: CardsInfoDbHelper
    lateinit var underTest: MTGCardDataSource
    lateinit var kaladesh: MTGSet

    @Before
    fun setup() {
        mtgDatabaseHelper = MTGDatabaseHelper(RuntimeEnvironment.application)
        cardsInfoDbHelper = CardsInfoDbHelper(RuntimeEnvironment.application)
        val cardDataSource = CardDataSource(cardsInfoDbHelper.writableDatabase, Gson())
        val setDataSource = SetDataSource(mtgDatabaseHelper.readableDatabase)
        for (set in setDataSource.sets) {
            if (set.name.equals("kaladesh", ignoreCase = true)) {
                kaladesh = set
                break
            }
        }
        underTest = MTGCardDataSource(mtgDatabaseHelper.readableDatabase, cardDataSource)
    }

    @After
    fun tearDown() {
        cardsInfoDbHelper.clear()
        cardsInfoDbHelper.close()
        mtgDatabaseHelper.close()
    }

    @Test
    fun fetchesAllSets() {
        val setsJ = readSetListJSON()
        // val set = setsJ[0]
        for (set in setsJ) {
            // LOG.e("checking set: " + set.getId() + " - " + set.getName());
            try {
                val cardsJ = readSingleSetFile(set)
                val cards = underTest.getSet(set)
                /*LOG.e("checking set: " + cardsJ.size + " - " + cards.size)
                cardsJ.forEach {cardJ ->
                    var found = false
                    cards.forEach { card ->
                        if (card == cardJ){
                            found = true
                        }

                    }
                    if (!found){
                        LOG.e("not found $cardJ")
                    }
                }*/
                assertThat("checking $set", cardsJ.size, `is`(cards.size))
                assertTrue("set failing $set", cards.containsAll(cardsJ))
            } catch (e: Resources.NotFoundException) {
                LOG.e(set.code + " file not found")
            }
        }
    }

    @Test
    fun getsRandomCards() {
        val cards = underTest.getRandomCard(10)
        assertThat(cards.size, `is`(10))
    }

    @Test
    fun searchCardsByName() {
        val searchParams = SearchParams()
        searchParams.name = "Dragon"
        val cards = underTest.searchCards(searchParams)
        for (card in cards) {
            assertTrue(card.name.toLowerCase(Locale.getDefault()).contains("dragon"))
        }
    }

    @Test
    fun searchCardsByType() {
        val searchParams = SearchParams()
        searchParams.types = "creature"
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for (card in cards) {
            assertTrue(card.type.toLowerCase(Locale.getDefault()).contains("creature"))
        }
    }

    @Test
    fun searchCardsByText() {
        val searchParams = SearchParams()
        searchParams.text = "lifelink"
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for (card in cards) {
            assertTrue(card.text.toLowerCase(Locale.getDefault()).contains("lifelink"))
        }
    }

    @Test
    fun shouldSearchCardsByCmcEqualThan5() {
        val searchParams = SearchParams()
        searchParams.cmc = CMCParam("=", 5, listOf("5"))

        val cards = underTest.searchCards(searchParams)

        for (card in cards) {
            assertThat(card.cmc, `is`(5))
        }
    }

    @Test
    fun shouldSearchCardsByCmcLessThan5() {
        val searchParams = SearchParams()
        searchParams.cmc = CMCParam("<", 5, listOf("5"))

        val cards = underTest.searchCards(searchParams)

        for (card in cards) {
            assertThat(card.cmc, lessThan(5))
        }
    }

    @Test
    fun shouldSearchCardsByCmcEqualThan2WU() {
        val searchParams = SearchParams()
        searchParams.cmc = CMCParam("=", 4, listOf("2", "W", "U"))

        val cards = underTest.searchCards(searchParams)

        for (card in cards) {
            assertThat(card.cmc, `is`(4))
            assertThat(card.manaCost, `is`("{2}{W}{U}"))
        }
    }

    @Test
    fun shouldSearchCardsByCmcGreaterOREqualThan2WWU() {
        val searchParams = SearchParams()
        searchParams.cmc = CMCParam(">=", 5, listOf("2", "WW", "U"))

        val cards = underTest.searchCards(searchParams)

        for (card in cards) {
            assertThat(card.cmc, greaterThanOrEqualTo(5))
            assertThat(card.manaCost, containsString("{W}{W}"))
            assertThat(card.manaCost, containsString("{U}"))
        }
    }

    @Test
    fun shouldSearchCardsByCmcEqualThanX2U() {
        val searchParams = SearchParams()
        searchParams.cmc = CMCParam("=", 3, listOf("X", "2", "U"))

        val cards = underTest.searchCards(searchParams)

        for (card in cards) {
            assertThat(card.cmc, `is`(3))
            assertThat(card.manaCost, `is`("{X}{2}{U}"))
        }
    }

    @Test
    fun shouldSearchCardsByCmcGreaterOREqualThanX2U() {
        val searchParams = SearchParams()
        searchParams.cmc = CMCParam(">=", 3, listOf("X", "2", "U"))

        val cards = underTest.searchCards(searchParams)

        for (card in cards) {
            assertThat(card.cmc, greaterThanOrEqualTo(3))
            assertThat(card.manaCost, containsString("{X}"))
            assertThat(card.manaCost, containsString("{2}"))
            assertThat(card.manaCost, containsString("{U}"))
        }
    }

    @Test
    fun searchCardsByPower() {
        val searchParams = SearchParams()
        for (i in 0 until OPERATOR.values().size) {
            val operator = OPERATOR.values()[i]
            searchParams.power = operator.generatePTParam()
            val cards = underTest.searchCards(searchParams)
            assertTrue(cards.isNotEmpty())
            for (card in cards) {
                operator.assertOperator(Integer.parseInt(card.power))
            }
        }
        searchParams.power = PTParam("", -1)
        var cards = underTest.searchCards(searchParams)
        for (card in cards) {
            assertThat(card.power, containsString("*"))
        }
        searchParams.power = PTParam("=", 0)
        cards = underTest.searchCards(searchParams)
        for (card in cards) {
            assertThat(card.toString(), card.power, anyOf(equalTo("0"), equalTo("+0")))
        }
    }

    @Test
    fun searchCardsByToughness() {
        val searchParams = SearchParams()
        for (i in 0 until OPERATOR.values().size) {
            val operator = OPERATOR.values()[i]
            searchParams.tough = operator.generatePTParam()
            val cards = underTest.searchCards(searchParams)
            assertTrue(cards.isNotEmpty())
            cards.forEach {
                operator.assertOperator(it.toughness.toInt())
            }
        }
        searchParams.tough = PTParam("", -1)
        var cards = underTest.searchCards(searchParams)
        for (card in cards) {
            assertThat(card.toughness, containsString("*"))
        }
        searchParams.tough = PTParam(">=", 2)
        cards = underTest.searchCards(searchParams)
        for (card in cards) {
            assertTrue(Integer.parseInt(card.toughness) >= 2)
        }
    }

    @Test
    fun searchCardsByColor() {
        var searchParams = SearchParams()
        searchParams.isWhite = true
        var cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for (card in cards) {
            assertThat(card.manaCost, containsString("W"))
        }
        searchParams = SearchParams()
        searchParams.isBlue = true
        cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for (card in cards) {
            assertThat(card.manaCost, containsString("U"))
        }
        searchParams = SearchParams()
        searchParams.isRed = true
        cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for (card in cards) {
            assertThat(card.manaCost, containsString("R"))
        }
        searchParams = SearchParams()
        searchParams.isBlack = true
        cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for (card in cards) {
            assertThat(card.manaCost, containsString("B"))
        }
        searchParams = SearchParams()
        searchParams.isGreen = true
        cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for (card in cards) {
            assertThat(card.manaCost, containsString("G"))
        }
    }

    @Test
    fun searchKaladeshCardsWithTwoColors() {
        val searchParams = SearchParams()
        searchParams.isRed = true
        searchParams.isBlue = true
        searchParams.setId = kaladesh.id
        searchParams.text = "Energy"
        val cards = underTest.searchCards(searchParams)

        assertThat(cards.size, `is`(20))
        for (card in cards) {
            // assertTrue(containsString("W") || containsString("U"));
            assertTrue(card.isRed || card.isBlue)
            assertTrue(card.text.toLowerCase().contains("energy"))
        }
    }

    @Test
    fun searchKaladeshCardsWithTwoColorsOnlyMulticolor() {
        val searchParams = SearchParams()
        searchParams.isRed = true
        searchParams.isBlue = true
        searchParams.setOnlyMulti(true)
        searchParams.setId = kaladesh.id
        searchParams.text = "Energy"
        val cards = underTest.searchCards(searchParams)

        assertThat(cards.size, `is`(3))
        for (card in cards) {
            assertTrue(card.isMultiColor)
            assertTrue(card.isRed || card.isBlue)
            assertTrue(card.text.toLowerCase().contains("energy"))
        }
    }

    @Test
    fun searchKaladeshCardsWithTwoColorsOnlyMulticolorAndNoOtherColors() {
        val searchParams = SearchParams()
        searchParams.isRed = true
        searchParams.isBlue = true
        searchParams.isOnlyMultiNoOthers = true
        searchParams.setId = kaladesh.id
        searchParams.text = "Energy"
        val cards = underTest.searchCards(searchParams)

        assertThat(cards.size, `is`(1))
        val card = cards[0]
        assertTrue(card.isMultiColor)
        assertTrue(card.isRed && card.isBlue)
        assertTrue(!card.isBlack && !card.isWhite && !card.isGreen)
        assertThat(card.name, `is`("Whirler Virtuoso"))
        assertTrue(card.text.toLowerCase().contains("energy"))
    }

    @Test
    fun searchKaladeshCardsWithTwoColorsNoMulticolor() {
        val searchParams = SearchParams()
        searchParams.isRed = true
        searchParams.isBlue = true
        searchParams.isNoMulti = true
        searchParams.setId = kaladesh.id
        searchParams.text = "Energy"
        val cards = underTest.searchCards(searchParams)

        assertThat(cards.size, `is`(17))
        for (card in cards) {
            assertFalse(card.isMultiColor)
            assertTrue(card.isRed || card.isBlue)
            assertTrue(card.text.toLowerCase().contains("energy"))
        }
    }

    @Test
    fun searchCardsWithTwoColorsOnlyMulticolor() {
        val searchParams = SearchParams()
        searchParams.isWhite = true
        searchParams.isBlue = true
        searchParams.setOnlyMulti(true)
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for (card in cards) {
            assertTrue(card.isMultiColor)
            assertTrue(card.isWhite || card.isBlue)
        }
    }

    @Test
    fun searchCardsWithTwoColorsNoMulticolor() {
        val searchParams = SearchParams()
        searchParams.isWhite = true
        searchParams.isBlue = true
        searchParams.isNoMulti = true
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for (card in cards) {
            assertFalse(card.isMultiColor)
            assertTrue(card.manaCost.contains("W") && !card.manaCost.contains("U") || card.manaCost.contains("U") && !card.manaCost.contains("W"))
        }
    }

    @Test
    fun searchCommonCards() {
        val searchParams = SearchParams()
        searchParams.isCommon = true
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for (card in cards) {
            assertTrue(card.rarity == Rarity.COMMON)
        }
    }

    @Test
    fun searchUncommonCards() {
        val searchParams = SearchParams()
        searchParams.isUncommon = true
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for (card in cards) {
            assertTrue(card.rarity == Rarity.UNCOMMON)
        }
    }

    @Test
    fun searchRareCards() {
        val searchParams = SearchParams()
        searchParams.isRare = true
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for (card in cards) {
            assertTrue(card.rarity == Rarity.RARE)
        }
    }

    @Test
    fun searchMythicCards() {
        val searchParams = SearchParams()
        searchParams.isMythic = true
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for (card in cards) {
            assertTrue(card.rarity == Rarity.MYTHIC)
        }
    }

    @Test
    fun searchRareAndMythicCards() {
        val searchParams = SearchParams()
        searchParams.isRare = true
        searchParams.isMythic = true
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for (card in cards) {
            assertTrue(card.rarity == Rarity.RARE || card.rarity == Rarity.MYTHIC)
        }
    }

    @Test
    fun search_cards_by_multiple_types() {
        val searchParams = SearchParams()
        searchParams.types = "creature angel"
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for (card in cards) {
            assertTrue(card.type.toLowerCase(Locale.getDefault()).contains("creature") && card.type.toLowerCase(Locale.getDefault()).contains("angel"))
        }
        searchParams.types = "creature angel ally"
        val cards2 = underTest.searchCards(searchParams)
        assertTrue(cards2.isNotEmpty())
        for (card in cards2) {
            assertTrue(card.type.toLowerCase(Locale.getDefault()).contains("creature") && card.type.toLowerCase(Locale.getDefault()).contains("angel") && card.type.toLowerCase(Locale.getDefault()).contains("ally"))
        }
    }

    @Test
    fun search_cards_by_set_id() {
        val setDataSource = SetDataSource(mtgDatabaseHelper.readableDatabase)
        val set = setDataSource.sets[0]
        val searchParams = SearchParams()
        searchParams.setId = set.id
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for (card in cards) {
            assertThat(card.set, `is`(set))
        }
    }

    @Test
    fun search_cards_by_standard() {
        val searchParams = SearchParams()
        searchParams.setId = -2
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for (card in cards) {
            assertTrue(MTGCardDataSource.STANDARD.values().map { it.set }.contains(card.set?.name))
        }
    }

    @Test
    fun search_cards_by_name_and_types() {
        val searchParams = SearchParams()
        searchParams.name = "angel"
        searchParams.types = "creature angel"
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for (card in cards) {
            assertTrue(card.name.toLowerCase(Locale.getDefault()).contains("angel"))
            assertTrue(card.type.toLowerCase(Locale.getDefault()).contains("creature") && card.type.toLowerCase(Locale.getDefault()).contains("angel"))
        }
    }

    @Test
    fun search_cards_with_multiple_params() {
        val searchParams = SearchParams()
        searchParams.name = "angel"
        searchParams.types = "creature"
        searchParams.isWhite = true
        searchParams.isNoMulti = true
        searchParams.isRare = true
        searchParams.power = PTParam("=", 4)
        searchParams.tough = PTParam("=", 4)
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for (card in cards) {
            assertTrue(card.name.toLowerCase(Locale.getDefault()).contains("angel"))
            assertTrue(card.type.toLowerCase(Locale.getDefault()).contains("creature"))
            assertTrue(card.manaCost.contains("W"))
            assertTrue(Integer.parseInt(card.power) == 4)
            assertTrue(Integer.parseInt(card.toughness) == 4)
            assertTrue(card.rarity == Rarity.RARE)
        }
    }

    @Test
    fun MTGCardDataSource_searchCardByName() {
        val toTest = arrayOf("Wasteland", "Ulamog, the Ceaseless Hunger", "Urborg, Tomb of Yawgmoth", "Engineered Explosives")
        var card: MTGCard?
        for (name in toTest) {
            card = underTest.searchCard(name)
            assertNotNull(card)
            assertThat(card?.name, `is`(name))
        }
        card = underTest.searchCard("Obama")
        assertNull(card)
    }

    @Test
    fun searchCardsByLands() {
        val searchParams = SearchParams()
        searchParams.name = "island"
        searchParams.isLand = true
        val cards = underTest.searchCards(searchParams)
        assertNotNull(cards)
        assertTrue(cards.isNotEmpty())
        for (card in cards) {
            assertTrue(card.name.toLowerCase(Locale.getDefault()).contains("island"))
            assertTrue(card.isLand)
        }
    }

    @Test
    fun searchCardsByMultiverseId() {
        val card = underTest.searchCard(420621)
        assertNotNull(card)
        assertThat(card?.name, `is`("Selfless Squire"))
    }

    @Test
    fun searchCardsById() {
        val card = underTest.searchCardById(5)
        assertNotNull(card)
        assertThat(card?.name, `is`(BuildConfig.MTG_CARD_FIFTH_NAME))
    }

    private enum class OPERATOR constructor(private val operator: String) {
        EQUAL("=") {
            override fun assertOperator(value: Int) {
                assertThat(value, `is`(NUMBER))
            }
        },
        LESS("<") {
            override fun assertOperator(value: Int) {
                assertThat(value, lessThan(NUMBER))
            }
        },
        MORE(">") {
            override fun assertOperator(value: Int) {
                assertThat(value, greaterThan(NUMBER))
            }
        },
        EQUAL_LESS("<=") {
            override fun assertOperator(value: Int) {
                assertThat(value, lessThanOrEqualTo(NUMBER))
            }
        },
        EQUAL_MORE(">=") {
            override fun assertOperator(value: Int) {
                assertThat(value, greaterThanOrEqualTo(NUMBER))
            }
        };

        abstract fun assertOperator(value: Int)

        fun generatePTParam(): PTParam {
            return PTParam(operator, NUMBER)
        }
    }
}

const val NUMBER = 5