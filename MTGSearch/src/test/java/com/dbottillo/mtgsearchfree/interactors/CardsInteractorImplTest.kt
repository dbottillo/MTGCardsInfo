package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.model.storage.CardsStorage
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import java.util.*

class CardsInteractorImplTest {

    private val MULTIVERSE_ID = 180607

    @Rule @JvmField
    var mockitoRule = MockitoJUnit.rule()

    lateinit var underTest: CardsInteractor

    @Mock
    lateinit var cardsStorage: CardsStorage
    @Mock
    lateinit var set: MTGSet
    @Mock
    lateinit var searchParams: SearchParams
    @Mock
    lateinit var card: MTGCard
    @Mock
    lateinit var otherSideCard: MTGCard
    @Mock
    lateinit var logger: Logger

    private val favCards = Arrays.asList(MTGCard(3), MTGCard(4))

    @Mock
    lateinit var lukcyCardsCollection: CardsCollection

    @Mock
    lateinit var setCollection: CardsCollection

    @Mock
    lateinit var searchCardsCollection: CardsCollection

    @Mock
    lateinit var schedulerProvider: SchedulerProvider

    @Before
    fun setup() {
        `when`(schedulerProvider.io()).thenReturn(Schedulers.trampoline())
        `when`(schedulerProvider.ui()).thenReturn(Schedulers.trampoline())
        underTest = CardsInteractorImpl(cardsStorage, schedulerProvider, logger)
    }

    @Test
    fun `get lucky cards should call storage and return observable`() {
        `when`(cardsStorage.getLuckyCards(2)).thenReturn(lukcyCardsCollection)
        val testSubscriber = TestObserver<CardsCollection>()
        
        underTest.getLuckyCards(2).subscribe(testSubscriber)
        
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(lukcyCardsCollection)
        verify(cardsStorage).getLuckyCards(2)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(cardsStorage, schedulerProvider)
    }

    @Test
    fun `get favourites should call storage and return observable`() {
        `when`(cardsStorage.getFavourites()).thenReturn(favCards)
        val testSubscriber = TestObserver<List<MTGCard>>()

        underTest.getFavourites().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(favCards)
        verify(cardsStorage).getFavourites()
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(cardsStorage, schedulerProvider)
    }

    @Test
    fun `save card as favourites should call storage and return observable`() {
        underTest.saveAsFavourite(card)

        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verify(cardsStorage).saveAsFavourite(card)
        verifyNoMoreInteractions(cardsStorage, schedulerProvider)
    }

    @Test
    fun `remove card as favourites should call storage and return observable`() {
        underTest.removeFromFavourite(card)

        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verify(cardsStorage).removeFromFavourite(card)
        verifyNoMoreInteractions(cardsStorage, schedulerProvider)
    }

    @Test
    fun `load set should call storage and return observable`() {
        `when`(cardsStorage.load(set)).thenReturn(setCollection)
        val testSubscriber = TestObserver<CardsCollection>()

        underTest.loadSet(set).subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(setCollection)
        verify(cardsStorage).load(set)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(cardsStorage, schedulerProvider)
    }

    @Test
    fun `load ifs of favourites should call storage and return observable`() {
        val idFavs = intArrayOf(6, 7, 8)
        `when`(cardsStorage.loadIdFav()).thenReturn(idFavs)
        val testSubscriber = TestObserver<IntArray>()

        underTest.loadIdFav().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(idFavs)
        verify(cardsStorage).loadIdFav()
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(cardsStorage, schedulerProvider)
    }

    @Test
    fun `do search should call storage and return observable`() {
        `when`(cardsStorage.doSearch(searchParams)).thenReturn(searchCardsCollection)
        val testSubscriber = TestObserver<CardsCollection>()

        underTest.doSearch(searchParams).subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(searchCardsCollection)
        verify(cardsStorage).doSearch(searchParams)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(cardsStorage, schedulerProvider)
    }

    @Test
    fun `load card with multiverse id should call storage and return observable`() {
        `when`(cardsStorage.loadCard(MULTIVERSE_ID)).thenReturn(card)
        val testSubscriber = TestObserver<MTGCard>()

        underTest.loadCard(MULTIVERSE_ID).subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(card)
        verify(cardsStorage).loadCard(MULTIVERSE_ID)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(cardsStorage, schedulerProvider)
    }

    @Test
    fun `load other side of card should call storage and return observable`() {
        `when`(cardsStorage.loadOtherSide(card)).thenReturn(otherSideCard)
        val testSubscriber = TestObserver<MTGCard>()

        underTest.loadOtherSideCard(card).subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(otherSideCard)
        verify(cardsStorage).loadOtherSide(card)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(cardsStorage, schedulerProvider)
    }

}