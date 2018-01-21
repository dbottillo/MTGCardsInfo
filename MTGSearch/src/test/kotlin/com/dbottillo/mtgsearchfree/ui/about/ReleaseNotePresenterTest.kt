package com.dbottillo.mtgsearchfree.ui.about

import com.dbottillo.mtgsearchfree.interactors.ReleaseNoteInteractor
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

class ReleaseNotePresenterTest {

    @Rule
    @JvmField
    var mockitoRule = MockitoJUnit.rule()

    @Mock lateinit var interactor: ReleaseNoteInteractor
    @Mock lateinit var view: ReleaseNoteView
    @Mock lateinit var list: List<ReleaseNoteItem>

    private lateinit var underTest: ReleaseNotePresenter

    @Before
    fun setUp() {
        underTest = ReleaseNotePresenter(interactor)
        underTest.init(view)
    }

    @Test
    fun `should show items if interactor is successful`() {
        whenever(interactor.load()).thenReturn(Single.just(list))

        underTest.load()

        verify(view).showItems(list)
        verify(interactor).load()
        verifyNoMoreInteractions(interactor, view)
    }

    @Test
    fun `should show error if interactor is unsuccessful`() {
        whenever(interactor.load()).thenReturn(Single.error(Throwable("error")))

        underTest.load()

        verify(view).showError("error")
        verify(interactor).load()
        verifyNoMoreInteractions(interactor, view)
    }
}