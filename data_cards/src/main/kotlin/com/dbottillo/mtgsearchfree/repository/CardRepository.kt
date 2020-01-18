package com.dbottillo.mtgsearchfree.repository

import com.dbottillo.mtgsearchfree.model.CardPrice
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.network.MKMApiInterface
import com.dbottillo.mtgsearchfree.network.TCGApiInterface
import io.reactivex.Single
import javax.inject.Inject

class CardRepository @Inject constructor(
    private val tcgApi: TCGApiInterface,
    private val mkmApi: MKMApiInterface,
    private val mapper: CardPriceMapper
) {

    fun fetchPriceTCG(card: MTGCard): Single<CardPrice> {
        return tcgApi.fetchPrice(card.tcgplayerProductId).flatMap {
            when (val result = mapper.mapTCG(it)) {
                is CardPriceResult.Error -> Single.error(CardPriceException(card.tcgplayerProductId))
                is CardPriceResult.Data -> Single.just(result.data)
            }
        }
    }

    fun fetchPriceMKM(card: MTGCard): Single<CardPrice> {
        return mkmApi.findProduct(card.name.replace(" ", "+"))
            .flatMap { productsApi ->
                var exact = true
                val product = productsApi.product?.find { it.expansionName == card.set?.name }
                if (product == null) {
                    exact = false
                }
                val productId: Long? = product?.idProduct ?: productsApi.product?.first()?.idProduct
                if (productId == null) {
                    Single.error(Throwable("can't find MKM product id"))
                } else {
                    mkmApi.findProduct(productId).map { singleProductApi ->
                        Pair(singleProductApi, exact)
                    }
                }
            }
            .flatMap { pair ->
                when (val result = mapper.mapMKM(pair.first, pair.second)) {
                    is CardPriceResult.Error -> Single.error(CardPriceException(card.tcgplayerProductId))
                    is CardPriceResult.Data -> Single.just(result.data)
                }
            }
    }
}

sealed class CardPriceResult {
    object Error : CardPriceResult()
    data class Data(val data: CardPrice) : CardPriceResult()
}