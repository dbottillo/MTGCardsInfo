package com.dbottillo.mtgsearchfree.ui.decks

import com.dbottillo.mtgsearchfree.interactors.SchedulerProvider
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.storage.CardsStorage
import com.dbottillo.mtgsearchfree.model.storage.DecksStorage
import com.dbottillo.mtgsearchfree.ui.decks.addToDeck.AddToDeckData
import com.dbottillo.mtgsearchfree.ui.decks.addToDeck.AddToDeckInteractor
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit

class AddToDeckInteractorTest {

    @Rule
    @JvmField
    var mockitoRule = MockitoJUnit.rule()

    @Mock lateinit var schedulerProvider: SchedulerProvider

    @Mock lateinit var decksStorage: DecksStorage
    @Mock lateinit var cardsStorage: CardsStorage
    @Mock lateinit var card: MTGCard
    @Mock lateinit var decks: List<Deck>
    @Mock lateinit var deck: Deck

    private lateinit var underTest: AddToDeckInteractor

    @Before
    fun setup() {
        `when`(schedulerProvider.io()).thenReturn(Schedulers.trampoline())
        `when`(schedulerProvider.ui()).thenReturn(Schedulers.trampoline())
        underTest = AddToDeckInteractor(decksStorage, cardsStorage, schedulerProvider)
    }

    @Test
    fun `init should zip load to decks and card`() {
        `when`(decksStorage.load()).thenReturn(decks)
        `when`(cardsStorage.loadCard(4)).thenReturn(card)
        val testObserver = TestObserver<AddToDeckData>()

        underTest.init(4).subscribe(testObserver)

        testObserver.assertComplete()
        assertThat(testObserver.values()[0].card, `is`(card))
        assertThat(testObserver.values()[0].decks, `is`(decks))
        verify(decksStorage).load()
        verify(cardsStorage).loadCard(4)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(decksStorage, cardsStorage, schedulerProvider)
    }

    @Test
    fun `add card to new deck should call storage and complete`() {
        underTest.addCard("deck", card, 2)

        verify(decksStorage).addCard("deck", card, 2)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(decksStorage, cardsStorage, schedulerProvider)
    }

    @Test
    fun `add card to an existing deck should call storage and complete`() {
        underTest.addCard(deck, card, 2)

        verify(decksStorage).addCard(deck, card, 2)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(decksStorage, cardsStorage, schedulerProvider)
    }
}