package com.dbottillo.mtgsearchfree.repository

import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.TCGPrice
import com.dbottillo.mtgsearchfree.network.ApiInterface
import com.dbottillo.mtgsearchfree.network.ApiTCGPrice
import com.dbottillo.mtgsearchfree.network.ApiTCGPriceResult
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

    lateinit var underTest: CardRepository

    @Before
    fun setup() {
        whenever(card.tcgplayerProductId).thenReturn(2)
        underTest = CardRepository(api)
    }

    @Test
    fun `should fetch price and map it`() {
        whenever(api.fetchPrice(2)).thenReturn(Single.just(ApiTCGPrice(
            success = true, results = listOf(ApiTCGPriceResult(
                productId = 2, highPrice = 10.0, lowPrice = 20.0, midPrice = 30.0
            ))
        )))

        val result = underTest.fetchPrice(card).test()

        result.assertValue(TCGPrice("10.0", "20.0", "30.0"))
        result.assertComplete()
        verify(api).fetchPrice(2)
        verifyNoMoreInteractions(api)
    }

    @Test
    fun `should fetch price and throw an exception if call is not successful`() {
        whenever(api.fetchPrice(2)).thenReturn(Single.just(ApiTCGPrice(
            success = false, results = listOf(ApiTCGPriceResult(
                productId = 2, highPrice = 10.0, lowPrice = 20.0, midPrice = 30.0
            ))
        )))

        val result = underTest.fetchPrice(card).test()

        result.assertError(CardPriceException::class.java)
        verify(api).fetchPrice(2)
        verifyNoMoreInteractions(api)
    }

    @Test
    fun `should fetch price and throw an exception if there are no results`() {
        whenever(api.fetchPrice(2)).thenReturn(Single.just(ApiTCGPrice(
            success = true, results = listOf()
        )))

        val result = underTest.fetchPrice(card).test()

        result.assertError(CardPriceException::class.java)
        verify(api).fetchPrice(2)
        verifyNoMoreInteractions(api)
    }
}