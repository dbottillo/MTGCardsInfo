package com.dbottillo.mtgsearchfree.search

import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.SearchParams
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

class SearchPresenterImplTest {

    @Rule @JvmField var mockitoRule = MockitoJUnit.rule()!!

    @Mock lateinit var view: SearchActivityView
    @Mock lateinit var setsInteractor: SetsInteractor
    @Mock lateinit var cardsInteractor: CardsInteractor
    @Mock lateinit var generalData: GeneralData
    @Mock lateinit var logger: Logger
    @Mock lateinit var sets: List<MTGSet>
    @Mock lateinit var searchParams: SearchParams
    @Mock lateinit var cards: CardsCollection
    @Mock lateinit var card: MTGCard

    lateinit var underTest: SearchPresenter

    @Before
    fun setUp() {
        underTest = SearchPresenterImpl(setsInteractor, cardsInteractor, generalData, logger)
    }

    @Test
    fun `init should show grid if general data is set to grid`() {
        whenever(generalData.isCardsShowTypeGrid).thenReturn(true)

        underTest.init(view)

        verify(view).showCardsGrid()
        verify(generalData).isCardsShowTypeGrid
        verifyNoMoreInteractions(setsInteractor, cardsInteractor, generalData, view)
    }

    @Test
    fun `init should show list if general data is set to list`() {
        whenever(generalData.isCardsShowTypeGrid).thenReturn(false)

        underTest.init(view)

        verify(view).showCardsList()
        verify(generalData).isCardsShowTypeGrid
        verifyNoMoreInteractions(setsInteractor, cardsInteractor, generalData, view)
    }

    @Test
    fun `load set should call interactor and update view`() {
        underTest.init(view)
        Mockito.reset(setsInteractor, cardsInteractor, generalData, view)
        whenever(setsInteractor.load()).thenReturn(Observable.just(sets))

        underTest.loadSet()

        verify(view).setLoaded(sets)
        verify(setsInteractor).load()
        verifyNoMoreInteractions(setsInteractor, cardsInteractor, generalData, view)
    }

    @Test
    fun `do search should call interactor and update view`() {
        underTest.init(view)
        Mockito.reset(setsInteractor, cardsInteractor, generalData, view)
        whenever(cardsInteractor.doSearch(searchParams)).thenReturn(Observable.just(cards))

        underTest.doSearch(searchParams)

        verify(view).showSearch(cards)
        verify(cardsInteractor).doSearch(searchParams)
        verifyNoMoreInteractions(setsInteractor, cardsInteractor, generalData, view)
    }

    @Test
    fun `toggle card view preference should switch to list if it was grid`() {
        underTest.init(view)
        Mockito.reset(setsInteractor, cardsInteractor, generalData, view)
        whenever(generalData.isCardsShowTypeGrid).thenReturn(true)

        underTest.toggleCardTypeViewPreference()

        verify(generalData).isCardsShowTypeGrid
        verify(generalData).setCardsShowTypeList()
        verify(view).showCardsList()
        verifyNoMoreInteractions(setsInteractor, cardsInteractor, view, generalData)
    }

    @Test
    fun `toggle card view preference should switch to grid if it was list`() {
        underTest.init(view)
        Mockito.reset(setsInteractor, cardsInteractor, generalData, view)
        whenever(generalData.isCardsShowTypeGrid).thenReturn(false)

        underTest.toggleCardTypeViewPreference()

        verify(generalData).isCardsShowTypeGrid
        verify(generalData).setCardsShowTypeGrid()
        verify(view).showCardsGrid()
        verifyNoMoreInteractions(setsInteractor, cardsInteractor, view, generalData)
    }

    @Test
    fun `save card as favourite should just call interactor`() {
        underTest.init(view)
        Mockito.reset(setsInteractor, cardsInteractor, generalData, view)

        underTest.saveAsFavourite(card)

        verify(cardsInteractor).saveAsFavourite(card)
        verifyNoMoreInteractions(setsInteractor, cardsInteractor, view, generalData)
    }
}