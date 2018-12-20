package com.dbottillo.mtgsearchfree.storage

import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.model.Color
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.Rarity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit
import java.util.Random

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

        val result = underTest.filterAndSortSet(cardFilter, cards)

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
    fun `filter cards should order cards in a set by set-number`() {
        val cardFilter = CardFilter()
        cardFilter.sortSetNumber = true
        val zyx = generateCard(name = "ZYX", number = "2").also { it.set = MTGSet(1, "AAA", "setA") }
        val abc = generateCard(name = "ABC", number = "1").also { it.set = MTGSet(1, "AAA", "setA") }
        val cards = listOf(zyx, abc)

        val result = underTest.filterAndSortSet(cardFilter, cards)

        assertThat(result[0], `is`(abc))
        assertThat(result[1], `is`(zyx))
    }

    @Test
    fun `filter cards should order cards across sets by set-number`() {
        val cardFilter = CardFilter()
        cardFilter.sortSetNumber = true
        val lmn = generateCard(name = "LMN", number = "2").also { it.set = MTGSet(1, "AAA", "setA") }
        val def = generateCard(name = "DEF", number = "1").also { it.set = MTGSet(1, "AAA", "setA") }
        val zyx = generateCard(name = "ZYX", number = "2").also { it.set = MTGSet(2, "BBB", "setB") }
        val abc = generateCard(name = "ABC", number = "1").also { it.set = MTGSet(2, "BBB", "setB") }
        val cards = listOf(lmn, def, zyx, abc)

        val result = underTest.filterAndSortMultipleSets(cardFilter, cards)

        assertThat(result[0], `is`(def))
        assertThat(result[1], `is`(lmn))
        assertThat(result[2], `is`(abc))
        assertThat(result[3], `is`(zyx))
    }

    @Test
    fun `filter cards should order cards in a set by name`() {
        val cardFilter = CardFilter()
        cardFilter.sortSetNumber = false
        val zyx = generateCard(name = "ZYX", number = "1").also { it.set = MTGSet(1, "AAA", "setA") }
        val abc = generateCard(name = "ABC", number = "2").also { it.set = MTGSet(1, "AAA", "setA") }
        val cards = listOf(zyx, abc)

        val result = underTest.filterAndSortSet(cardFilter, cards)

        assertThat(result[0], `is`(abc))
        assertThat(result[1], `is`(zyx))
    }

    @Test
    fun `filter cards should order cards across sets by name`() {
        val cardFilter = CardFilter()
        cardFilter.sortSetNumber = false
        val lmn = generateCard(name = "LMN", number = "2").also { it.set = MTGSet(1, "AAA", "setA") }
        val def = generateCard(name = "DEF", number = "1").also { it.set = MTGSet(1, "AAA", "setA") }
        val zyx = generateCard(name = "ZYX", number = "2").also { it.set = MTGSet(2, "BBB", "setB") }
        val abc = generateCard(name = "ABC", number = "1").also { it.set = MTGSet(2, "BBB", "setB") }
        val cards = listOf(lmn, def, zyx, abc)

        val result = underTest.filterAndSortMultipleSets(cardFilter, cards)

        assertThat(result[0], `is`(abc))
        assertThat(result[1], `is`(def))
        assertThat(result[2], `is`(lmn))
        assertThat(result[3], `is`(zyx))
    }

    private fun checkCondition(prepareFilter: (CardFilter) -> Unit, validateCards: (MTGCard) -> Unit) {
        val cardFilter = CardFilter()
        prepareFilter(cardFilter)
        val cards = generateCards()
        val result = underTest.filterAndSortSet(cardFilter, cards)

        result.forEach {
            validateCards(it)
        }
    }

    private fun generateCards(): List<MTGCard> {
        val list = mutableListOf<MTGCard>()
        val colors = listOf("W", "U", "B", "R", "G")
        val colorsIdentity = listOf(Color.WHITE, Color.BLUE, Color.BLACK, Color.RED, Color.GREEN)
        val rarity = listOf(Rarity.COMMON, Rarity.UNCOMMON, Rarity.RARE, Rarity.MYTHIC)
        var number = 0
        colors.forEachIndexed { index, color ->
            rarity.forEach { rarity ->
                list.add(generateCard(cost = color, rarity = rarity,
                        number = number.toString(), colors = listOf(color),
                        colorsIdentity = listOf(colorsIdentity[index])))
                number++
            }
        }
        val artifactCard = MTGCard()
        artifactCard.isArtifact = true
        artifactCard.setCardName("Card $number")
        artifactCard.rarity = Rarity.COMMON
        artifactCard.number = number.toString()
        number++
        list.add(artifactCard)
        val landCard = MTGCard()
        landCard.isLand = true
        landCard.setCardName("Card $number")
        landCard.rarity = Rarity.UNCOMMON
        landCard.number = number.toString()
        number++
        list.add(landCard)
        val eldraziCard = MTGCard()
        eldraziCard.setCardName("Card $number")
        eldraziCard.type = "Eldrazi"
        eldraziCard.rarity = Rarity.RARE
        eldraziCard.number = number.toString()
        list.add(eldraziCard)
        return list
    }

    private fun generateCard(
        name: String = "Card",
        cost: String = "WU",
        number: String,
        rarity: Rarity = Rarity.COMMON,
        colors: List<String> = listOf(),
        colorsIdentity: List<Color> = listOf(),
        isLand: Boolean = false,
        isArtifact: Boolean = false,
        isMulticolor: Boolean = false
    ): MTGCard {
        val card = MTGCard()
        card.uuid = Random().nextInt().toString()
        card.setCardName("$name $number")
        card.manaCost = cost
        card.rarity = rarity
        card.colorsDisplay = colors
        card.colorsIdentity = colorsIdentity
        card.isLand = isLand
        card.isArtifact = isArtifact
        card.isMultiColor = isMulticolor
        card.number = number
        return card
    }
}