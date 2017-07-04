package com.dbottillo.mtgsearchfree.ui.cards

import android.content.Intent
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractor
import com.dbottillo.mtgsearchfree.model.*
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.ui.cards.CardsActivity.*
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Observable

class CardsActivityPresenterImpl(val cardsInteractor: CardsInteractor,
                                 val savedCardsInteractor: SavedCardsInteractor,
                                 val decksInteractor: DecksInteractor,
                                 val cardsPreferences: CardsPreferences,
                                 val logger: Logger) : CardsActivityPresenter {

    var set: MTGSet? = null
    var search: SearchParams? = null
    var deck: Deck? = null
    var startPosition: Int = 0
    var isFavs: Boolean = false
    var currentData: CardsCollection? = null
    var favs: MutableList<Int> = mutableListOf()
    lateinit var view: CardsActivityView

    override fun init(view: CardsActivityView, intent: Intent?) {
        logger.d()
        this.view = view

        if (intent != null) {
            if (intent.hasExtra(KEY_SET)) {
                set = intent.getParcelableExtra(KEY_SET)
                set?.let { view.updateTitle(it.name) }

            } else if (intent.hasExtra(KEY_SEARCH)) {
                search = intent.getParcelableExtra(KEY_SEARCH)
                view.updateTitle(R.string.action_search)

            } else if (intent.hasExtra(KEY_DECK)) {
                deck = intent.getParcelableExtra(KEY_DECK)
                deck?.let { view.updateTitle(it.name) }

            } else if (intent.hasExtra(KEY_FAV)) {
                isFavs = true
                view.updateTitle(R.string.action_saved)
            } else {
                view.finish()
                return
            }
            startPosition = intent.getIntExtra(POSITION, 0)
        } else {
            view.finish()
            return
        }

        cardsInteractor.loadIdFav().subscribe({
            favs.addAll(it.toList())
            if (set != null) {
                set?.let { loadData(cardsInteractor.loadSet(it)) }
            } else if (deck != null) {
                deck?.let { loadDeck(decksInteractor.loadDeck(it)) }
            } else if (search != null) {
                search?.let { loadData(cardsInteractor.doSearch(it)) }
            } else {
                loadData(savedCardsInteractor.load())
            }
        }, {
            showError(it)
        })
    }

    internal fun loadData(obs: Observable<CardsCollection>) {
        logger.d()
        obs.subscribe({
            currentData = it
            updateView()
        }, {
            showError(it)
        })
    }

    internal fun loadDeck(obs: Observable<DeckCollection>) {
        logger.d()
        obs.subscribe({
            currentData = it.toCardsCollection()
            updateView()
        }, {
            showError(it)
        })
    }

    internal fun updateView() {
        currentData?.let { view.updateAdapter(it, cardsPreferences.showImage(), startPosition) }
    }

    override fun updateMenu(currentCard: MTGCard?) {
        logger.d()
        if (favs.isEmpty()) {
            // too early
            return
        }
        if (currentCard != null && currentCard.multiVerseId > 0) {
            view.showFavMenuItem()
            if (favs.contains(currentCard.multiVerseId)) {
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
    override fun favClicked(currentCard: MTGCard?) {
        currentCard?.let {
            if (favs.contains(it.multiVerseId)) {
                cardsInteractor.removeFromFavourite(it)
                favs.remove(it.multiVerseId)
            } else {
                cardsInteractor.saveAsFavourite(it)
                favs.add(it.multiVerseId)
            }
            updateMenu(it)
        }
    }

    override fun isDeck(): Boolean {
        return deck != null
    }

    override fun toggleImage(show: Boolean) {
        cardsPreferences.setShowImage(show)
        updateView()
    }

    fun showError(throwable: Throwable) {
        logger.e(throwable)
        view.showError(throwable.localizedMessage)
    }
}
