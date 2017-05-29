package com.dbottillo.mtgsearchfree.model.storage

import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.model.database.DeckDataSource
import com.dbottillo.mtgsearchfree.model.database.FavouritesDataSource
import com.dbottillo.mtgsearchfree.model.database.MTGCardDataSource
import com.dbottillo.mtgsearchfree.util.Logger

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

import java.util.Arrays

import junit.framework.Assert.assertNotNull
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.mockito.ArgumentMatchers
import org.mockito.Matchers.anyBoolean
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`

class CardsStorageImplTest {

    @Rule
    var mockitoRule = MockitoJUnit.rule()

    @Mock
    private val deck: Deck? = null

    @Mock
    private val card: MTGCard? = null

    @Mock
    private val mainSideCard: MTGCard? = null

    @Mock
    private val secondSideCard: MTGCard? = null

    @Mock
    private val logger: Logger? = null

    @Mock
    internal var mtgCardDataSource: MTGCardDataSource? = null

    @Mock
    internal var deckDataSource: DeckDataSource? = null

    @Mock
    internal var favouritesDataSource: FavouritesDataSource? = null

    private val setCards = Arrays.asList(MTGCard(5), MTGCard(6))
    private val luckyCards = Arrays.asList(MTGCard(8), MTGCard(9))
    private val deckCards = Arrays.asList(MTGCard(18), MTGCard(19))
    private val searchCards = Arrays.asList(MTGCard(12), MTGCard(13))
    private var favCards: List<MTGCard>? = null

    private var underTest: CardsStorageImpl? = null

    @Before
    fun setupStorage() {
        val fav1 = MTGCard(7)
        fav1.multiVerseId = 100
        val fav2 = MTGCard(8)
        fav1.multiVerseId = 101
        favCards = Arrays.asList(fav1, fav2)
        `when`(mtgCardDataSource!!.getSet(set)).thenReturn(setCards)
        `when`(favouritesDataSource!!.getCards(ArgumentMatchers.anyBoolean())).thenReturn(favCards)
        `when`(mtgCardDataSource!!.getRandomCard(2)).thenReturn(luckyCards)
        `when`(mtgCardDataSource!!.searchCards(Matchers.any(SearchParams::class.java))).thenReturn(searchCards)
        `when`(deckDataSource!!.getCards(deck)).thenReturn(deckCards)
        `when`(mainSideCard!!.name).thenReturn("One")
        `when`(secondSideCard!!.name).thenReturn("Two")
        `when`(mtgCardDataSource!!.searchCard("Two")).thenReturn(secondSideCard)
        `when`(mtgCardDataSource!!.searchCard("One")).thenReturn(mainSideCard)
        `when`(mainSideCard.names).thenReturn(Arrays.asList("One", "Two"))
        `when`(secondSideCard.names).thenReturn(Arrays.asList("One", "Two"))
        underTest = CardsStorageImpl(mtgCardDataSource!!, deckDataSource!!, favouritesDataSource!!, logger!!)
    }

    @Test
    fun testLoad() {
        val cards = underTest!!.load(set!!)
        assertThat(cards.size, `is`(2))
        assertThat(cards[0].id, `is`(5L))
        assertThat(cards[1].id, `is`(6L))
    }

    @Test
    fun testSaveAsFavourite() {
        val card = mock(MTGCard::class.java)
        val favs = underTest!!.saveAsFavourite(card)
        verify<FavouritesDataSource>(favouritesDataSource).saveFavourites(card)
        assertThat(favs.size, `is`(2))
    }

    @Test
    fun testLoadIdFav() {
        val favs = underTest!!.loadIdFav()
        verify<FavouritesDataSource>(favouritesDataSource).getCards(false)
        assertThat(favs.size, `is`(2))
        assertThat(favs[0], `is`(favCards!![0].multiVerseId))
        assertThat(favs[1], `is`(favCards!![1].multiVerseId))
    }

    @Test
    fun testRemoveFromFavourite() {
        val card = mock(MTGCard::class.java)
        val favs = underTest!!.removeFromFavourite(card)
        verify<FavouritesDataSource>(favouritesDataSource).removeFavourites(card)
        verify<FavouritesDataSource>(favouritesDataSource).getCards(false)
        assertThat(favs.size, `is`(2))
    }

    @Test
    fun testGetLuckyCards() {
        val lucky = underTest!!.getLuckyCards(2)
        verify<MTGCardDataSource>(mtgCardDataSource).getRandomCard(2)
        assertThat(lucky.size, `is`(2))
        assertThat(lucky, `is`(luckyCards))
    }

    @Test
    fun testGetFavourites() {
        val favs = underTest!!.getFavourites()
        verify<FavouritesDataSource>(favouritesDataSource).getCards(true)
        assertNotNull(favs)
        assertThat(favs, `is`<List<MTGCard>>(favCards))
    }

    @Test
    fun testLoadDeck() {
        val cards = underTest!!.loadDeck(deck!!)
        verify<DeckDataSource>(deckDataSource).getCards(deck)
        assertNotNull(cards)
        assertThat(cards, `is`(deckCards))
    }

    @Test
    fun testDoSearch() {
        val searchParams = mock(SearchParams::class.java)
        val search = underTest!!.doSearch(searchParams)
        verify<MTGCardDataSource>(mtgCardDataSource).searchCards(searchParams)
        assertNotNull(search)
        assertThat(search, `is`(searchCards))
    }

    @Test
    fun testShouldRetrieveCardsByMultiverseId() {
        `when`(mtgCardDataSource!!.searchCard(MULTIVERSE_ID)).thenReturn(card)

        val result = underTest!!.loadCard(MULTIVERSE_ID)

        assertThat(result, `is`<MTGCard>(card))
        verify<MTGCardDataSource>(mtgCardDataSource).searchCard(MULTIVERSE_ID)
        verifyNoMoreInteractions(mtgCardDataSource)
    }

    @Test
    @Throws(Exception::class)
    fun shouldRetrieveOtherSideCards() {
        var result = underTest!!.loadOtherSide(mainSideCard!!)
        assertThat(result, `is`<MTGCard>(secondSideCard))

        result = underTest!!.loadOtherSide(secondSideCard!!)
        assertThat(result, `is`(mainSideCard))
    }

    @Test
    @Throws(Exception::class)
    fun loadOtherSide_shouldReturnTheSameCard_IfCardIsNotDouble() {
        `when`(mainSideCard!!.names).thenReturn(null)
        val result = underTest!!.loadOtherSide(mainSideCard)
        assertThat(result, `is`(mainSideCard))

        `when`(mainSideCard.names).thenReturn(Arrays.asList("One"))
        val result2 = underTest!!.loadOtherSide(mainSideCard)
        assertThat(result2, `is`(mainSideCard))
    }

    companion object {

        private val MULTIVERSE_ID = 180607

        @Mock
        private val set: MTGSet? = null
    }
}