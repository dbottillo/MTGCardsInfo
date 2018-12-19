package com.dbottillo.mtgsearchfree.storage

import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.database.FavouritesDataSource
import com.dbottillo.mtgsearchfree.util.Logger
import com.nhaarman.mockito_kotlin.whenever
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnit

class SavedCardsStorageImplTest {

    @JvmField @Rule var mockitoRule = MockitoJUnit.rule()!!

    lateinit var underTest: SavedCardsStorage

    @Mock private lateinit var favouriteDataSource: FavouritesDataSource
    @Mock lateinit var cardsPreferences: CardsPreferences
    @Mock lateinit var cardsHelper: CardsHelper
    @Mock lateinit var logger: Logger

    @Mock lateinit var filter: CardFilter
    @Mock lateinit var cards: List<MTGCard>
    @Mock lateinit var filteredCards: List<MTGCard>

    @Before
    fun setUp() {
        whenever(favouriteDataSource.getCards(true)).thenReturn(cards)
        whenever(cardsPreferences.load()).thenReturn(filter)
        whenever(cardsHelper.filterCards(filter, cards)).thenReturn(filteredCards)
        underTest = SavedCardsStorageImpl(favouriteDataSource, cardsHelper, cardsPreferences, logger)
    }

    @Test
    fun load_shouldLoadFav_filterAndSortWUBGR() {
        val result = underTest.load()

        assertThat(result.list, `is`(filteredCards))
        assertThat(result.filter, `is`(filter))
        verify(favouriteDataSource).getCards(true)
        verify(cardsPreferences).load()
        verify(cardsHelper).filterCards(filter, cards)
        verifyNoMoreInteractions(filteredCards, favouriteDataSource, cardsHelper)
    }

    @Test
    fun load_shouldLoadFav_filterAndSortAZ() {
        val result = underTest.load()

        assertThat(result.list, `is`(filteredCards))
        assertThat(result.filter, `is`(filter))
        verify(favouriteDataSource).getCards(true)
        verify(cardsPreferences).load()
        verify(cardsHelper).filterCards(filter, cards)
        verifyNoMoreInteractions(filteredCards, favouriteDataSource, cardsHelper)
    }
}