package com.dbottillo.mtgsearchfree.ui.lucky

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.presenter.Runner
import com.dbottillo.mtgsearchfree.presenter.RunnerFactory
import com.dbottillo.mtgsearchfree.util.ArrayUtils
import com.dbottillo.mtgsearchfree.util.Logger

class CardsLuckyPresenterImpl(val cardsInteractor: CardsInteractor,
                              val cardsPreferences: CardsPreferences,
                              val logger: Logger) : CardsLuckyPresenter{

    lateinit var view: CardsLuckyView

    var luckyCards = mutableListOf<MTGCard>()
    internal var currentCard: MTGCard? = null
    internal lateinit var favs: MutableList<Int>

    override fun init(view: CardsLuckyView, bundle: Bundle?, intent: Intent?) {
        this.view = view

        cardsInteractor.loadIdFav().subscribe({
            favs = it.toMutableList()

            intent?.let{
                if (it.hasExtra(CARD)) {
                    currentCard = it.getParcelableExtra<MTGCard>(CARD)
                }
            }

            bundle?.let{
                currentCard = bundle.getParcelable<MTGCard>(CARD)
            }

            currentCard?.let{
                loadCurrentCard()
            }

            loadMoreCards()
        })
    }

    private fun loadMoreCards() {
        cardsInteractor.getLuckyCards(LUCKY_BATCH_CARDS).subscribe {
            luckyCards.addAll(it.list)
            if (currentCard == null) {
                showNextCard()
            }
            luckyCards.forEach {
                view.preFetchCardImage(it)
            }
        }
    }

    override fun showNextCard(){
        if (luckyCards.isEmpty()){
            currentCard = null
            loadMoreCards()
            return
        }
        currentCard = luckyCards.removeAt(0)
        loadCurrentCard()

        if (luckyCards.size <= 2) {
            loadMoreCards()
        }
    }

    internal fun loadCurrentCard(){
        currentCard?.let {
            view.showCard(it, cardsPreferences.showImage())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(CARD, currentCard)
    }

    // TODO: this need testing
    override fun updateMenu() {
        logger.d()
        if (favs.isEmpty()) {
            // too early
            return
        }
        if (currentCard != null && currentCard!!.multiVerseId > 0) {
            view.showFavMenuItem()
            if (favs.contains(currentCard?.multiVerseId)) {
                view.updateFavMenuItem(R.string.favourite_remove, R.drawable.ab_star_colored)
            } else {
                view.updateFavMenuItem(R.string.favourite_add, R.drawable.ab_star)
            }
        } else {
            view.hideFavMenuItem()
        }
        view.setImageMenuItemChecked(cardsPreferences.showImage())
    }

    // TODO: this need testing
    override fun saveOrRemoveCard() {
        currentCard?.let {
            if (favs.contains(it.multiVerseId)) {
                cardsInteractor.removeFromFavourite(it)
                favs.remove(it.multiVerseId)
            } else {
                cardsInteractor.saveAsFavourite(it)
                favs.add(it.multiVerseId)
            }
            updateMenu()
        }
    }

    companion object {
        val CARD = "CARD"
        val LUCKY_BATCH_CARDS = 10
    }
}