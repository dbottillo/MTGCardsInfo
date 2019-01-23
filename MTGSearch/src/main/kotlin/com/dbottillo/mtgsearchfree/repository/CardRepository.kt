package com.dbottillo.mtgsearchfree.repository

import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.TCGPrice
import com.dbottillo.mtgsearchfree.network.ApiInterface
import io.reactivex.Single
import javax.inject.Inject

class CardRepository @Inject constructor(val api: ApiInterface) {

    fun fetchPrice(card: MTGCard): Single<TCGPrice> {
        return api.fetchPrice(card.tcgplayerProductId).flatMap {
            if (!it.success || it.results.isEmpty()) Single.error(CardPriceException(card.tcgplayerProductId))
            else {
                Single.just(TCGPrice(hiPrice = it.results[0].highPrice.toString(),
                    lowprice = it.results[0].lowPrice.toString(),
                    avgPrice = it.results[0].midPrice.toString()))
            }
        }
    }
}