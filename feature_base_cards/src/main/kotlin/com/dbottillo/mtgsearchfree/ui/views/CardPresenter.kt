package com.dbottillo.mtgsearchfree.ui.views

import com.dbottillo.mtgsearchfree.model.CardPrice
import com.dbottillo.mtgsearchfree.model.MTGCard
import io.reactivex.Single

interface CardPresenter {
    fun loadOtherSideCard(card: MTGCard): Single<MTGCard>
    fun fetchPrice(card: MTGCard): Single<CardPrice>
}
