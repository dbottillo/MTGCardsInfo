package com.dbottillo.mtgsearchfree.database

import android.content.res.Resources
import com.dbottillo.mtgsearchfree.model.CMCParam
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.PTParam
import com.dbottillo.mtgsearchfree.model.Rarity
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.model.Side
import com.dbottillo.mtgsearchfree.storage.SetDataSource
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.readSetListJSON
import com.dbottillo.mtgsearchfree.util.readSingleSetFile
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.google.gson.Gson
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class MTGCardDataSourceTest {

    private lateinit var mtgDatabaseHelper: MTGDatabaseHelper
    private lateinit var cardsInfoDbHelper: CardsInfoDbHelper
    private lateinit var underTest: MTGCardDataSource

    @Before
    fun setup() {
        mtgDatabaseHelper = MTGDatabaseHelper(RuntimeEnvironment.application)
        cardsInfoDbHelper = CardsInfoDbHelper(RuntimeEnvironment.application)
        val cardDataSource = CardDataSource(cardsInfoDbHelper.writableDatabase, Gson())
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
            try {
                val cardsJ = readSingleSetFile(set).filter { it.side == Side.A || it.layout == "meld" }
                val cards = underTest.getSet(set)
                /*
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
                assertThat(cardsJ.size).isEqualTo(cards.size)
                assertThat(cards).containsExactlyElementsIn(cardsJ)
            } catch (e: Resources.NotFoundException) {
                LOG.e(set.code + " file not found")
            }
        }
    }

    @Test
    fun getsRandomCards() {
        val cards = underTest.getRandomCard(10)
        assertThat(cards.size).isEqualTo(10)
    }

    @Test
    fun searchCardsByName() {
        val searchParams = SearchParams()
        searchParams.name = "Dragon"
        val cards = underTest.searchCards(searchParams)
        for (card in cards) {
            assertThat(card.name.toLowerCase(Locale.getDefault()).contains("dragon"))
        }
    }

    @Test
    fun searchCardsByType() {
        val searchParams = SearchParams()
        searchParams.types = "creature"
        val cards = underTest.searchCards(searchParams)
        assertThat(cards.isNotEmpty()).isTrue()
        for (card in cards) {
            assertThat(card.type.toLowerCase(Locale.getDefault()).contains("creature"))
        }
    }

    @Test
    fun searchCardsByText() {
        val searchParams = SearchParams()
        searchParams.text = "lifelink"
        val cards = underTest.searchCards(searchParams)
        assertThat(cards.isNotEmpty()).isTrue()
        for (card in cards) {
            assertThat(card.text.toLowerCase(Locale.getDefault()).contains("lifelink"))
        }
    }

    @Test
    fun shouldSearchCardsByCmcEqualThan5() {
        val searchParams = SearchParams()
        searchParams.cmc = CMCParam("=", 5, listOf("5"))

        val cards = underTest.searchCards(searchParams)

        for (card in cards) {
            assertThat(card.cmc).isEqualTo(5)
        }
    }

    @Test
    fun shouldSearchCardsByCmcLessThan5() {
        val searchParams = SearchParams()
        searchParams.cmc = CMCParam("<", 5, listOf("5"))

        val cards = underTest.searchCards(searchParams)

        for (card in cards) {
            assertThat(card.cmc).isLessThan(5)
        }
    }

    @Test
    fun shouldSearchCardsByCmcEqualThan2WU() {
        val searchParams = SearchParams()
        searchParams.cmc = CMCParam("=", 4, listOf("2", "W", "U"))

        val cards = underTest.searchCards(searchParams)

        for (card in cards.filter { it.layout == "normal" }) {
            assertThat(card.cmc).isEqualTo(4)
            assertThat(card.manaCost).isEqualTo("{2}{W}{U}")
        }
    }

    @Test
    fun shouldSearchCardsByCmcGreaterOREqualThan2WWU() {
        val searchParams = SearchParams()
        searchParams.cmc = CMCParam(">=", 5, listOf("2", "WW", "U"))

        val cards = underTest.searchCards(searchParams)

        for (card in cards) {
            assertThat(card.cmc).isGreaterThan(4)
            assertThat(card.manaCost).contains("{W}{W}")
            assertThat(card.manaCost).contains("{U}")
        }
    }

    @Test
    fun shouldSearchCardsByCmcEqualThanX2U() {
        val searchParams = SearchParams()
        searchParams.cmc = CMCParam("=", 3, listOf("X", "2", "U"))

        val cards = underTest.searchCards(searchParams)

        for (card in cards) {
            assertThat(card.cmc).isEqualTo(3)
            assertThat(card.manaCost).isEqualTo("{X}{2}{U}")
        }
    }

    @Test
    fun shouldSearchCardsByCmcGreaterOREqualThanX2U() {
        val searchParams = SearchParams()
        searchParams.cmc = CMCParam(">=", 3, listOf("X", "2", "U"))

        val cards = underTest.searchCards(searchParams)

        for (card in cards) {
            assertThat(card.cmc).isAtLeast(3)
            assertThat(card.manaCost).contains("{X}")
            assertThat(card.manaCost).contains("{2}")
            assertThat(card.manaCost).contains("{U}")
        }
    }

    @Test
    fun searchCardsByPower() {
        val searchParams = SearchParams()
        for (element in OPERATOR.values()) {
            searchParams.power = element.generatePTParam()
            val cards = underTest.searchCards(searchParams)
            assertThat(cards).isNotEmpty()
            for (card in cards) {
                element.assertOperator(Integer.parseInt(card.power))
            }
        }
        searchParams.power = PTParam("", -1)
        var cards = underTest.searchCards(searchParams)
        for (card in cards) {
            assertThat(card.power).contains("*")
        }
        searchParams.power = PTParam("=", 0)
        cards = underTest.searchCards(searchParams)
        for (card in cards) {
            assertThat(card.power).isAnyOf("0", "+0")
        }
    }

    @Test
    fun `should search with power and toughness greater than 10`() {
        val searchParams = SearchParams()
        searchParams.power = PTParam(">=", 10)
        searchParams.tough = PTParam(">=", 10)

        val cards = underTest.searchCards(searchParams)

        assertThat(cards).isNotEmpty()
        for (card in cards) {
            assertThat(card.power.toInt()).isAtLeast(10)
            assertThat(card.toughness.toInt()).isAtLeast(10)
        }
    }

    @Test
    fun searchCardsByToughness() {
        val searchParams = SearchParams()
        for (element in OPERATOR.values()) {
            val operator = element
            searchParams.tough = operator.generatePTParam()
            val cards = underTest.searchCards(searchParams)
            assertThat(cards).isNotEmpty()
            cards.forEach {
                operator.assertOperator(it.toughness.toInt())
            }
        }
        searchParams.tough = PTParam("", -1)
        var cards = underTest.searchCards(searchParams)
        for (card in cards) {
            assertThat(card.toughness).contains("*")
        }
        searchParams.tough = PTParam(">=", 2)
        cards = underTest.searchCards(searchParams)
        for (card in cards) {
            assertThat(Integer.parseInt(card.toughness) >= 2).isTrue()
        }
    }

    @Test
    fun searchCardsByColor() {
        var searchParams = SearchParams()
        searchParams.isWhite = true
        var cards = underTest.searchCards(searchParams)
        assertThat(cards).isNotEmpty()
        for (card in cards) {
            assertThat(card.isWhite).isTrue()
        }
        searchParams = SearchParams()
        searchParams.isBlue = true
        cards = underTest.searchCards(searchParams)
        assertThat(cards).isNotEmpty()
        for (card in cards) {
            assertThat(card.isBlue).isTrue()
        }
        searchParams = SearchParams()
        searchParams.isRed = true
        cards = underTest.searchCards(searchParams)
        assertThat(cards).isNotEmpty()
        for (card in cards) {
            assertThat(card.isRed).isTrue()
        }
        searchParams = SearchParams()
        searchParams.isBlack = true
        cards = underTest.searchCards(searchParams)
        assertThat(cards).isNotEmpty()
        for (card in cards) {
            assertThat(card.isBlack).isTrue()
        }
        searchParams = SearchParams()
        searchParams.isGreen = true
        cards = underTest.searchCards(searchParams)
        assertThat(cards).isNotEmpty()
        for (card in cards) {
            assertThat(card.isGreen).isTrue()
        }
    }

    @Test
    fun `should search multi color cards without color filters`() {
        val searchParams = SearchParams()
        searchParams.name = "Render Silent"

        val cards = underTest.searchCards(searchParams)

        assertThat(cards[0].name).isEqualTo("Render Silent")
    }

    @Test
    fun `should search exactly blu and red colors in standard`() {
        val searchParams = SearchParams()
        searchParams.isRed = true
        searchParams.isBlue = true
        searchParams.setId = -2
        searchParams.exactlyColors = true
        searchParams.includingColors = false
        searchParams.atMostColors = false

        val cards = underTest.searchCards(searchParams)

        for (card in cards) {
            assertThat(card.isRed && card.isBlue && !card.isWhite &&
                    !card.isBlack && !card.isGreen).isTrue()
        }
    }

    @Test
    fun `should search exactly blue, green and red colors in standard`() {
        val searchParams = SearchParams()
        searchParams.isRed = true
        searchParams.isBlue = true
        searchParams.isGreen = true
        searchParams.setId = -2
        searchParams.exactlyColors = true
        searchParams.includingColors = false
        searchParams.atMostColors = false

        val cards = underTest.searchCards(searchParams)

        for (card in cards) {
            assertThat(card.isRed && card.isBlue && !card.isWhite &&
                    !card.isBlack && card.isGreen).isTrue()
        }
    }

    @Test
    fun `should search by colors including blu and red in standard`() {
        val searchParams = SearchParams()
        searchParams.isRed = true
        searchParams.isBlue = true
        searchParams.exactlyColors = false
        searchParams.includingColors = true
        searchParams.atMostColors = false
        searchParams.setId = -2

        val cards = underTest.searchCards(searchParams)

        for (card in cards) {
            assertThat(card.isRed && card.isBlue).isTrue()
        }
    }

    @Test
    fun `should search by colors at most blu and red in standard`() {
        val searchParams = SearchParams()
        searchParams.isRed = true
        searchParams.isBlue = true
        searchParams.exactlyColors = false
        searchParams.includingColors = false
        searchParams.atMostColors = true
        searchParams.setId = -2

        val cards = underTest.searchCards(searchParams)

        for (card in cards) {
            assertThat(card.isRed || card.isBlue).isTrue()
        }
    }

    @Test
    fun `should search by white, green and elf`() {
        val searchParams = SearchParams()
        searchParams.isWhite = true
        searchParams.isGreen = true
        searchParams.exactlyColors = true
        searchParams.types = "elf"

        val cards = underTest.searchCards(searchParams)

        assertThat(cards).isNotEmpty()
        for (card in cards) {
            assertWithMessage(card.toString()).that((card.isWhite || card.isGreen) && card.type.contains("elf", ignoreCase = true)).isTrue()
        }
    }

    @Test
    fun searchCommonCards() {
        val searchParams = SearchParams()
        searchParams.isCommon = true
        val cards = underTest.searchCards(searchParams)
        assertThat(cards).isNotEmpty()
        for (card in cards) {
            assertThat(card.rarity == Rarity.COMMON).isTrue()
        }
    }

    @Test
    fun searchUncommonCards() {
        val searchParams = SearchParams()
        searchParams.isUncommon = true
        val cards = underTest.searchCards(searchParams)
        assertThat(cards).isNotEmpty()
        for (card in cards) {
            assertThat(card.rarity == Rarity.UNCOMMON).isTrue()
        }
    }

    @Test
    fun searchRareCards() {
        val searchParams = SearchParams()
        searchParams.isRare = true
        val cards = underTest.searchCards(searchParams)
        assertThat(cards).isNotEmpty()
        for (card in cards) {
            assertThat(card.rarity == Rarity.RARE).isTrue()
        }
    }

    @Test
    fun searchMythicCards() {
        val searchParams = SearchParams()
        searchParams.isMythic = true
        val cards = underTest.searchCards(searchParams)
        assertThat(cards).isNotEmpty()
        for (card in cards) {
            assertThat(card.rarity == Rarity.MYTHIC).isTrue()
        }
    }

    @Test
    fun searchRareAndMythicCards() {
        val searchParams = SearchParams()
        searchParams.isRare = true
        searchParams.isMythic = true
        val cards = underTest.searchCards(searchParams)
        assertThat(cards).isNotEmpty()
        for (card in cards) {
            assertThat(card.rarity == Rarity.RARE || card.rarity == Rarity.MYTHIC).isTrue()
        }
    }

    @Test
    fun search_cards_by_multiple_types() {
        val searchParams = SearchParams()
        searchParams.types = "creature angel"
        val cards = underTest.searchCards(searchParams)
        assertThat(cards).isNotEmpty()
        for (card in cards) {
            assertThat(card.type.toLowerCase(Locale.getDefault()).contains("creature") && card.type.toLowerCase(Locale.getDefault()).contains("angel")).isTrue()
        }
        searchParams.types = "creature angel ally"
        val cards2 = underTest.searchCards(searchParams)
        assertThat(cards).isNotEmpty()
        for (card in cards2) {
            assertThat(card.type.toLowerCase(Locale.getDefault()).contains("creature") && card.type.toLowerCase(Locale.getDefault()).contains("angel") && card.type.toLowerCase(Locale.getDefault()).contains("ally")).isTrue()
        }
    }

    @Test
    fun `should search cards by set id`() {
        val setDataSource = SetDataSource(mtgDatabaseHelper.readableDatabase)
        val set = setDataSource.sets[0]
        val searchParams = SearchParams()
        searchParams.setId = set.id
        val cards = underTest.searchCards(searchParams)
        assertThat(cards).isNotEmpty()
        for (card in cards) {
            assertThat(card.set?.id).isEqualTo(set.id)
            assertThat(card.set?.code).isEqualTo(set.code)
        }
    }

    @Test
    fun search_cards_by_standard() {
        val searchParams = SearchParams()
        searchParams.setId = -2
        val cards = underTest.searchCards(searchParams)
        assertThat(cards).isNotEmpty()
        for (card in cards) {
            assertThat(MTGCardDataSource.STANDARD.values().map { it.set }.contains(card.set?.name)).isTrue()
        }
    }

    @Test
    fun search_cards_by_name_and_types() {
        val searchParams = SearchParams()
        searchParams.name = "angel"
        searchParams.types = "creature angel"
        val cards = underTest.searchCards(searchParams)
        assertThat(cards).isNotEmpty()
        for (card in cards) {
            assertThat(card.name.toLowerCase(Locale.getDefault()).contains("angel")).isTrue()
            assertThat(card.type.toLowerCase(Locale.getDefault()).contains("creature") && card.type.toLowerCase(Locale.getDefault()).contains("angel")).isTrue()
        }
    }

    @Test
    fun `search cards with multiple params`() {
        val searchParams = SearchParams()
        searchParams.name = "angel"
        searchParams.types = "creature"
        searchParams.isWhite = true
        searchParams.isRare = true
        searchParams.power = PTParam("=", 4)
        searchParams.tough = PTParam("=", 4)

        val cards = underTest.searchCards(searchParams)

        assertThat(cards).isNotEmpty()
        for (card in cards) {
            assertThat(card.name.toLowerCase(Locale.getDefault()).contains("angel")).isTrue()
            assertThat(card.type.toLowerCase(Locale.getDefault()).contains("creature")).isTrue()
            assertThat(card.isWhite).isTrue()
            assertThat(card.power.toInt()).isEqualTo(4)
            assertThat(card.toughness.toInt()).isEqualTo(4)
            assertThat(card.rarity == Rarity.RARE).isTrue()
        }
    }

    @Test
    fun MTGCardDataSource_searchCardByName() {
        val toTest = arrayOf("Wasteland", "Ulamog, the Ceaseless Hunger", "Urborg, Tomb of Yawgmoth", "Engineered Explosives")
        var card: MTGCard?
        for (name in toTest) {
            card = underTest.searchCard(name)
            assertThat(card).isNotNull()
            assertThat(card?.name).isEqualTo(name)
        }
        card = underTest.searchCard("Obama")
        assertThat(card).isNull()
    }

    @Test
    fun searchCardsByLands() {
        val searchParams = SearchParams()
        searchParams.name = "island"
        searchParams.isLand = true
        val cards = underTest.searchCards(searchParams)
        assertThat(cards).isNotNull()
        assertThat(cards).isNotEmpty()
        for (card in cards) {
            assertThat(card.name.toLowerCase(Locale.getDefault()).contains("island")).isTrue()
            assertThat(card.isLand).isTrue()
        }
    }

    @Test
    fun searchCardsByMultiverseId() {
        val card = underTest.searchCard(420621)
        assertThat(card).isNotNull()
        assertThat(card?.name).isEqualTo("Selfless Squire")
    }

    @Test
    fun `should search cards by id`() {
        val card = underTest.searchCardById(5)
        assertThat(card).isNotNull()
        assertThat(card?.name).isEqualTo("Allure of the Unknown")
    }

    @Test
    fun `should search lands without land flag on`() {
        val searchParams = SearchParams()
        searchParams.name = "Blood Crypt"

        val cards = underTest.searchCards(searchParams)

        assertThat(cards.size).isEqualTo(4)
        cards.forEach { card ->
            assertThat(card.name).isEqualTo("Blood Crypt")
        }
    }

    @Test
    fun `should search with no duplicates flag on`() {
        val searchParams = SearchParams()
        searchParams.name = "serra angel"
        searchParams.duplicates = false

        val cards = underTest.searchCards(searchParams)

        assertThat(cards.size).isEqualTo(1)
    }

    @Test
    fun `should search colorless cards`() {
        val searchParams = SearchParams()
        searchParams.colorless = true

        val cards = underTest.searchCards(searchParams)

        cards.forEach { card ->
            assertWithMessage(card.name).that(card.colorsDisplay).isEmpty()
        }
    }

    private enum class OPERATOR constructor(private val operator: String) {
        EQUAL("=") {
            override fun assertOperator(value: Int) {
                assertThat(value).isEqualTo(NUMBER)
            }
        },
        LESS("<") {
            override fun assertOperator(value: Int) {
                assertThat(value).isLessThan(NUMBER)
            }
        },
        MORE(">") {
            override fun assertOperator(value: Int) {
                assertThat(value).isGreaterThan(NUMBER)
            }
        },
        EQUAL_LESS("<=") {
            override fun assertOperator(value: Int) {
                assertThat(value).isAtMost(NUMBER)
            }
        },
        EQUAL_MORE(">=") {
            override fun assertOperator(value: Int) {
                assertThat(value).isAtLeast(NUMBER)
            }
        };

        abstract fun assertOperator(value: Int)

        fun generatePTParam(): PTParam {
            return PTParam(operator, NUMBER)
        }
    }
}

const val NUMBER = 5