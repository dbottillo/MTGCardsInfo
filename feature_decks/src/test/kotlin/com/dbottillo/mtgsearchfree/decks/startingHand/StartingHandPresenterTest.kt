package com.dbottillo.mtgsearchfree.decks.startingHand

import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.DeckCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.util.Logger
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit

class StartingHandPresenterTest {

    @Rule @JvmField val mockitoRule = MockitoJUnit.rule()!!

    @Mock lateinit var interactor: DecksInteractor
    @Mock lateinit var logger: Logger
    @Mock lateinit var view: StartingHandView
    @Mock lateinit var deck: Deck
    @Mock lateinit var card: MTGCard
    @Mock lateinit var cards: DeckCollection

    lateinit var underTest: StartingHandPresenter

    private lateinit var deckCollection: DeckCollection

    @Before
    fun setup() {
        deckCollection = DeckCollection()
        deckCollection.creatures.add(MTGCard(name = "creature", quantity = 4, isSideboard = false))
        deckCollection.instantAndSorceries.add(MTGCard(name = "instant", quantity = 4, isSideboard = false))
        deckCollection.other.add(MTGCard(name = "other", quantity = 3, isSideboard = false))
        deckCollection.lands.add(MTGCard(name = "lands", quantity = 4, isSideboard = false))
        deckCollection.side.add(MTGCard(name = "side", quantity = 4, isSideboard = true))
        underTest = StartingHandPresenter(interactor, logger)
        underTest.init(view, 2L)
    }

    @Test
    fun `load deck should call interactor if bundle is null`() {
        whenever(interactor.loadDeck(2L)).thenReturn(Observable.just(deckCollection))

        underTest.loadDeck(Pair(null, null))

        verify(interactor).loadDeck(2L)
        argumentCaptor<MutableList<StartingHandCard>>().apply {
            verify(view).showOpeningHands(capture())

            assertThat(firstValue.size, `is`(7))
        }
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `load deck should call interactor if bundle is null and handle deck with less than 7 cards`() {
        val smallDeckCollection = DeckCollection()
        smallDeckCollection.creatures.add(MTGCard(name = "creature", quantity = 2, isSideboard = false))
        whenever(interactor.loadDeck(2L)).thenReturn(Observable.just(smallDeckCollection))

        underTest.loadDeck(Pair(null, null))

        verify(interactor).loadDeck(2L)
        argumentCaptor<MutableList<StartingHandCard>>().apply {
            verify(view).showOpeningHands(capture())

            assertThat(firstValue.size, `is`(2))
        }
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `load deck should call interactor if bundle is not null and it doesn't contain cards`() {
        whenever(interactor.loadDeck(2L)).thenReturn(Observable.just(deckCollection))

        underTest.loadDeck(Pair(listOf(), listOf()))

        verify(interactor).loadDeck(2L)
        argumentCaptor<MutableList<StartingHandCard>>().apply {
            verify(view).showOpeningHands(capture())

            assertThat(firstValue.size, `is`(7))
        }
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `load deck should restore bundle if contains cards`() {
        val left = arrayListOf(StartingHandCard(gathererImage = "image12", name = "name1"))
        val shown = arrayListOf(StartingHandCard(gathererImage = "image22", name = "name2"))

        underTest.loadDeck(Pair(left, shown))

        argumentCaptor<MutableList<StartingHandCard>>().apply {
            verify(view).showOpeningHands(capture())

            assertThat(firstValue.size, `is`(1))
            assertThat(firstValue[0].name, `is`("name2"))
            assertThat(firstValue[0].gathererImage, `is`("image22"))
        }
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `repeat should clear view and reload a deck`() {
        whenever(interactor.loadDeck(2L)).thenReturn(Observable.just(deckCollection))

        underTest.repeat()

        verify(interactor).loadDeck(2L)
        verify(view).clear()
        argumentCaptor<MutableList<StartingHandCard>>().apply {
            verify(view).showOpeningHands(capture())

            assertThat(firstValue.size, `is`(7))
        }
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `next should show next card`() {
        whenever(interactor.loadDeck(2L)).thenReturn(Observable.just(deckCollection))
        underTest.loadDeck(Pair(null, null))
        Mockito.reset(interactor, view)

        underTest.next()

        argumentCaptor<StartingHandCard>().apply {
            verify(view).newCard(capture())

            assertNotNull(firstValue)
        }
        verifyNoMoreInteractions(view, interactor)
    }
}