package com.dbottillo.mtgsearchfree.lucky

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.util.Logger
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit

class CardsLuckyPresenterImplTest {

    @Rule @JvmField var mockitoRule = MockitoJUnit.rule()!!

    @Mock lateinit var interactor: CardsInteractor
    @Mock lateinit var cardsPreferences: CardsPreferences
    @Mock internal lateinit var logger: Logger
    @Mock lateinit var view: CardsLuckyView
    @Mock lateinit var cardsCollection: CardsCollection
    @Mock lateinit var card1: MTGCard
    @Mock lateinit var card2: MTGCard
    @Mock lateinit var card3: MTGCard
    @Mock lateinit var card4: MTGCard
    @Mock lateinit var card5: MTGCard
    @Mock lateinit var card6: MTGCard
    @Mock lateinit var card7: MTGCard
    @Mock lateinit var card8: MTGCard
    @Mock lateinit var card9: MTGCard
    @Mock lateinit var bundle: Bundle
    @Mock lateinit var intent: Intent
    @Mock lateinit var cardInBundle: MTGCard
    @Mock lateinit var cardInIntent: MTGCard

    lateinit var underTest: CardsLuckyPresenterImpl
    internal val idFavs = intArrayOf(100, 101, 102)
    lateinit var cards: List<MTGCard>

    @Before
    fun setUp() {
        cards = listOf(card1, card2, card3, card4, card5)
        underTest = CardsLuckyPresenterImpl(interactor, cardsPreferences, logger)
    }

    @Test
    fun `init with bundle null and intent null, should load idFavs and cards from interactor and show first`() {
        whenever(interactor.loadIdFav()).thenReturn(Observable.just(idFavs))
        whenever(cardsPreferences.showImage()).thenReturn(true)
        whenever(interactor.getLuckyCards(10)).thenReturn(Observable.just(cardsCollection))
        whenever(cardsCollection.list).thenReturn(cards)

        underTest.init(view, null, null)

        verify(interactor).loadIdFav()
        verify(interactor).getLuckyCards(10)
        verify(cardsPreferences).showImage()
        verify(view).showCard(card1, true)
        verify(view).preFetchCardImage(card2)
        verify(view).preFetchCardImage(card3)
        verify(view).preFetchCardImage(card4)
        verify(view).preFetchCardImage(card5)
        verifyNoMoreInteractions(interactor, cardsPreferences, view)
    }

    @Test
    fun `init with bundle not null and intent null, should load idFavs and cards from interactor and show card in bundle`() {
        whenever(interactor.loadIdFav()).thenReturn(Observable.just(idFavs))
        whenever(cardsPreferences.showImage()).thenReturn(true)
        whenever(interactor.getLuckyCards(10)).thenReturn(Observable.just(cardsCollection))
        whenever(cardsCollection.list).thenReturn(cards)
        whenever(cardsPreferences.showImage()).thenReturn(false)
        whenever(interactor.loadCardById(5)).thenReturn(Single.just(cardInBundle))
        whenever(bundle.getInt(CARD)).thenReturn(5)

        underTest.init(view, bundle, null)

        verify(interactor).loadIdFav()
        verify(interactor).loadCardById(5)
        verify(interactor).getLuckyCards(10)
        verify(cardsPreferences).showImage()
        verify(view).showCard(cardInBundle, false)
        verify(view).preFetchCardImage(card1)
        verify(view).preFetchCardImage(card2)
        verify(view).preFetchCardImage(card3)
        verify(view).preFetchCardImage(card4)
        verify(view).preFetchCardImage(card5)
        verifyNoMoreInteractions(interactor, cardsPreferences, view)
    }

