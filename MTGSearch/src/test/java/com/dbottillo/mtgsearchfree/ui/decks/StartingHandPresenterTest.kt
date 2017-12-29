package com.dbottillo.mtgsearchfree.ui.decks

import android.os.Bundle
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.DeckCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.util.Logger
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import junit.framework.Assert.assertNotNull
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit

class StartingHandPresenterTest {

    @Rule
    @JvmField
    val mockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var interactor: DecksInteractor
    @Mock
    internal lateinit var logger: Logger
    @Mock lateinit var view: StartingHandView
    @Mock lateinit var deck: Deck
    @Mock lateinit var bundle: Bundle
    @Mock lateinit var arguments: Bundle
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
        `when`(arguments.get(DECK_KEY)).thenReturn(deck)
        underTest = StartingHandPresenter(interactor)
        underTest.init(view, arguments)
    }

    @Test
    fun `load deck should call interactor if bundle is null`() {
        whenever(interactor.loadDeck(deck)).thenReturn(Observable.just(deckCollection))

        underTest.loadDeck(null)

        verify(interactor).loadDeck(deck)
        argumentCaptor<MutableList<StartingHandCard>>().apply {
            verify(view).showOpeningHands(capture())

            assertThat(firstValue.size, `is`(7))
        }
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `load deck should call interactor if bundle is not null and it doesn't contain cards`() {
        whenever(bundle.getParcelableArray(BUNDLE_KEY_LEFT)).thenReturn(arrayOf<StartingHandCard>())
        whenever(bundle.getParcelableArray(BUNDLE_KEY_SHOWN)).thenReturn(arrayOf<StartingHandCard>())
        whenever(interactor.loadDeck(deck)).thenReturn(Observable.just(deckCollection))

        underTest.loadDeck(bundle)

        verify(interactor).loadDeck(deck)
        argumentCaptor<MutableList<StartingHandCard>>().apply {
            verify(view).showOpeningHands(capture())

            assertThat(firstValue.size, `is`(7))
        }
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `load deck should restore bundle if contains cards`() {
        whenever(bundle.getParcelableArray(BUNDLE_KEY_LEFT)).thenReturn(arrayOf(StartingHandCard("image1", "name1")))
        whenever(bundle.getParcelableArray(BUNDLE_KEY_SHOWN)).thenReturn(arrayOf(StartingHandCard("image2", "name2")))

        underTest.loadDeck(bundle)

        argumentCaptor<MutableList<StartingHandCard>>().apply {
            verify(view).showOpeningHands(capture())

            assertThat(firstValue.size, `is`(1))
            assertThat(firstValue[0].name, `is`("name2"))
            assertThat(firstValue[0].image, `is`("image2"))
        }
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `repeat should clear view and reload a deck`() {
        whenever(interactor.loadDeck(deck)).thenReturn(Observable.just(deckCollection))

        underTest.repeat()

        verify(interactor).loadDeck(deck)
        verify(view).clear()
        argumentCaptor<MutableList<StartingHandCard>>().apply {
            verify(view).showOpeningHands(capture())

            assertThat(firstValue.size, `is`(7))
        }
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `next should show next card`() {
        whenever(interactor.loadDeck(deck)).thenReturn(Observable.just(deckCollection))
        underTest.loadDeck(null)
        Mockito.reset(interactor, view)

        underTest.next()

        argumentCaptor<StartingHandCard>().apply {
            verify(view).newCard(capture())

            assertNotNull(firstValue)
        }
        verifyNoMoreInteractions(view, interactor)
    }
}