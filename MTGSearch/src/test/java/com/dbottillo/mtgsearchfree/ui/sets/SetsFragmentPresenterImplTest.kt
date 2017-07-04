package com.dbottillo.mtgsearchfree.ui.sets

import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.model.storage.GeneralData
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit

class SetsFragmentPresenterImplTest {

    @Rule @JvmField
    var mockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var view: SetsFragmentView
    @Mock
    lateinit var setsInteractor: SetsInteractor
    @Mock
    lateinit var cardsInteractor: CardsInteractor
    @Mock
    lateinit var generalData: GeneralData
    @Mock
    lateinit var cardsPreferences: CardsPreferences
    @Mock
    lateinit var logger: Logger
    @Mock
    lateinit var sets: List<MTGSet>
    @Mock
    lateinit var cards: CardsCollection
    @Mock
    lateinit var set: MTGSet
    @Mock
    lateinit var card: MTGCard

    lateinit var underTest: SetsFragmentPresenter

    @Before
    fun setup() {
        underTest = SetsFragmentPresenterImpl(setsInteractor, cardsInteractor, cardsPreferences, generalData, logger)
        underTest.init(view)
    }

    @Test
    fun `load sets should load sets and then load set if it's first time`() {
        `when`(setsInteractor.load()).thenReturn(Observable.just(sets))
        `when`(cardsInteractor.loadSet(set)).thenReturn(Observable.just(cards))
        `when`(cardsPreferences.setPosition).thenReturn(5)
        `when`(sets[5]).thenReturn(set)

        underTest.loadSets()

        verify(setsInteractor).load()
        verify(cardsInteractor).loadSet(set)
        verify(view).showSet(set, cards)
        verify(cardsPreferences).setPosition
        verify(sets)[5]
        verifyNoMoreInteractions(setsInteractor, cardsInteractor, cardsPreferences, view, generalData)
    }

    @Test
    fun `load sets should load sets and then do nothing if the same set is selected`() {
        afterFirstLoadSets()
        `when`(setsInteractor.load()).thenReturn(Observable.just(sets))
        `when`(cardsPreferences.setPosition).thenReturn(5)

        underTest.loadSets()

        verify(setsInteractor).load()
        verify(cardsPreferences).setPosition
        verifyNoMoreInteractions(setsInteractor, cardsInteractor, cardsPreferences, view, generalData)
    }

    @Test
    fun `reload set should just refresh the set and update the view`() {
        afterFirstLoadSets()
        `when`(cardsInteractor.loadSet(set)).thenReturn(Observable.just(cards))

        underTest.reloadSet()

        verify(cardsInteractor).loadSet(set)
        verify(view).showSet(set, cards)
        verifyNoMoreInteractions(setsInteractor, cardsInteractor, cardsPreferences, view, generalData)
    }

    @Test
    fun `toggle card view preference should switch to list if it was grid`() {
        `when`(generalData.isCardsShowTypeGrid).thenReturn(true)

        underTest.toggleCardTypeViewPreference()

        verify(generalData).isCardsShowTypeGrid
        verify(generalData).setCardsShowTypeList()
        verify(view).showCardsList()
        verifyNoMoreInteractions(setsInteractor, cardsInteractor, view, generalData)
    }

    @Test
    fun `toggle card view preference should switch to grid if it was list`() {
        `when`(generalData.isCardsShowTypeGrid).thenReturn(false)

        underTest.toggleCardTypeViewPreference()

        verify(generalData).isCardsShowTypeGrid
        verify(generalData).setCardsShowTypeGrid()
        verify(view).showCardsGrid()
        verifyNoMoreInteractions(setsInteractor, cardsInteractor, view, generalData)
    }

    @Test
    fun `save card as favourite should just call interactor`() {
        underTest.saveAsFavourite(card)

        verify(cardsInteractor).saveAsFavourite(card)
        verifyNoMoreInteractions(setsInteractor, cardsInteractor, view, generalData)
    }

    internal fun afterFirstLoadSets(){
        `when`(setsInteractor.load()).thenReturn(Observable.just(sets))
        `when`(cardsInteractor.loadSet(set)).thenReturn(Observable.just(cards))
        `when`(cardsPreferences.setPosition).thenReturn(5)
        `when`(sets[5]).thenReturn(set)
        underTest.loadSets()
        Mockito.reset(setsInteractor, cardsInteractor, cardsPreferences, view, generalData)
    }
}