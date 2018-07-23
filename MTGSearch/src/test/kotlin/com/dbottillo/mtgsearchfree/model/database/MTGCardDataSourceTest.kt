package com.dbottillo.mtgsearchfree.model.database

import android.content.res.Resources
import com.dbottillo.mtgsearchfree.BuildConfig
import com.dbottillo.mtgsearchfree.model.*
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.readSetListJSON
import com.dbottillo.mtgsearchfree.util.readSingleSetFile
import com.google.gson.Gson
import org.hamcrest.Matchers.*
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsCollectionContaining.hasItem
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.util.*

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
        //MTGSet set = setsJ.get(180);
        for (set in setsJ) {
            //LOG.e("checking set: " + set.getId() + " - " + set.getName());
            try {
                val cardsJ = readSingleSetFile(set)
                val cards = underTest.getSet(set)
                /*if (set.getId() == 180){
                    LOG.e("checking set: " + cardsJ.size() + " - " + cards.size());
                    for (MTGCard cardJ : cardsJ){
                        LOG.e("card "+cardJ.toString());
                    }
                    for (MTGCard card : cards){
                        LOG.e("card2 "+card.toString());
                    }
                    for (MTGCard cardJ : cardsJ){
                        LOG.e("checking card: " + cardJ.toString());
                        boolean found = false;
                        for (MTGCard card : cards){
                            if (cardJ.equals(card)){
                                found = true;
                            }
                        }
                        if (!found){
                            LOG.e("not found "+cardJ);
                        }
                    }
                    if (cardsJ.get(0).equals(cards.get(0))){
                        LOG.e("found ");
                    } else {
                        LOG.e("not found ");
                    }
                }*/
                assertThat(cardsJ.size, `is`(cards.size))
                assertTrue(cards.containsAll(cardsJ))
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
        for ((_, name) in cards) {
            assertTrue(name.toLowerCase(Locale.getDefault()).contains("dragon"))
        }
    }

    @Test
    fun searchCardsByType() {
        val searchParams = SearchParams()
        searchParams.types = "creature"
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for ((_, _, type) in cards) {
            assertTrue(type.toLowerCase(Locale.getDefault()).contains("creature"))
        }
    }

    @Test
    fun searchCardsByText() {
        val searchParams = SearchParams()
        searchParams.text = "lifelink"
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for ((_, _, _, _, _, _, _, _, _, _, _, text) in cards) {
            assertTrue(text.toLowerCase(Locale.getDefault()).contains("lifelink"))
        }
    }

    @Test
    fun shouldSearchCardsByCmcEqualThan5() {
        val searchParams = SearchParams()
        searchParams.cmc = CMCParam("=", 5, listOf("5"))

        val cards = underTest.searchCards(searchParams)

        for ((_, _, _, _, _, _, cmc) in cards) {
            assertThat(cmc, `is`(5))
        }
    }

    @Test
    fun shouldSearchCardsByCmcLessThan5() {
        val searchParams = SearchParams()
        searchParams.cmc = CMCParam("<", 5, listOf("5"))

        val cards = underTest.searchCards(searchParams)

        for ((_, _, _, _, _, _, cmc) in cards) {
            assertThat(cmc, lessThan(5))
        }
    }

    @Test
    fun shouldSearchCardsByCmcEqualThan2WU() {
        val searchParams = SearchParams()
        searchParams.cmc = CMCParam("=", 4, Arrays.asList("2", "W", "U"))

        val cards = underTest.searchCards(searchParams)

        for ((_, _, _, _, _, _, cmc, _, _, _, manaCost) in cards) {
            assertThat(cmc, `is`(4))
            assertThat(manaCost, `is`("{2}{W}{U}"))
        }
    }

    @Test
    fun shouldSearchCardsByCmcGreaterOREqualThan2WWU() {
        val searchParams = SearchParams()
        searchParams.cmc = CMCParam(">=", 5, Arrays.asList("2", "WW", "U"))

        val cards = underTest.searchCards(searchParams)

        for ((_, _, _, _, _, _, cmc, _, _, _, manaCost) in cards) {
            assertThat(cmc, greaterThanOrEqualTo(5))
            assertThat(manaCost, containsString("{W}{W}"))
            assertThat(manaCost, containsString("{U}"))
        }
    }

    @Test
    fun shouldSearchCardsByCmcEqualThanX2U() {
        val searchParams = SearchParams()
        searchParams.cmc = CMCParam("=", 3, Arrays.asList("X", "2", "U"))

        val cards = underTest.searchCards(searchParams)

        for ((_, _, _, _, _, _, cmc, _, _, _, manaCost) in cards) {
            assertThat(cmc, `is`(3))
            assertThat(manaCost, `is`("{X}{2}{U}"))
        }
    }

    @Test
    fun shouldSearchCardsByCmcGreaterOREqualThanX2U() {
        val searchParams = SearchParams()
        searchParams.cmc = CMCParam(">=", 3, Arrays.asList("X", "2", "U"))

        val cards = underTest.searchCards(searchParams)

        for ((_, _, _, _, _, _, cmc, _, _, _, manaCost) in cards) {
            assertThat(cmc, greaterThanOrEqualTo(3))
            assertThat(manaCost, containsString("{X}"))
            assertThat(manaCost, containsString("{2}"))
            assertThat(manaCost, containsString("{U}"))
        }
    }

    @Test
    fun searchCardsByPower() {
        val searchParams = SearchParams()
        for (i in 0 until OPERATOR.values().size) {
            val operator = OPERATOR.values()[i]
            searchParams.power = operator.generatePTParam()
            val cards = underTest.searchCards(searchParams)
            assertTrue(cards.size > 0)
            for ((_, _, _, _, _, _, _, _, power) in cards) {
                operator.assertOperator(Integer.parseInt(power))
            }
        }
        searchParams.power = PTParam("", -1)
        var cards = underTest.searchCards(searchParams)
        for ((_, _, _, _, _, _, _, _, power) in cards) {
            assertThat(power, containsString("*"))
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
        for ((_, _, _, _, _, _, _, _, _, toughness) in cards) {
            assertThat(toughness, containsString("*"))
        }
        searchParams.tough = PTParam(">=", 2)
        cards = underTest.searchCards(searchParams)
        for ((_, _, _, _, _, _, _, _, _, toughness) in cards) {
            assertTrue(Integer.parseInt(toughness) >= 2)
        }
    }

    @Test
    fun searchCardsByColor() {
        var searchParams = SearchParams()
        searchParams.isWhite = true
        var cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for ((_, _, _, _, _, _, _, _, _, _, manaCost) in cards) {
            assertThat(manaCost, containsString("W"))
        }
        searchParams = SearchParams()
        searchParams.isBlue = true
        cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for ((_, _, _, _, _, _, _, _, _, _, manaCost) in cards) {
            assertThat(manaCost, containsString("U"))
        }
        searchParams = SearchParams()
        searchParams.isRed = true
        cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for ((_, _, _, _, _, _, _, _, _, _, manaCost) in cards) {
            assertThat(manaCost, containsString("R"))
        }
        searchParams = SearchParams()
        searchParams.isBlack = true
        cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for ((_, _, _, _, _, _, _, _, _, _, manaCost) in cards) {
            assertThat(manaCost, containsString("B"))
        }
        searchParams = SearchParams()
        searchParams.isGreen = true
        cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for ((_, _, _, _, _, _, _, _, _, _, manaCost) in cards) {
            assertThat(manaCost, containsString("G"))
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
            //assertTrue(containsString("W") || containsString("U"));
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
        assertTrue(cards.size > 0)
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
        for ((_, _, _, _, _, _, _, _, _, _, manaCost, _, isMultiColor) in cards) {
            assertFalse(isMultiColor)
            assertTrue(manaCost.contains("W") && !manaCost.contains("U") || manaCost.contains("U") && !manaCost.contains("W"))
        }
    }

    @Test
    fun searchCommonCards() {
        val searchParams = SearchParams()
        searchParams.isCommon = true
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.size > 0)
        for ((_, _, _, _, _, _, _, rarity) in cards) {
            assertTrue(rarity.equals("Common", ignoreCase = true))
        }
    }

    @Test
    fun searchUncommonCards() {
        val searchParams = SearchParams()
        searchParams.isUncommon = true
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.size > 0)
        for ((_, _, _, _, _, _, _, rarity) in cards) {
            assertTrue(rarity.equals("Uncommon", ignoreCase = true))
        }
    }

    @Test
    fun searchRareCards() {
        val searchParams = SearchParams()
        searchParams.isRare = true
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.size > 0)
        for ((_, _, _, _, _, _, _, rarity) in cards) {
            assertTrue(rarity.equals("Rare", ignoreCase = true))
        }
    }

    @Test
    fun searchMythicCards() {
        val searchParams = SearchParams()
        searchParams.isMythic = true
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.size > 0)
        for ((_, _, _, _, _, _, _, rarity) in cards) {
            assertTrue(rarity.equals("Mythic Rare", ignoreCase = true))
        }
    }

    @Test
    fun searchRareAndMythicCards() {
        val searchParams = SearchParams()
        searchParams.isRare = true
        searchParams.isMythic = true
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.size > 0)
        for ((_, _, _, _, _, _, _, rarity) in cards) {
            assertTrue(rarity.equals("Rare", ignoreCase = true) || rarity.equals("Mythic Rare", ignoreCase = true))
        }
    }

    @Test
    fun search_cards_by_multiple_types() {
        val searchParams = SearchParams()
        searchParams.types = "creature angel"
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.size > 0)
        for ((_, _, type) in cards) {
            assertTrue(type.toLowerCase(Locale.getDefault()).contains("creature") && type.toLowerCase(Locale.getDefault()).contains("angel"))
        }
        searchParams.types = "creature angel ally"
        val cards2 = underTest.searchCards(searchParams)
        assertTrue(cards2.size > 0)
        for ((_, _, type) in cards2) {
            assertTrue(type.toLowerCase(Locale.getDefault()).contains("creature") && type.toLowerCase(Locale.getDefault()).contains("angel") && type.toLowerCase(Locale.getDefault()).contains("ally"))
        }
    }

    @Test
    fun search_cards_by_set_id() {
        val setDataSource = SetDataSource(mtgDatabaseHelper.readableDatabase)
        val set = setDataSource.sets[0]
        val searchParams = SearchParams()
        searchParams.setId = set.id
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.size > 0)
        for ((_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, set1) in cards) {
            assertThat<MTGSet>(set1, `is`(set))
        }
    }

    @Test
    fun search_cards_by_standard() {
        val searchParams = SearchParams()
        searchParams.setId = -2
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.isNotEmpty())
        for ((_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, set) in cards) {
            assertTrue(MTGCardDataSource.STANDARD.values().map { it.set }.contains(set?.name))
        }
    }

    @Test
    fun search_cards_by_name_and_types() {
        val searchParams = SearchParams()
        searchParams.name = "angel"
        searchParams.types = "creature angel"
        val cards = underTest.searchCards(searchParams)
        assertTrue(cards.size > 0)
        for ((_, name, type) in cards) {
            assertTrue(name.toLowerCase(Locale.getDefault()).contains("angel"))
            assertTrue(type.toLowerCase(Locale.getDefault()).contains("creature") && type.toLowerCase(Locale.getDefault()).contains("angel"))
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
        assertTrue(cards.size > 0)
        for ((_, name, type, _, _, _, _, rarity, power, toughness, manaCost) in cards) {
            assertTrue(name.toLowerCase(Locale.getDefault()).contains("angel"))
            assertTrue(type.toLowerCase(Locale.getDefault()).contains("creature"))
            assertTrue(manaCost.contains("W"))
            assertTrue(Integer.parseInt(power) == 4)
            assertTrue(Integer.parseInt(toughness) == 4)
            assertTrue(rarity.equals("Rare", ignoreCase = true))
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
        for ((_, name, _, _, _, _, _, _, _, _, _, _, _, isLand) in cards) {
            assertTrue(name.toLowerCase(Locale.getDefault()).contains("island"))
            assertTrue(isLand)
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