package com.dbottillo.mtgsearchfree.interactors

import android.net.Uri
import com.dbottillo.mtgsearchfree.RxImmediateSchedulerRule
import com.dbottillo.mtgsearchfree.exceptions.ExceptionCode
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.DeckCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.storage.DecksStorage
import com.dbottillo.mtgsearchfree.util.FileUtil
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import java.util.*

class DecksInteractorImplTest {

    @Rule @JvmField
    var mockitoRule = MockitoJUnit.rule()!!

    @Rule @JvmField
    var rxjavaRule = RxImmediateSchedulerRule()

    @Mock
    lateinit var deck: Deck
    @Mock
    lateinit var card: MTGCard
    @Mock
    lateinit var storage: DecksStorage
    @Mock
    lateinit var fileUtil: FileUtil
    @Mock
    lateinit var uri: Uri
    @Mock
    lateinit var logger: Logger

    private val decks = Arrays.asList(Deck(2), Deck(3))

    @Mock
    lateinit var deckCollection: DeckCollection

    lateinit var underTest: DecksInteractor

    @Before
    fun setup() {
        underTest = DecksInteractorImpl(storage, fileUtil, logger)
    }

    @Test
    fun `load should call storage and returns observable`() {
        val testSubscriber = TestObserver<List<Deck>>()
        `when`(storage.load()).thenReturn(decks)

        underTest.load().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(decks)
        verify(storage).load()
        verifyNoMoreInteractions(storage)
    }

    @Test
    fun testLoadDeck() {
        `when`(storage.loadDeck(deck)).thenReturn(deckCollection)
        val testSubscriber = TestObserver<DeckCollection>()

        underTest.loadDeck(deck).subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(deckCollection)
        verify(storage).loadDeck(deck)
        verifyNoMoreInteractions(storage)
    }

    @Test
    fun testAddDeck() {
        `when`(storage.addDeck("deck")).thenReturn(decks)
        val testSubscriber = TestObserver<List<Deck>>()

        underTest.addDeck("deck").subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(decks)
        verify(storage).addDeck("deck")
        verifyNoMoreInteractions(storage)
    }

    @Test
    fun testDeleteDeck() {
        `when`(storage.deleteDeck(deck)).thenReturn(decks)
        val testSubscriber = TestObserver<List<Deck>>()

        underTest.deleteDeck(deck).subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(decks)
        verify(storage).deleteDeck(deck)
        verifyNoMoreInteractions(storage)
    }

    @Test
    fun testEditDeck() {
        `when`(storage.editDeck(deck, "new name")).thenReturn(deckCollection)
        val testSubscriber = TestObserver<DeckCollection>()

        underTest.editDeck(deck, "new name").subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(deckCollection)
        verify(storage).editDeck(deck, "new name")
        verifyNoMoreInteractions(storage)
    }

    @Test
    fun testAddCard() {
        `when`(storage.addCard(deck, card, 2)).thenReturn(deckCollection)
        val testSubscriber = TestObserver<DeckCollection>()

        underTest.addCard(deck, card, 2).subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(deckCollection)
        verify(storage).addCard(deck, card, 2)
        verifyNoMoreInteractions(storage)
    }

    @Test
    fun testAddCardWithNewDeck() {
        `when`(storage.addCard("name", card, 2)).thenReturn(deckCollection)
        val testSubscriber = TestObserver<DeckCollection>()

        underTest.addCard("name", card, 2).subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(deckCollection)
        verify(storage).addCard("name", card, 2)
        verifyNoMoreInteractions(storage)
    }

    @Test
    fun testRemoveCard() {
        `when`(storage.removeCard(deck, card)).thenReturn(deckCollection)
        val testSubscriber = TestObserver<DeckCollection>()

        underTest.removeCard(deck, card).subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(deckCollection)
        verify<DecksStorage>(storage).removeCard(deck, card)
    }

    @Test
    fun testRemoveAllCard() {
        `when`(storage.removeAllCard(deck, card)).thenReturn(deckCollection)
        val testSubscriber = TestObserver<DeckCollection>()
        underTest.removeAllCard(deck, card).subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(deckCollection)
        verify<DecksStorage>(storage).removeAllCard(deck, card)
    }

    /*@Test
    public void movesCardFromSideboard() {
        when(storage.moveCardFromSideboard(deck, card, 2)).thenReturn(deckDeckCollection);
        TestObserver<List<MTGCard>> testSubscriber = new TestObserver<>();
        underTest.moveCardFromSideboard(deck, card, 2).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(deckCards);
        verify(storage).moveCardFromSideboard(deck, card, 2);
    }

    @Test
    public void movesCardToSideboard() {
        when(storage.moveCardToSideboard(deck, card, 2)).thenReturn(deckCards);
        TestObserver<List<MTGCard>> testSubscriber = new TestObserver<>();
        underTest.moveCardToSideboard(deck, card, 2).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(deckCards);
        verify(storage).moveCardToSideboard(deck, card, 2);
    }*/

    @Test
    @Throws(Throwable::class)
    fun testImportDeck() {
        `when`(storage.importDeck(uri)).thenReturn(decks)
        val testSubscriber = TestObserver<List<Deck>>()
        underTest.importDeck(uri).subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(decks)
        verify<DecksStorage>(storage).importDeck(uri)
    }

    /* @Test
    public void exportsDeck() {
        when(fileUtil.downloadDeckToSdCard(deck, deckCards)).thenReturn(true);
        TestObserver<Boolean> testSubscriber = new TestObserver<>();
        underTest.exportDeck(deck, deckCards).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(true);
    }*/

    @Test
    @Throws(MTGException::class)
    fun throwErrorIfImportFails() {
        val exception = MTGException(ExceptionCode.DECK_NOT_IMPORTED, "error")
        `when`(storage.importDeck(uri)).thenThrow(exception)
        val testSubscriber = TestObserver<List<Deck>>()
        underTest.importDeck(uri).subscribe(testSubscriber)
        testSubscriber.assertError(exception)
    }
}