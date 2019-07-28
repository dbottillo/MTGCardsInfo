package com.dbottillo.mtgsearchfree.saved

import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractor
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.storage.GeneralData
import com.dbottillo.mtgsearchfree.util.Logger
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit

class SavedCardsPresenterImplTest {

    @Rule @JvmField var mockitoRule = MockitoJUnit.rule()!!

    @Mock lateinit var view: SavedCardsView
    @Mock lateinit var interactor: SavedCardsInteractor
    @Mock lateinit var generalData: GeneralData
    @Mock lateinit var logger: Logger
    @Mock lateinit var cards: CardsCollection
    @Mock lateinit var card: MTGCard

    lateinit var underTest: SavedCardsPresenter

    @Before
    fun setup() {
        underTest = SavedCardsPresenterImpl(interactor, generalData, logger)
        underTest.init(view)
        Mockito.reset(view, generalData)
    }

    @Test
    fun `init should load show grid if preference is grid`() {
        whenever(generalData.isCardsShowTypeGrid).thenReturn(true)

        underTest.init(view)

        verify(view).showCardsGrid()
        verify(generalData).isCardsShowTypeGrid
        verifyNoMoreInteractions(interactor, view, generalData)
    }

    @Test
    fun `init should load show list if preference is not grid`() {
        whenever(generalData.isCardsShowTypeGrid).thenReturn(false)

        underTest.init(view)

        verify(view).showCardsList()
        verify(generalData).isCardsShowTypeGrid
        verifyNoMoreInteractions(interactor, view, generalData)
    }

    @Test
    fun `load should load favourites and render`() {
        whenever(interactor.load()).thenReturn(Observable.just(cards))

        underTest.load()

        verify(view).render(SavedCardsUiModel.Loading)
        verify(interactor).load()
        verify(view).render(SavedCardsUiModel.Data(cards))
        verifyNoMoreInteractions(interactor, view, generalData)
    }

    @Test
    fun `remove from favourite should call interactor and show cards if any left`() {
        whenever(interactor.remove(card)).thenReturn(Observable.just(cards))

        underTest.removeFromFavourite(card)

        verify(interactor).remove(card)
        verify(view).render(SavedCardsUiModel.Data(cards))
        verifyNoMoreInteractions(interactor, view, generalData)
    }

    @Test
    fun `toggle card view preference should switch to list if it was grid`() {
        whenever(generalData.isCardsShowTypeGrid).thenReturn(true)

        underTest.toggleCardTypeViewPreference()

        verify(generalData).isCardsShowTypeGrid
        verify(generalData).setCardsShowTypeList()
        verify(view).showCardsList()
        verifyNoMoreInteractions(interactor, view, generalData)
    }

    @Test
    fun `toggle card view preference should switch to grid if it was list`() {
        whenever(generalData.isCardsShowTypeGrid).thenReturn(false)

        underTest.toggleCardTypeViewPreference()

        verify(generalData).isCardsShowTypeGrid
        verify(generalData).setCardsShowTypeGrid()
        verify(view).showCardsGrid()
        verifyNoMoreInteractions(interactor, view, generalData)
    }
}