package com.dbottillo.mtgsearchfree.storage

import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.database.FavouritesDataSource
import com.dbottillo.mtgsearchfree.database.MTGCardDataSource
import com.dbottillo.mtgsearchfree.util.Logger
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

class CardsStorageImplTest {

    @Rule @JvmField var mockitoRule = MockitoJUnit.rule()!!

    @Mock private lateinit var card: MTGCard
    @Mock private lateinit var set: MTGSet
    @Mock private lateinit var mainSideCard: MTGCard
    @Mock private lateinit var secondSideCard: MTGCard
    @Mock lateinit var filter: CardFilter
    @Mock private lateinit var logger: Logger
    @Mock internal lateinit var mtgCardDataSource: MTGCardDataSource
    @Mock internal lateinit var favouritesDataSource: FavouritesDataSource
    @Mock lateinit var cardsPreferences: CardsPreferences
    @Mock lateinit var cardsHelper: CardsHelper
    @Mock lateinit var searchParams: SearchParams

    private val setCards = listOf(MTGCard(5, 1), MTGCard(6, 2))
    private val setCardsFiltered = listOf(MTGCard(15, 3), MTGCard(16, 4))
    private val luckyCards = listOf(MTGCard(8, 5), MTGCard(9, 6))
    private val searchCards = listOf(MTGCard(12, 7), MTGCard(13, 8))
    private val searchCardsFiltered = listOf(MTGCard(121, 9), MTGCard(131, 10))
    private lateinit var favCards: List<MTGCard>
    private lateinit var underTest: CardsStorageImpl

    @Before
    fun setupStorage() {
        val fav1 = MTGCard(7, 11)
        fav1.multiVerseId = 100
        val fav2 = MTGCard(8, 12)
        fav1.multiVerseId = 101
        favCards = listOf(fav1, fav2)
        whenever(mtgCardDataSource.getSet(set)).thenReturn(setCards)
        whenever(favouritesDataSource.getCards(ArgumentMatchers.anyBoolean())).thenReturn(favCards)
        whenever(mtgCardDataSource.getRandomCard(2)).thenReturn(luckyCards)
        whenever(mtgCardDataSource.searchCards(searchParams)).thenReturn(searchCards)
        whenever(mainSideCard.name).thenReturn("One")
        whenever(secondSideCard.name).thenReturn("Two")
        whenever(mtgCardDataSource.searchCard("Two")).thenReturn(secondSideCard)
        whenever(mtgCardDataSource.searchCard("One")).thenReturn(mainSideCard)
        whenever(mainSideCard.names).thenReturn(listOf("One", "Two"))
        whenever(secondSideCard.names).thenReturn(listOf("One", "Two"))
        whenever(cardsPreferences.load()).thenReturn(filter)
        underTest = CardsStorageImpl(mtgCardDataSource, favouritesDataSource, cardsPreferences, cardsHelper, logger)
    }

    @Test
    fun testLoad() {
        whenever(cardsHelper.filterAndSortSet(filter, setCards)).thenReturn(setCardsFiltered)

        val cards = underTest.load(set)

        assertThat(cards.list, `is`(setCardsFiltered))
        assertThat(cards.filter, `is`(filter))
        assertFalse(cards.isDeck)
    }

    @Test
    fun testSaveAsFavourite() {
        underTest.saveAsFavourite(card)

        verify(favouritesDataSource).saveFavourites(card)
        verifyNoMoreInteractions(favouritesDataSource, mtgCardDataSource, cardsPreferences, cardsHelper)
    }

    @Test
    fun testRemoveFromFavourite() {
        underTest.removeFromFavourite(card)

        verify(favouritesDataSource).removeFavourites(card)
        verifyNoMoreInteractions(favouritesDataSource, mtgCardDataSource, cardsPreferences, cardsHelper)
    }

    @Test
    fun testLoadIdFav() {
        val favs = underTest.loadIdFav()
        verify(favouritesDataSource).getCards(false)
        assertThat(favs.size, `is`(2))
        assertThat(favs[0], `is`(favCards[0].multiVerseId))
        assertThat(favs[1], `is`(favCards[1].multiVerseId))
    }

    @Test
    fun testGetLuckyCards() {
        val lucky = underTest.getLuckyCards(2)

        verify(mtgCardDataSource).getRandomCard(2)
        assertThat(lucky.list.size, `is`(2))
        assertThat(lucky.list, `is`(luckyCards))
    }

    @Test
    fun testGetFavourites() {
        val favs = underTest.getFavourites()
        verify(favouritesDataSource).getCards(true)
        assertNotNull(favs)
        assertThat(favs, `is`<List<MTGCard>>(favCards))
    }

    @Test
    fun `should search cards and return them sorted`() {
        whenever(cardsHelper.filterAndSortMultipleSets(filter, searchCards)).thenReturn(searchCardsFiltered)

        val cards = underTest.doSearch(searchParams)

        assertThat(cards.list, `is`(searchCardsFiltered))
        assertNull(cards.filter)
        assertFalse(cards.isDeck)
        verify(cardsHelper).filterAndSortMultipleSets(filter, searchCards)
        verify(mtgCardDataSource).searchCards(searchParams)
        verify(cardsPreferences).load()
        verifyNoMoreInteractions(mtgCardDataSource, favouritesDataSource, cardsPreferences, cardsHelper)
    }

    @Test
    fun testShouldRetrieveCardsByMultiverseId() {
        whenever(mtgCardDataSource.searchCard(MULTIVERSE_ID)).thenReturn(card)

        val result = underTest.loadCard(MULTIVERSE_ID)

        assertThat(result, `is`(card))
        verify(mtgCardDataSource).searchCard(MULTIVERSE_ID)
        verifyNoMoreInteractions(mtgCardDataSource)
    }

    @Test
    fun testShouldRetrieveCardsById() {
        whenever(mtgCardDataSource.searchCardById(5)).thenReturn(card)

        val result = underTest.loadCardById(5)

        assertThat(result, `is`(card))
        verify(mtgCardDataSource).searchCardById(5)
        verifyNoMoreInteractions(mtgCardDataSource)
    }

    @Test
    @Throws(Exception::class)
    fun shouldRetrieveOtherSideCards() {
        var result = underTest.loadOtherSide(mainSideCard)
        assertThat(result, `is`<MTGCard>(secondSideCard))

        result = underTest.loadOtherSide(secondSideCard)
        assertThat(result, `is`(mainSideCard))
    }

    @Test
    @Throws(Exception::class)
    fun loadOtherSide_shouldReturnTheSameCard_IfCardIsNotDouble() {
        whenever(mainSideCard.names).thenReturn(listOf())
        val result = underTest.loadOtherSide(mainSideCard)
        assertThat(result, `is`(mainSideCard))

        whenever(mainSideCard.names).thenReturn(listOf("One"))
        val result2 = underTest.loadOtherSide(mainSideCard)
        assertThat(result2, `is`(mainSideCard))
    }
}

private const val MULTIVERSE_ID = 180607