    @Test
    fun `init with bundle null and intent not null, should load idFavs and cards from interactor and show card in bundle`() {
        whenever(interactor.loadIdFav()).thenReturn(Observable.just(idFavs))
        whenever(cardsPreferences.showImage()).thenReturn(true)
        whenever(interactor.getLuckyCards(10)).thenReturn(Observable.just(cardsCollection))
        whenever(cardsCollection.list).thenReturn(cards)
        whenever(interactor.loadCardById(6)).thenReturn(Single.just(cardInIntent))
        whenever(intent.hasExtra(CARD)).thenReturn(true)
        whenever(intent.getIntExtra(CARD, 0)).thenReturn(6)

        underTest.init(view, null, intent)

        verify(interactor).loadIdFav()
        verify(interactor).loadCardById(6)
        verify(interactor).getLuckyCards(10)
        verify(cardsPreferences).showImage()
        verify(view).showCard(cardInIntent, true)
        verify(view).preFetchCardImage(card1)
        verify(view).preFetchCardImage(card2)
        verify(view).preFetchCardImage(card3)
        verify(view).preFetchCardImage(card4)
        verify(view).preFetchCardImage(card5)
        verifyNoMoreInteractions(interactor, cardsPreferences, view)
    }

    @Test
    fun `init with bundle not null and intent not null, should load idFavs and cards from interactor and show card in bundle`() {
        whenever(interactor.loadIdFav()).thenReturn(Observable.just(idFavs))
        whenever(cardsPreferences.showImage()).thenReturn(true)
        whenever(interactor.getLuckyCards(10)).thenReturn(Observable.just(cardsCollection))
        whenever(cardsCollection.list).thenReturn(cards)
        whenever(cardsPreferences.showImage()).thenReturn(false)
        whenever(intent.getIntExtra(CARD, 0)).thenReturn(6)
        whenever(intent.hasExtra(CARD)).thenReturn(true)
        whenever(interactor.loadCardById(5)).thenReturn(Single.just(cardInBundle))
        whenever(bundle.getInt(CARD)).thenReturn(5)

        underTest.init(view, bundle, intent)

        verify(interactor).loadIdFav()
        verify(interactor).loadCardById(5)
        verify(interactor).getLuckyCards(10)
        verify(cardsPreferences).showImage()
        verify(view).showCard(cardInBundle, false)
        verify(view).preFetchCardImage(card1)
        verify(view).preFetchCardImage(card2)
        verify(view).preFetchCardImage(card3)
        verify(view).preFetchCardImage(card4)
        verify(view).preFetchCardImage(card5)
        verifyNoMoreInteractions(interactor, cardsPreferences, view)
    }

    @Test
    fun `show next card, should just show next card in list if there are more than 2 cards left`() {
        whenever(interactor.loadIdFav()).thenReturn(Observable.just(idFavs))
        whenever(cardsPreferences.showImage()).thenReturn(true)
        whenever(interactor.getLuckyCards(10)).thenReturn(Observable.just(cardsCollection))
        whenever(cardsCollection.list).thenReturn(cards)
        underTest.init(view, null, null)
        Mockito.reset(interactor, cardsPreferences, view)
        whenever(cardsPreferences.showImage()).thenReturn(true)

        underTest.showNextCard()

        verify(cardsPreferences).showImage()
        verify(view).showCard(card2, true)
        verifyNoMoreInteractions(interactor, cardsPreferences, view)
    }

    @Test
    fun `show next card, should load more cards if list is empty`() {
        whenever(interactor.loadIdFav()).thenReturn(Observable.just(idFavs))
        whenever(cardsPreferences.showImage()).thenReturn(true)
        whenever(interactor.getLuckyCards(10)).thenReturn(Observable.just(cardsCollection))
        whenever(cardsCollection.list).thenReturn(cards)
        underTest.init(view, null, null)
        Mockito.reset(interactor, cardsPreferences, view)
        underTest.luckyCards = mutableListOf()
        whenever(cardsPreferences.showImage()).thenReturn(true)
        whenever(interactor.getLuckyCards(10)).thenReturn(Observable.just(cardsCollection))
        whenever(cardsCollection.list).thenReturn(listOf(card6, card7, card8, card9))

        underTest.showNextCard()

        verify(cardsPreferences).showImage()
        verify(view).showCard(card6, true)
        verify(interactor).getLuckyCards(10)
        verify(view).preFetchCardImage(card7)
        verify(view).preFetchCardImage(card8)
        verify(view).preFetchCardImage(card9)
        verifyNoMoreInteractions(interactor, cardsPreferences, view)
    }

