package com.dbottillo.mtgsearchfree.repository

import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.TCGCardPrice
import com.dbottillo.mtgsearchfree.network.TCGApiInterface
import com.dbottillo.mtgsearchfree.network.ApiTCGPrice
import com.dbottillo.mtgsearchfree.network.MKMApiInterface
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

    @Mock lateinit var tcgApiInterface: TCGApiInterface
    @Mock lateinit var mkmApiInterface: MKMApiInterface
    @Mock lateinit var card: MTGCard
    @Mock lateinit var cardPriceMapper: CardPriceMapper

    lateinit var underTest: CardRepository

    @Before
    fun setup() {
        whenever(card.tcgplayerProductId).thenReturn(2)
        underTest = CardRepository(tcgApiInterface, mkmApiInterface, cardPriceMapper)
    }

    @Test
    fun `should fetch TCG price and map it`() {
        val apiPrice = mock<ApiTCGPrice>()
        val price = mock<TCGCardPrice>()
        whenever(cardPriceMapper.mapTCG(apiPrice)).thenReturn(CardPriceResult.Data(price))
        whenever(tcgApiInterface.fetchPrice(2)).thenReturn(Single.just(apiPrice))

        val result = underTest.fetchPriceTCG(card).test()

        result.assertValue(price)
        result.assertComplete()
        verify(tcgApiInterface).fetchPrice(2)
        verifyNoMoreInteractions(tcgApiInterface)
    }

    @Test
    fun `should fetch TCG price and throw an exception if call is not successful`() {
        val apiPrice = mock<ApiTCGPrice>()
        whenever(cardPriceMapper.mapTCG(apiPrice)).thenReturn(CardPriceResult.Error)
        whenever(tcgApiInterface.fetchPrice(2)).thenReturn(Single.just(apiPrice))

        val result = underTest.fetchPriceTCG(card).test()

        result.assertError(CardPriceException::class.java)
        verify(tcgApiInterface).fetchPrice(2)
        verifyNoMoreInteractions(tcgApiInterface)
    }
}