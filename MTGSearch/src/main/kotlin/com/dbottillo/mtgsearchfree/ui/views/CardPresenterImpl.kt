package com.dbottillo.mtgsearchfree.ui.views

import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.TCGPrice
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Single

import javax.inject.Inject

class CardPresenterImpl @Inject constructor(
    private val interactor: CardsInteractor,
    private val logger: Logger
) : CardPresenter {

    private lateinit var cardView: CardView

    init {
        logger.d("created")
    }

    override fun init(cardView: CardView) {
        this.cardView = cardView
    }

    override fun loadOtherSideCard(card: MTGCard) {
        interactor.loadOtherSideCard(card).subscribe {
            cardView.otherSideCardLoaded(it)
        }
    }

    override fun fetchPrice(card: MTGCard): Single<TCGPrice> {
        return interactor.fetchPrice(card)
    }
}