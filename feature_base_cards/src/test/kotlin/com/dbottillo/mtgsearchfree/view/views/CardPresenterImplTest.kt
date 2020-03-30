package com.dbottillo.mtgsearchfree.view.views

import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.model.CardPrice
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.PriceProvider
import com.dbottillo.mtgsearchfree.model.PriceProvider.MKM
import com.dbottillo.mtgsearchfree.util.Logger
import com.dbottillo.mtgsearchfree.ui.views.CardPresenter
import com.dbottillo.mtgsearchfree.ui.views.CardPresenterImpl
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import io.reactivex.Single

class CardPresenterImplTest {

    @Rule @JvmField var mockitoRule = MockitoJUnit.rule()!!

    lateinit var underTest: CardPresenter

    @Mock lateinit var cardsInteractor: CardsInteractor
    @Mock lateinit var logger: Logger
    @Mock lateinit var card: MTGCard

    @Before
    fun setUp() {
        underTest = CardPresenterImpl(cardsInteractor, logger)
    }

    @Test
    fun `load other side card should call interactor and update view`() {
        val single = mock<Single<MTGCard>>()
        whenever(cardsInteractor.loadOtherSideCard(card)).thenReturn(single)

        val result = underTest.loadOtherSideCard(card)

        verify(cardsInteractor).loadOtherSideCard(card)
        assertThat(result).isEqualTo(single)
        verifyNoMoreInteractions(cardsInteractor)
    }

    @Test
    fun `fetch price should call interactor`() {
        val single = mock<Single<CardPrice>>()
        whenever(cardsInteractor.fetchPrice(card, PriceProvider.MKM)).thenReturn(single)

        val result = underTest.fetchPrice(card, MKM)

        verify(cardsInteractor).fetchPrice(card, PriceProvider.MKM)
        assertThat(result).isEqualTo(single)
        verifyNoMoreInteractions(cardsInteractor)
    }
}