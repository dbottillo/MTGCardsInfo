package com.dbottillo.mtgsearchfree.ui.views

import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.model.CardPrice
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.PriceProvider
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Single

import javax.inject.Inject

class CardPresenterImpl @Inject constructor(
    private val interactor: CardsInteractor,
    logger: Logger
) : CardPresenter {

    init {
        logger.d("created")
    }

    override fun loadOtherSideCard(card: MTGCard): Single<MTGCard> {
        return interactor.loadOtherSideCard(card)
    }

    override fun fetchPrice(card: MTGCard, priceProvider: PriceProvider): Single<CardPrice> {
        return interactor.fetchPrice(card, priceProvider)
    }
}