package com.dbottillo.mtgsearchfree.ui.saved

import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractor
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.storage.GeneralData
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit

class SavedCardsPresenterImplTest {

    @Rule @JvmField
    var mockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var view: SavedCardsView
    @Mock
    lateinit var interactor: SavedCardsInteractor
    @Mock
    lateinit var generalData: GeneralData
    @Mock
    lateinit var logger: Logger
    @Mock
    lateinit var cards: CardsCollection
    @Mock
    lateinit var card: MTGCard

    lateinit var underTest: SavedCardsPresenter

    @Before
    fun setup() {
        underTest = SavedCardsPresenterImpl(interactor, generalData, logger)
        underTest.init(view)
    }

    @Test
    fun `load should load favourites and update view`() {
        `when`(interactor.load()).thenReturn(Observable.just(cards))

        underTest.load()

        verify(interactor).load()
        verify(view).showCards(cards)
        verifyNoMoreInteractions(interactor, view, generalData)
    }

    @Test
    fun `remove from favourite should call interactor and update view`() {
        `when`(interactor.remove(card)).thenReturn(Observable.just(cards))

        underTest.removeFromFavourite(card)

        verify(interactor).remove(card)
        verify(view).showCards(cards)
        verifyNoMoreInteractions(interactor, view, generalData)
    }

    @Test
    fun `toggle card view preference should switch to list if it was grid`() {
        `when`(generalData.isCardsShowTypeGrid).thenReturn(true)

        underTest.toggleCardTypeViewPreference()

        verify(generalData).isCardsShowTypeGrid
        verify(generalData).setCardsShowTypeList()
        verify(view).showCardsList()
        verifyNoMoreInteractions(interactor, view, generalData)
    }

    @Test
    fun `toggle card view preference should switch to grid if it was list`() {
        `when`(generalData.isCardsShowTypeGrid).thenReturn(false)

        underTest.toggleCardTypeViewPreference()

        verify(generalData).isCardsShowTypeGrid
        verify(generalData).setCardsShowTypeGrid()
        verify(view).showCardsGrid()
        verifyNoMoreInteractions(interactor, view, generalData)
    }
}