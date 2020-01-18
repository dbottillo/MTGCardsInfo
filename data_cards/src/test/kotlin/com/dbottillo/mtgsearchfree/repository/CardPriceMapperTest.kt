package com.dbottillo.mtgsearchfree.repository

import com.dbottillo.mtgsearchfree.model.MKMCardPrice
import com.dbottillo.mtgsearchfree.model.TCGCardPrice
import com.dbottillo.mtgsearchfree.network.ApiTCGPrice
import com.dbottillo.mtgsearchfree.network.ApiTCGPriceResult
import com.dbottillo.mtgsearchfree.network.MKMPriceGuideApi
import com.dbottillo.mtgsearchfree.network.MKMProductApi
import com.dbottillo.mtgsearchfree.network.MKMSingleProductApi
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit

class CardPriceMapperTest {

    @JvmField @Rule val mockitoRule = MockitoJUnit.rule()!!

    lateinit var underTest: CardPriceMapper

    @Before
    fun setup() {
        underTest = CardPriceMapper()
    }

    @Test
    fun `should return error if tcg api price is not successful`() {
        val input = mock<ApiTCGPrice>()
        whenever(input.success).thenReturn(false)

        assertThat(underTest.mapTCG(input)).isEqualTo(CardPriceResult.Error)
    }

    @Test
    fun `should return error if tcg api price is empty`() {
        val input = mock<ApiTCGPrice>()
        whenever(input.success).thenReturn(true)
        whenever(input.results).thenReturn(emptyList())

        assertThat(underTest.mapTCG(input)).isEqualTo(CardPriceResult.Error)
    }

    @Test
    fun `should return price of first tcg card with sub type name normal`() {
        val input = mock<ApiTCGPrice>()
        whenever(input.success).thenReturn(true)
        val price1 = mock<ApiTCGPriceResult>()
        whenever(price1.subTypeName).thenReturn("Foil")
        val price2 = mock<ApiTCGPriceResult>()
        whenever(price2.subTypeName).thenReturn("Normal")
        whenever(price2.highPrice).thenReturn(20.0)
        whenever(price2.lowPrice).thenReturn(10.0)
        whenever(price2.midPrice).thenReturn(5.0)
        whenever(input.results).thenReturn(listOf(price1, price2))

        assertThat(underTest.mapTCG(input)).isEqualTo(CardPriceResult.Data(TCGCardPrice(
            hiPrice = "20.0",
            lowprice = "10.0",
            avgPrice = "5.0"
        )))
    }

    @Test
    fun `should return price of first tcg card if there is no subtype normal`() {
        val input = mock<ApiTCGPrice>()
        whenever(input.success).thenReturn(true)
        val price1 = mock<ApiTCGPriceResult>()
        whenever(price1.subTypeName).thenReturn("Foil")
        val price2 = mock<ApiTCGPriceResult>()
        whenever(price2.subTypeName).thenReturn("Foil")
        whenever(price1.highPrice).thenReturn(20.0)
        whenever(price1.lowPrice).thenReturn(10.0)
        whenever(price1.midPrice).thenReturn(5.0)
        whenever(input.results).thenReturn(listOf(price1, price2))

        assertThat(underTest.mapTCG(input)).isEqualTo(CardPriceResult.Data(TCGCardPrice(
            hiPrice = "20.0",
            lowprice = "10.0",
            avgPrice = "5.0"
        )))
    }

    @Test
    fun `should return error if mkm api price product is null`() {
        val input = mock<MKMSingleProductApi>()
        whenever(input.product).thenReturn(null)

        assertThat(underTest.mapMKM(input, true)).isEqualTo(CardPriceResult.Error)
    }

    @Test
    fun `should return error if mkm api price price guide is null`() {
        val input = mock<MKMSingleProductApi>()
        val product = mock<MKMProductApi>()
        whenever(product.priceGuide).thenReturn(null)
        whenever(input.product).thenReturn(product)

        assertThat(underTest.mapMKM(input, true)).isEqualTo(CardPriceResult.Error)
    }

    @Test
    fun `should return price of mkm api price price guide with no website link`() {
        val input = mock<MKMSingleProductApi>()
        val product = mock<MKMProductApi>()
        val priceGuide = mock<MKMPriceGuideApi>()
        whenever(priceGuide.LOW).thenReturn(20.0)
        whenever(priceGuide.TREND).thenReturn(10.0)
        whenever(product.priceGuide).thenReturn(priceGuide)
        whenever(input.product).thenReturn(product)

        assertThat(underTest.mapMKM(input, true)).isEqualTo(CardPriceResult.Data(MKMCardPrice(
            low = "20.0",
            url = "",
            trend = "10.0",
            exact = true
        )))
    }

    @Test
    fun `should return price of mkm api price price guide with website link`() {
        val input = mock<MKMSingleProductApi>()
        val product = mock<MKMProductApi>()
        val priceGuide = mock<MKMPriceGuideApi>()
        whenever(priceGuide.LOW).thenReturn(20.0)
        whenever(priceGuide.TREND).thenReturn(10.0)
        whenever(product.priceGuide).thenReturn(priceGuide)
        whenever(input.product).thenReturn(product)
        whenever(product.website).thenReturn("website")

        assertThat(underTest.mapMKM(input, false)).isEqualTo(CardPriceResult.Data(MKMCardPrice(
            low = "20.0",
            url = "website",
            trend = "10.0",
            exact = false
        )))
    }
}