package com.dbottillo.mtgsearchfree.repository

import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.TCGPrice
import com.dbottillo.mtgsearchfree.network.ApiInterface
import com.dbottillo.mtgsearchfree.network.ApiTCGPrice
import io.reactivex.Single
import javax.inject.Inject

class CardRepository @Inject constructor(
    private val api: ApiInterface,
    private val mapper: CardPriceMapper
) {

    fun fetchPrice(card: MTGCard): Single<TCGPrice> {
        return api.fetchPrice(card.tcgplayerProductId).flatMap {
            when (val result = mapper.map(it)) {
                is TCGPriceResult.Error -> Single.error(CardPriceException(card.tcgplayerProductId))
                is TCGPriceResult.Price -> Single.just(result.data)
            }
        }
    }
}

class CardPriceMapper @Inject constructor() {

    fun map(input: ApiTCGPrice): TCGPriceResult {
        if (!input.success || input.results.isEmpty()) return TCGPriceResult.Error
        val apiPrice = input.results.find { it.subTypeName == "Normal" } ?: input.results[0]
        return TCGPriceResult.Price(TCGPrice(
            hiPrice = apiPrice.highPrice.toString(),
            lowprice = apiPrice.lowPrice.toString(),
            avgPrice = apiPrice.midPrice.toString()))
    }
}

sealed class TCGPriceResult {
    object Error : TCGPriceResult()
    data class Price(val data: TCGPrice) : TCGPriceResult()
}