    @Test
    fun `show next card, should show next card in list and load more cards if list has 2 or less elements left`() {
        whenever(interactor.loadIdFav()).thenReturn(Observable.just(idFavs))
        whenever(cardsPreferences.showImage()).thenReturn(true)
        whenever(interactor.getLuckyCards(10)).thenReturn(Observable.just(cardsCollection))
        whenever(cardsCollection.list).thenReturn(cards)
        underTest.init(view, null, null)
        Mockito.reset(interactor, cardsPreferences, view)
        underTest.luckyCards = mutableListOf(card2, card3)
        whenever(cardsPreferences.showImage()).thenReturn(true)
        whenever(interactor.getLuckyCards(10)).thenReturn(Observable.just(cardsCollection))
        whenever(cardsCollection.list).thenReturn(listOf(card6, card7, card8, card9))

        underTest.showNextCard()

        verify(cardsPreferences).showImage()
        verify(view).showCard(card2, true)
        verify(interactor).getLuckyCards(10)
        verify(view).preFetchCardImage(card3)
        verify(view).preFetchCardImage(card6)
        verify(view).preFetchCardImage(card7)
        verify(view).preFetchCardImage(card8)
        verify(view).preFetchCardImage(card9)
        verifyNoMoreInteractions(interactor, cardsPreferences, view)
    }

    @Test
    fun `should save current card in save instance state bundle`() {
        whenever(card1.id).thenReturn(4)
        underTest.currentCard = card1

        underTest.onSaveInstanceState(bundle)

        verify(bundle).putInt(CARD, 4)
        verifyNoMoreInteractions(bundle, interactor, cardsPreferences, view)
    }

    @Test
    fun `share bitmap should get artwork and share it on view`() {
        whenever(interactor.loadIdFav()).thenReturn(Observable.just(idFavs))
        whenever(cardsPreferences.showImage()).thenReturn(true)
        whenever(interactor.getLuckyCards(10)).thenReturn(Observable.just(cardsCollection))
        whenever(cardsCollection.list).thenReturn(cards)
        underTest.init(view, null, null)
        Mockito.reset(interactor, cardsPreferences, view)
        val bitmap = com.nhaarman.mockito_kotlin.mock<Bitmap>()
        val uri = com.nhaarman.mockito_kotlin.mock<Uri>()
        whenever(interactor.getArtworkUri(bitmap)).thenReturn(Single.just(uri))

        underTest.shareImage(bitmap)

        verify(view).shareUri(uri)
        verify(interactor).getArtworkUri(bitmap)
        verifyNoMoreInteractions(bundle, interactor, cardsPreferences, view)
    }

    @Test
    fun `share bitmap should should show error if get artwork uri fails`() {
        whenever(interactor.loadIdFav()).thenReturn(Observable.just(idFavs))
        whenever(cardsPreferences.showImage()).thenReturn(true)
        whenever(interactor.getLuckyCards(10)).thenReturn(Observable.just(cardsCollection))
        whenever(cardsCollection.list).thenReturn(cards)
        underTest.init(view, null, null)
        Mockito.reset(interactor, cardsPreferences, view)
        val bitmap = com.nhaarman.mockito_kotlin.mock<Bitmap>()
        val throwable = com.nhaarman.mockito_kotlin.mock<Throwable>()
        whenever(throwable.localizedMessage).thenReturn("message")
        whenever(interactor.getArtworkUri(bitmap)).thenReturn(Single.error(throwable))

        underTest.shareImage(bitmap)

        verify(view).showError("message")
        verify(interactor).getArtworkUri(bitmap)
        verifyNoMoreInteractions(bundle, interactor, cardsPreferences, view)
    }
}