package com.dbottillo.mtgsearchfree.model.storage

import com.dbottillo.mtgsearchfree.model.*
import com.dbottillo.mtgsearchfree.model.database.DeckDataSource
import com.dbottillo.mtgsearchfree.model.database.FavouritesDataSource
import com.dbottillo.mtgsearchfree.model.database.MTGCardDataSource
import com.dbottillo.mtgsearchfree.util.Logger
import com.dbottillo.mtgsearchfree.view.helpers.CardsHelper
import junit.framework.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

import java.util.Arrays

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.mockito.ArgumentMatchers
import org.mockito.Matchers.anyBoolean
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`

class CardsStorageImplTest {

    @Rule @JvmField
    var mockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var deck: Deck
    @Mock
    private lateinit var card: MTGCard
    @Mock
    private lateinit var set: MTGSet
    @Mock
    private lateinit var mainSideCard: MTGCard
    @Mock
    private lateinit var secondSideCard: MTGCard
    @Mock
    lateinit var filter: CardFilter
    @Mock
    private lateinit var logger: Logger
    @Mock
    internal lateinit var mtgCardDataSource: MTGCardDataSource
    @Mock
    internal lateinit var favouritesDataSource: FavouritesDataSource
    @Mock
    lateinit var cardsPreferences: CardsPreferences
    @Mock
    lateinit var cardsHelper: CardsHelper

    private val setCards = Arrays.asList(MTGCard(5), MTGCard(6))
    private val setCardsFiltered = Arrays.asList(MTGCard(15), MTGCard(16))
    private val luckyCards = Arrays.asList(MTGCard(8), MTGCard(9))
    private val searchCards = Arrays.asList(MTGCard(12), MTGCard(13))
    private val searchCardsFiltered = Arrays.asList(MTGCard(121), MTGCard(131))
    private lateinit var favCards: List<MTGCard>
    private lateinit var underTest: CardsStorageImpl

    @Before
    fun setupStorage() {
        val fav1 = MTGCard(7)
        fav1.multiVerseId = 100
        val fav2 = MTGCard(8)
        fav1.multiVerseId = 101
        favCards = Arrays.asList(fav1, fav2)
        `when`(mtgCardDataSource.getSet(set)).thenReturn(setCards)
        `when`(favouritesDataSource.getCards(ArgumentMatchers.anyBoolean())).thenReturn(favCards)
        `when`(mtgCardDataSource.getRandomCard(2)).thenReturn(luckyCards)
        `when`(mtgCardDataSource.searchCards(Matchers.any(SearchParams::class.java))).thenReturn(searchCards)
        `when`(mainSideCard.name).thenReturn("One")
        `when`(secondSideCard.name).thenReturn("Two")
        `when`(mtgCardDataSource.searchCard("Two")).thenReturn(secondSideCard)
        `when`(mtgCardDataSource.searchCard("One")).thenReturn(mainSideCard)
        `when`(mainSideCard.names).thenReturn(Arrays.asList("One", "Two"))
        `when`(secondSideCard.names).thenReturn(Arrays.asList("One", "Two"))
        `when`(cardsPreferences.load()).thenReturn(filter)
        underTest = CardsStorageImpl(mtgCardDataSource, favouritesDataSource, cardsPreferences, cardsHelper, logger)
    }

    @Test
    fun testLoad() {
        `when`(cardsHelper.filterCards(filter, setCards)).thenReturn(setCardsFiltered)

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
        verify<FavouritesDataSource>(favouritesDataSource).getCards(false)
        assertThat(favs.size, `is`(2))
        assertThat(favs[0], `is`(favCards[0].multiVerseId))
        assertThat(favs[1], `is`(favCards[1].multiVerseId))
    }

    @Test
    fun testGetLuckyCards() {
        val lucky = underTest.getLuckyCards(2)

        verify<MTGCardDataSource>(mtgCardDataSource).getRandomCard(2)
        assertThat(lucky.list.size, `is`(2))
        assertThat(lucky.list, `is`(luckyCards))
    }

    @Test
    fun testGetFavourites() {
        val favs = underTest.getFavourites()
        verify<FavouritesDataSource>(favouritesDataSource).getCards(true)
        assertNotNull(favs)
        assertThat(favs, `is`<List<MTGCard>>(favCards))
    }

    @Test
    fun testDoSearch() {
        val searchParams = mock(SearchParams::class.java)
        `when`(cardsHelper.filterCards(filter, searchCards)).thenReturn(searchCardsFiltered)

        val cards = underTest.doSearch(searchParams)

        assertThat(cards.list, `is`(searchCardsFiltered))
        assertNull(cards.filter)
        assertFalse(cards.isDeck)

    }

    @Test
    fun testShouldRetrieveCardsByMultiverseId() {
        `when`(mtgCardDataSource.searchCard(MULTIVERSE_ID)).thenReturn(card)

        val result = underTest.loadCard(MULTIVERSE_ID)

        assertThat(result, `is`<MTGCard>(card))
        verify<MTGCardDataSource>(mtgCardDataSource).searchCard(MULTIVERSE_ID)
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
        `when`(mainSideCard.names).thenReturn(null)
        val result = underTest.loadOtherSide(mainSideCard)
        assertThat(result, `is`(mainSideCard))

        `when`(mainSideCard.names).thenReturn(Arrays.asList("One"))
        val result2 = underTest.loadOtherSide(mainSideCard)
        assertThat(result2, `is`(mainSideCard))
    }

    companion object {

        private val MULTIVERSE_ID = 180607
    }
}