package com.dbottillo.mtgsearchfree.repository

import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.TCGPrice
import com.dbottillo.mtgsearchfree.network.ApiInterface
import com.dbottillo.mtgsearchfree.network.ApiTCGPrice
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

class CardRepositoryTest {

    @JvmField @Rule val mockitoRule = MockitoJUnit.rule()!!

    @Mock lateinit var api: ApiInterface
    @Mock lateinit var card: MTGCard
    @Mock lateinit var cardPriceMapper: CardPriceMapper

    lateinit var underTest: CardRepository

    @Before
    fun setup() {
        whenever(card.tcgplayerProductId).thenReturn(2)
        underTest = CardRepository(api, cardPriceMapper)
    }

    @Test
    fun `should fetch price and map it`() {
        val apiPrice = mock<ApiTCGPrice>()
        val price = mock<TCGPrice>()
        whenever(cardPriceMapper.map(apiPrice)).thenReturn(TCGPriceResult.Price(price))
        whenever(api.fetchPrice(2)).thenReturn(Single.just(apiPrice))

        val result = underTest.fetchPrice(card).test()

        result.assertValue(price)
        result.assertComplete()
        verify(api).fetchPrice(2)
        verifyNoMoreInteractions(api)
    }

    @Test
    fun `should fetch price and throw an exception if call is not successful`() {
        val apiPrice = mock<ApiTCGPrice>()
        whenever(cardPriceMapper.map(apiPrice)).thenReturn(TCGPriceResult.Error)
        whenever(api.fetchPrice(2)).thenReturn(Single.just(apiPrice))

        val result = underTest.fetchPrice(card).test()

        result.assertError(CardPriceException::class.java)
        verify(api).fetchPrice(2)
        verifyNoMoreInteractions(api)
    }
}