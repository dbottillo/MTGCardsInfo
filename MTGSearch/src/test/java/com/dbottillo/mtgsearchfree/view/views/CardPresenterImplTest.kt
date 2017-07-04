package com.dbottillo.mtgsearchfree.view.views

import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.util.Logger
import com.dbottillo.mtgsearchfree.view.CardView
import com.dbottillo.mtgsearchfree.view.views.CardPresenter
import com.dbottillo.mtgsearchfree.view.views.CardPresenterImpl

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

import io.reactivex.Observable

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.mockito.Mockito.*

class CardPresenterImplTest {

    @Rule @JvmField
    var mockitoRule = MockitoJUnit.rule()

    lateinit var underTest: CardPresenter

    @Mock
    lateinit var cardsInteractor: CardsInteractor

    @Mock
    lateinit var logger: Logger

    @Mock
    lateinit var view: CardView

    @Mock
    lateinit var card: MTGCard

    @Mock
    lateinit var otherCard: MTGCard

    @Before
    fun setUp() {
        underTest = CardPresenterImpl(cardsInteractor, logger)
        underTest.init(view)
    }

    @Test
    fun `load other side card should call interactor and update view`() {
        `when`(cardsInteractor.loadOtherSideCard(card)).thenReturn(Observable.just(otherCard))
        
        underTest.loadOtherSideCard(card)

        verify(cardsInteractor).loadOtherSideCard(card)
        verify(view).otherSideCardLoaded(otherCard)
        verifyNoMoreInteractions(cardsInteractor, view)
    }

}