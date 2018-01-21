package com.dbottillo.mtgsearchfree.ui.sets

import com.dbottillo.mtgsearchfree.interactors.SetsInteractor
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit

class SetPickerPresenterImplTest {

    @Rule @JvmField
    var mockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var view: SetPickerView
    @Mock
    lateinit var interactor: SetsInteractor
    @Mock
    lateinit var cardsPreferences: CardsPreferences
    @Mock
    lateinit var logger: Logger
    @Mock
    lateinit var sets: List<MTGSet>

    lateinit var underTest: SetPickerPresenter

    @Before
    fun setup() {
        underTest = SetPickerPresenterImpl(interactor, cardsPreferences, logger)
        underTest.init(view)
    }

    @Test
    fun `load sets should call interactor and update view`() {
        `when`(cardsPreferences.setPosition).thenReturn(5)
        `when`(interactor.load()).thenReturn(Observable.just(sets))

        underTest.loadSets()

        verify(view).showSets(sets, 5)
        verify(interactor).load()
        verify(cardsPreferences).setPosition
        verifyNoMoreInteractions(interactor, cardsPreferences, view)
    }

    @Test
    fun `set selected should update position and close screen`() {
        underTest.setSelected(6)

        verify(view).close()
        verify(cardsPreferences).saveSetPosition(6)
        verifyNoMoreInteractions(interactor, cardsPreferences, view)
    }

}