package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.model.storage.CardsStorageImpl
import com.dbottillo.mtgsearchfree.util.Logger

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

import java.util.Arrays

import io.reactivex.observers.TestObserver

import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class CardsInteractorImplTest {

    @Rule @JvmField
    var mockitoRule = MockitoJUnit.rule()

    lateinit var underTest: CardsInteractor

    @Mock
    lateinit var cardsStorageImpl: CardsStorageImpl
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

    @Before
    fun setup() {
        `when`(cardsStorageImpl.getLuckyCards(2)).thenReturn(lukcyCardsCollection)
        `when`(cardsStorageImpl.getFavourites()).thenReturn(favCards)
        `when`(cardsStorageImpl.load(set)).thenReturn(setCollection)
        `when`(cardsStorageImpl.doSearch(searchParams)).thenReturn(searchCardsCollection)
        // when(cardsStorageImpl.loadDeck(deck)).thenReturn(deckCollection);
        `when`(cardsStorageImpl.loadCard(MULTIVERSE_ID)).thenReturn(card)
        `when`(cardsStorageImpl.loadOtherSide(card)).thenReturn(otherSideCard)
        underTest = CardsInteractorImpl(cardsStorageImpl, logger)
    }

    @Test
    fun testGetLuckyCards() {
        val testSubscriber = TestObserver<CardsCollection>()
        underTest.getLuckyCards(2).subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(lukcyCardsCollection)
        verify<CardsStorageImpl>(cardsStorageImpl).getLuckyCards(2)
    }

    @Test
    fun testGetFavourites() {
        val testSubscriber = TestObserver<List<MTGCard>>()
        underTest.getFavourites().subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(favCards)
        verify<CardsStorageImpl>(cardsStorageImpl).getFavourites()
    }

    @Test
    fun testSaveAsFavourite() {
        val card = mock(MTGCard::class.java)
        val idFavs = intArrayOf(1, 2, 3)
        `when`(cardsStorageImpl.saveAsFavourite(card)).thenReturn(idFavs)
        val testSubscriber = TestObserver<IntArray>()
        underTest.saveAsFavourite(card).subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(idFavs)
        verify(cardsStorageImpl).saveAsFavourite(card)
    }

    @Test
    fun testRemoveFromFavourite() {
        val card = mock(MTGCard::class.java)
        val idFavs = intArrayOf(3, 4, 5)
        `when`(cardsStorageImpl.removeFromFavourite(card)).thenReturn(idFavs)
        val testSubscriber = TestObserver<IntArray>()
        underTest.removeFromFavourite(card).subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(idFavs)
        verify(cardsStorageImpl).removeFromFavourite(card)
    }

    @Test
    fun testLoadSet() {
        val testSubscriber = TestObserver<CardsCollection>()
        underTest.loadSet(set).subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(setCollection)
        verify<CardsStorageImpl>(cardsStorageImpl).load(set)
    }

    @Test
    fun testLoadIdFav() {
        val idFavs = intArrayOf(6, 7, 8)
        `when`(cardsStorageImpl.loadIdFav()).thenReturn(idFavs)
        val testSubscriber = TestObserver<IntArray>()
        underTest.loadIdFav().subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(idFavs)
        verify(cardsStorageImpl).loadIdFav()
    }

    /* @Test
    public void testLoadDeck() {
        TestObserver<CardsCollection> testSubscriber = new TestObserver<>();
        underTest.loadDeck(deck).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(deckCollection);
        verify(cardsStorageImpl).loadDeck(deck);
    }*/

    @Test
    fun testDoSearch() {
        val testSubscriber = TestObserver<CardsCollection>()
        underTest.doSearch(searchParams).subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(searchCardsCollection)
        verify<CardsStorageImpl>(cardsStorageImpl).doSearch(searchParams)
    }

    @Test
    fun testLoadCardWithMultiverseId() {
        val testSubscriber = TestObserver<MTGCard>()
        underTest.loadCard(MULTIVERSE_ID).subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(card)
        verify<CardsStorageImpl>(cardsStorageImpl).loadCard(MULTIVERSE_ID)
    }

    @Test
    fun testLoadOtherSideCard() {
        val testSubscriber = TestObserver<MTGCard>()
        underTest.loadOtherSideCard(card).subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(otherSideCard)
        verify<CardsStorageImpl>(cardsStorageImpl).loadOtherSide(card)
    }

    companion object {

        private val MULTIVERSE_ID = 180607
    }
}