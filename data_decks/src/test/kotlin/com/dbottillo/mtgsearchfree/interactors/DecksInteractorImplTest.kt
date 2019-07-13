package com.dbottillo.mtgsearchfree.interactors

import android.net.Uri
import com.dbottillo.mtgsearchfree.exceptions.ExceptionCode
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.interactor.SchedulerProvider
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.DeckCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.storage.DecksStorage
import com.dbottillo.mtgsearchfree.util.FileManager
import com.dbottillo.mtgsearchfree.util.Logger
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnit

class DecksInteractorImplTest {
    @Rule @JvmField var mockitoRule = MockitoJUnit.rule()!!

    @Mock lateinit var deck: Deck
    @Mock lateinit var decks: List<Deck>
    @Mock lateinit var editedDeck: Deck
    @Mock lateinit var card: MTGCard
    @Mock lateinit var cards: List<MTGCard>
    @Mock lateinit var storage: DecksStorage
    @Mock lateinit var fileManager: FileManager
    @Mock lateinit var uri: Uri
    @Mock lateinit var logger: Logger
    @Mock lateinit var deckCollection: DeckCollection
    @Mock lateinit var schedulerProvider: SchedulerProvider

    lateinit var underTest: DecksInteractor

    @Before
    fun setup() {
        whenever(schedulerProvider.io()).thenReturn(Schedulers.trampoline())
        whenever(schedulerProvider.ui()).thenReturn(Schedulers.trampoline())
        underTest = DecksInteractorImpl(storage, fileManager, schedulerProvider, logger)
    }

    @Test
    fun `load should call storage and returns observable`() {
        val testSubscriber = TestObserver<List<Deck>>()
        whenever(storage.load()).thenReturn(decks)

        underTest.load().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(decks)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verify(storage).load()
        verifyNoMoreInteractions(storage, schedulerProvider)
    }

    @Test
    fun testLoadDeck() {
        whenever(storage.loadDeck(2L)).thenReturn(deckCollection)
        val testSubscriber = TestObserver<DeckCollection>()

        underTest.loadDeck(2L).subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(deckCollection)
        verify(storage).loadDeck(2L)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(storage, schedulerProvider)
    }

    @Test
    fun testLoadDeckById() {
        whenever(storage.loadDeckById(2L)).thenReturn(deck)
        val testSubscriber = TestObserver<Deck>()

        underTest.loadDeckById(2L).subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(deck)
        verify(storage).loadDeckById(2L)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(storage, schedulerProvider)
    }

    @Test
    fun testAddDeck() {
        whenever(storage.addDeck("deck")).thenReturn(decks)
        val testSubscriber = TestObserver<List<Deck>>()

        underTest.addDeck("deck").subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(decks)
        verify(storage).addDeck("deck")
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(storage, schedulerProvider)
    }

    @Test
    fun testDeleteDeck() {
        whenever(storage.deleteDeck(deck)).thenReturn(decks)
        val testSubscriber = TestObserver<List<Deck>>()

        underTest.deleteDeck(deck).subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(decks)
        verify(storage).deleteDeck(deck)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(storage, schedulerProvider)
    }

    @Test
    fun testEditDeck() {
        whenever(storage.editDeck(deck, "new name")).thenReturn(editedDeck)
        val testSubscriber = TestObserver<Deck>()

        underTest.editDeck(deck, "new name").subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(editedDeck)
        verify(storage).editDeck(deck, "new name")
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(storage, schedulerProvider)
    }

    @Test
    fun testAddCard() {
        whenever(storage.addCard(deck, card, 2)).thenReturn(deckCollection)
        val testSubscriber = TestObserver<DeckCollection>()

        underTest.addCard(deck, card, 2).subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(deckCollection)
        verify(storage).addCard(deck, card, 2)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(storage, schedulerProvider)
    }

    @Test
    fun testAddCardWithNewDeck() {
        whenever(storage.addCard("name", card, 2)).thenReturn(deckCollection)
        val testSubscriber = TestObserver<DeckCollection>()

        underTest.addCard("name", card, 2).subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(deckCollection)
        verify(storage).addCard("name", card, 2)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(storage, schedulerProvider)
    }

    @Test
    fun testRemoveCard() {
        whenever(storage.removeCard(deck, card)).thenReturn(deckCollection)
        val testSubscriber = TestObserver<DeckCollection>()

        underTest.removeCard(deck, card).subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(deckCollection)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verify(storage).removeCard(deck, card)
    }

    @Test
    fun testRemoveAllCard() {
        whenever(storage.removeAllCard(deck, card)).thenReturn(deckCollection)
        val testSubscriber = TestObserver<DeckCollection>()
        underTest.removeAllCard(deck, card).subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(deckCollection)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verify(storage).removeAllCard(deck, card)
    }

    @Test
    fun movesCardFromSideboard() {
        whenever(storage.moveCardFromSideboard(deck, card, 2)).thenReturn(deckCollection)

        val result = underTest.moveCardFromSideboard(deck, card, 2).test()

        result.assertNoErrors()
        result.assertValue(deckCollection)
        verify(storage).moveCardFromSideboard(deck, card, 2)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(storage, schedulerProvider)
    }

    @Test
    fun movesCardToSideboard() {
        whenever(storage.moveCardToSideboard(deck, card, 2)).thenReturn(deckCollection)

        val result = underTest.moveCardToSideboard(deck, card, 2).test()

        result.assertNoErrors()
        result.assertValue(deckCollection)
        verify(storage).moveCardToSideboard(deck, card, 2)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(storage, schedulerProvider)
    }

    @Test
    @Throws(Throwable::class)
    fun testImportDeck() {
        whenever(storage.importDeck(uri)).thenReturn(decks)
        val testSubscriber = TestObserver<List<Deck>>()
        underTest.importDeck(uri).subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(decks)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verify(storage).importDeck(uri)
    }

    @Test
    fun `export deck should load card, call file util and complete if successful`() {
        whenever(deck.id).thenReturn(2L)
        whenever(storage.loadDeck(2L)).thenReturn(deckCollection)
        whenever(deckCollection.allCards()).thenReturn(cards)
        whenever(fileManager.saveDeckToFile(deck, cards)).thenReturn(uri)

        val testObserver = underTest.exportDeck(deck).test()

        testObserver.assertValue(uri)
        verify(storage).loadDeck(2L)
        verify(fileManager).saveDeckToFile(deck, cards)
        verifyNoMoreInteractions(storage, fileManager)
    }

    @Test
    @Throws(MTGException::class)
    fun throwErrorIfImportFails() {
        val exception = MTGException(ExceptionCode.DECK_NOT_IMPORTED, "error")
        whenever(storage.importDeck(uri)).thenThrow(exception)
        val testSubscriber = TestObserver<List<Deck>>()
        underTest.importDeck(uri).subscribe(testSubscriber)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        testSubscriber.assertError(exception)
    }

    @Test
    fun `should copy deck in the background`() {
        whenever(storage.copy(deck)).thenReturn(decks)
        val testSubscriber = TestObserver<List<Deck>>()

        underTest.copy(deck).subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(decks)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verify(storage).copy(deck)
        verifyNoMoreInteractions(storage, fileManager, schedulerProvider)
    }
}