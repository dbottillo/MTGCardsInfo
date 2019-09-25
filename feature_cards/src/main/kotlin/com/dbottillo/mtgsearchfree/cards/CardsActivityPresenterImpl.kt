package com.dbottillo.mtgsearchfree.cards

import android.content.Intent
import android.graphics.Bitmap
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractor
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class CardsActivityPresenterImpl(
    private val cardsInteractor: CardsInteractor,
    private val savedCardsInteractor: SavedCardsInteractor,
    private val decksInteractor: DecksInteractor,
    private val cardsPreferences: CardsPreferences,
    private val logger: Logger
) : CardsActivityPresenter {

    var set: MTGSet? = null
    var search: SearchParams? = null
    var deckId: Long? = null
    var startPosition: Int = 0
    var isFavs: Boolean = false/**/
    var currentData: CardsCollection? = null
    var favs: MutableList<Int> = mutableListOf()

    lateinit var view: CardsActivityView

    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun init(view: CardsActivityView, intent: Intent?) {
        logger.d()
        this.view = view

        if (intent != null) {
            when {
                intent.hasExtra(KEY_SET) -> {
                    set = intent.getParcelableExtra(KEY_SET)
                    set?.let {
                        view.updateTitle(it.name)
                        view.renderPreview(it.isPreview)
                    }
                }
                intent.hasExtra(KEY_SEARCH) -> {
                    search = intent.getParcelableExtra(KEY_SEARCH)
                    view.updateTitle(R.string.action_search)
                }
                intent.hasExtra(KEY_DECK) -> deckId = intent.getLongExtra(KEY_DECK, 0)
                intent.hasExtra(KEY_FAV) -> {
                    isFavs = true
                    view.updateTitle(R.string.action_saved)
                }
                else -> {
                    view.finish()
                    return
                }
            }
            startPosition = intent.getIntExtra(POSITION, 0)
        } else {
            view.finish()
            return
        }

        view.showLoading()
        disposable.add(cardsInteractor.loadIdFav().subscribe({
            favs.addAll(it.toList())
            when {
                set != null -> set?.let { loadData(cardsInteractor.loadSet(it)) }
                deckId != null -> deckId?.let { loadDeck(deckId!!) }
                search != null -> search?.let { loadData(cardsInteractor.doSearch(it)) }
                else -> loadData(savedCardsInteractor.load())
            }
        }, {
            view.hideLoading()
            showError(it)
            logger.logNonFatal(it)
        }))
    }

    private fun loadData(obs: Observable<CardsCollection>) {
        logger.d()
        disposable.add(obs.subscribe({
            view.hideLoading()
            currentData = it
            updateView()
        }, {
            view.hideLoading()
            showError(it)
            logger.logNonFatal(it)
        }))
    }

    private fun loadDeck(deckId: Long) {
        logger.d()
        disposable.add(decksInteractor.loadDeckById(deckId)
                .toObservable()
                .flatMap { deck ->
                    decksInteractor.loadDeck(deck.id).map {
                        Pair(deck, it)
                    }
                }.subscribe({
                    view.hideLoading()
                    view.updateTitle(it.first.name)
                    currentData = it.second.toCardsCollection()
                    updateView()
                }, {
                    view.hideLoading()
                    showError(it)
                    logger.logNonFatal(it)
                }))
    }

    private fun updateView() {
        currentData?.let { view.updateAdapter(it, cardsPreferences.showImage(), startPosition) }
    }

    override fun updateMenu(currentCard: MTGCard?) {
        logger.d()
        if (currentCard != null && currentCard.multiVerseId > 0 && favs.isNotEmpty()) {
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
        return deckId != null
    }

    override fun toggleImage(show: Boolean) {
        cardsPreferences.setShowImage(show)
        updateView()
    }

    private fun showError(throwable: Throwable) {
        logger.e(throwable)
        view.showError(throwable.localizedMessage)
    }

    override fun shareImage(bitmap: Bitmap) {
        disposable.add(cardsInteractor.getArtworkUri(bitmap).subscribe({
            view.shareUri(it)
        }, {
            showError(it)
            logger.logNonFatal(it)
        }))
    }

    override fun onDestroy() {
        disposable.clear()
    }
}
