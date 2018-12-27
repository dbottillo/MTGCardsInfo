package com.dbottillo.mtgsearchfree.ui.lucky

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.disposables.CompositeDisposable

@Suppress("CAST_NEVER_SUCCEEDS")
class CardsLuckyPresenterImpl(
    val cardsInteractor: CardsInteractor,
    val cardsPreferences: CardsPreferences,
    val logger: Logger
) : CardsLuckyPresenter {

    lateinit var view: CardsLuckyView

    var luckyCards = mutableListOf<MTGCard>()
    var currentCard: MTGCard? = null
    private var favs: MutableList<Int>? = mutableListOf()

    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun init(view: CardsLuckyView, bundle: Bundle?, intent: Intent?) {
        this.view = view

        disposable.add(cardsInteractor.loadIdFav().subscribe {
            favs = it.toMutableList()

            var idCard: Int? = null

            intent?.let {
                if (it.hasExtra(CARD)) {
                    idCard = it.getIntExtra(CARD, 0)
                }
            }

            bundle?.let {
                idCard = bundle.getInt(CARD)
            }

            idCard?.let {
                cardsInteractor.loadCardById(it).subscribe { card ->
                    currentCard = card
                    loadCurrentCard()
                }
            }

            loadMoreCards()
        })
    }

    private fun loadMoreCards() {
        disposable.add(cardsInteractor.getLuckyCards(LUCKY_BATCH_CARDS).subscribe {
            luckyCards.addAll(it.list)
            if (currentCard == null) {
                showNextCard()
            }
            luckyCards.forEach {
                view.preFetchCardImage(it)
            }
        })
    }

    override fun showNextCard() {
        if (luckyCards.isEmpty()) {
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

    private fun loadCurrentCard() {
        currentCard?.let {
            view.showCard(it, cardsPreferences.showImage())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        currentCard?.let { outState.putInt(CARD, it.id) }
    }

    // TODO: this need testing
    override fun updateMenu() {
        logger.d()
        favs?.let {
            if (it.isEmpty()) {
                // too early
                return
            }
            if (currentCard != null && currentCard!!.multiVerseId > 0) {
                view.showFavMenuItem()
                if (it.contains(currentCard?.multiVerseId)) {
                    view.updateFavMenuItem(R.string.favourite_remove, R.drawable.ab_star_colored)
                } else {
                    view.updateFavMenuItem(R.string.favourite_add, R.drawable.ab_star)
                }
            } else {
                view.hideFavMenuItem()
            }
            view.setImageMenuItemChecked(cardsPreferences.showImage())
        }
    }

    // TODO: this need testing
    override fun saveOrRemoveCard() {
        currentCard?.let { card ->
            favs?.let {
                if (it.contains(card.multiVerseId)) {
                    cardsInteractor.removeFromFavourite(card)
                    it.remove(card.multiVerseId)
                } else {
                    cardsInteractor.saveAsFavourite(card)
                    it.add(card.multiVerseId)
                }
                updateMenu()
            }
        }
    }

    override fun shareImage(bitmap: Bitmap) {
        disposable.add(cardsInteractor.getArtworkUri(bitmap).subscribe({
            view.shareUri(it)
        }, {
            view.showError(it.localizedMessage)
        }))
    }

    override fun onDestroy() {
        disposable.clear()
    }
}

const val CARD = "CARD"
const val LUCKY_BATCH_CARDS = 10