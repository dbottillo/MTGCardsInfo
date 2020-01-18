package com.dbottillo.mtgsearchfree.repository

import com.dbottillo.mtgsearchfree.model.MKMCardPrice
import com.dbottillo.mtgsearchfree.model.TCGCardPrice
import com.dbottillo.mtgsearchfree.network.ApiTCGPrice
import com.dbottillo.mtgsearchfree.network.MKMSingleProductApi
import javax.inject.Inject

class CardPriceMapper @Inject constructor() {

    fun mapTCG(input: ApiTCGPrice): CardPriceResult {
        if (!input.success || input.results.isEmpty()) return CardPriceResult.Error
        val apiPrice = input.results.find { it.subTypeName == "Normal" } ?: input.results[0]
        return CardPriceResult.Data(
            TCGCardPrice(
                hiPrice = apiPrice.highPrice.toString(),
                lowprice = apiPrice.lowPrice.toString(),
                avgPrice = apiPrice.midPrice.toString()
            )
        )
    }

    fun mapMKM(input: MKMSingleProductApi, exact: Boolean): CardPriceResult {
        val priceGuideApi = input.product?.priceGuide ?: return CardPriceResult.Error
        return CardPriceResult.Data(
            MKMCardPrice(
                low = priceGuideApi.LOW.toString(),
                url = input.product?.website ?: "",
                trend = priceGuideApi.TREND.toString(),
                exact = exact
            )
        )
    }
}