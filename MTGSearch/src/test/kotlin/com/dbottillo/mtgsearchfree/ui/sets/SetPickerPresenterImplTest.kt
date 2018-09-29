package com.dbottillo.mtgsearchfree.ui.sets

import com.dbottillo.mtgsearchfree.interactors.SetsInteractor
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.util.Logger
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
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

class SetPickerPresenterImplTest {

    @Rule @JvmField var mockitoRule = MockitoJUnit.rule()!!

    @Mock lateinit var view: SetPickerView
    @Mock lateinit var interactor: SetsInteractor
    @Mock lateinit var cardsPreferences: CardsPreferences
    @Mock lateinit var sets: List<MTGSet>

    lateinit var underTest: SetPickerPresenter

    @Before
    fun setup() {
        underTest = SetPickerPresenterImpl(interactor, cardsPreferences)
        underTest.init(view)
    }

    @Test
    fun `load sets should call interactor and update view`() {
        val set = mock<MTGSet>()
        whenever(cardsPreferences.setPosition).thenReturn(5)
        whenever(sets[5]).thenReturn(set)
        whenever(sets.indexOf(set)).thenReturn(2)
        whenever(interactor.load()).thenReturn(Observable.just(sets))

        underTest.loadSets()

        verify(view).showSets(sets, 2)
        verify(interactor).load()
        verify(cardsPreferences).setPosition
        verifyNoMoreInteractions(interactor, cardsPreferences, view)
    }

    @Test
    fun `set selected should update position and close screen`() {
        val firstSet = MTGSet(id = 1, name = "Commander", code = "COM")
        val selectedSet = MTGSet(id = 2, name = "Kaladesh", code = "KAL")
        val thirdSet = MTGSet(id = 3, name = "Jayce vs Vraska", code = "JVV")
        whenever(cardsPreferences.setPosition).thenReturn(1)
        whenever(sets[1]).thenReturn(selectedSet)
        whenever(sets.indexOf(selectedSet)).thenReturn(2)
        whenever(interactor.load()).thenReturn(Observable.just(listOf(firstSet, selectedSet, thirdSet)))
        underTest.loadSets()
        Mockito.reset(view)

        underTest.setSelected(thirdSet)

        verify(view).close()
        verify(interactor).load()
        verify(cardsPreferences).setPosition
        verify(cardsPreferences).saveSetPosition(2)
        verifyNoMoreInteractions(interactor, cardsPreferences, view)
    }

    @Test
    fun `should search sets by name and code`() {
        val firstSet = MTGSet(id = 1, name = "Commander", code = "COM")
        val selectedSet = MTGSet(id = 2, name = "Kaladesh", code = "KAL")
        val thirdSet = MTGSet(id = 3, name = "Jayce vs Vraska", code = "JVV")
        whenever(cardsPreferences.setPosition).thenReturn(1)
        whenever(sets[1]).thenReturn(selectedSet)
        whenever(sets.indexOf(selectedSet)).thenReturn(2)
        whenever(interactor.load()).thenReturn(Observable.just(listOf(firstSet, selectedSet, thirdSet)))
        underTest.loadSets()
        Mockito.reset(view)

        underTest.search("Com")
        underTest.search("vra")
        underTest.search("de")
        underTest.search("KAL")

        inOrder(view) {
            verify(view).showSets(listOf(firstSet), -1)
            verify(view).showSets(listOf(thirdSet), -1)
            verify(view).showSets(listOf(firstSet, selectedSet), 1)
            verify(view).showSets(listOf(selectedSet), 0)
        }
        verify(interactor).load()
        verify(cardsPreferences).setPosition
        verifyNoMoreInteractions(interactor, cardsPreferences, view)
    }
}