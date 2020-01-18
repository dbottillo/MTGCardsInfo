package com.dbottillo.mtgsearchfree.repository

import com.dbottillo.mtgsearchfree.model.TCGCardPrice
import com.dbottillo.mtgsearchfree.network.ApiTCGPrice
import com.dbottillo.mtgsearchfree.network.ApiTCGPriceResult
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit

class TCGCardTCGPriceMapperTest {

    @JvmField @Rule val mockitoRule = MockitoJUnit.rule()!!

    lateinit var underTest: CardPriceMapper

    @Before
    fun setup() {
        underTest = CardPriceMapper()
    }

    @Test
    fun `should return result error if api tcg price is not successful`() {
        val api = ApiTCGPrice(success = false, results = emptyList())

        val result = underTest.mapTCG(api)

        assertThat(result).isEqualTo(CardPriceResult.Error)
    }

    @Test
    fun `should return result error if api tcg price results is empty`() {
        val api = ApiTCGPrice(success = true, results = emptyList())

        val result = underTest.mapTCG(api)

        assertThat(result).isEqualTo(CardPriceResult.Error)
    }

    @Test
    fun `should return result price if it's the only one in the results`() {
        val api = ApiTCGPrice(success = true, results = listOf(
            ApiTCGPriceResult(23, 23.0, 30.0, 40.0, null)
        ))

        val result = underTest.mapTCG(api)

        assertThat(result).isEqualTo(CardPriceResult.Data(TCGCardPrice(hiPrice = "30.0", avgPrice = "40.0", lowprice = "23.0")))
    }

    @Test
    fun `should return normal price if there is more than one`() {
        val api = ApiTCGPrice(success = true, results = listOf(
            ApiTCGPriceResult(23, 23.0, 30.0, 40.0, "Foil"),
            ApiTCGPriceResult(24, 123.0, 130.0, 140.0, "Normal")
        ))

        val result = underTest.mapTCG(api)

        assertThat(result).isEqualTo(CardPriceResult.Data(TCGCardPrice(hiPrice = "130.0", avgPrice = "140.0", lowprice = "123.0")))
    }
}