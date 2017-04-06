package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.SavedCards
import com.dbottillo.mtgsearchfree.model.storage.SavedCardsStorage
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnit

class SavedCardsInteractorImplTest {

    @Rule @JvmField
    var mockitoRule = MockitoJUnit.rule()

    lateinit var underTest: SavedCardsInteractor

    @Mock lateinit var storage: SavedCardsStorage
    @Mock lateinit var logger: Logger
    @Mock lateinit var card: MTGCard
    @Mock lateinit var cards: SavedCards

    @Before
    fun setUp() {
        Mockito.`when`(storage.load()).thenReturn(cards)
        underTest = SavedCardsInteractorImpl(storage, logger)
    }

    @Test
    fun load_shouldLoadSavedCards() {
        val testObserver = TestObserver<SavedCards>()
        underTest.load().subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValueCount(1)
        testObserver.assertValue(cards)
        verify(storage).load()
        verifyNoMoreInteractions(storage)
    }

    @Test
    fun removeCard_shouldRemoveCard_andLoadSavedCards() {
        val testObserver = TestObserver<SavedCards>()
        underTest.remove(card).subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValueCount(1)
        testObserver.assertValue(cards)
        verify(storage).load()
        verify(storage).removeFromFavourite(card)
        verifyNoMoreInteractions(storage)
    }

    @Test
    fun addCard_shouldAddCard_andLoadSavedCards() {
        val testObserver = TestObserver<SavedCards>()
        underTest.save(card).subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValueCount(1)
        testObserver.assertValue(cards)
        verify(storage).load()
        verify(storage).saveAsFavourite(card)
        verifyNoMoreInteractions(storage)
    }

}