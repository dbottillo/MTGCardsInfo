package com.dbottillo.mtgsearchfree.ui.cards

import android.content.Intent
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractor
import com.dbottillo.mtgsearchfree.model.*
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.presenter.Runner
import com.dbottillo.mtgsearchfree.presenter.RunnerFactory
import com.dbottillo.mtgsearchfree.ui.cards.CardsActivity.*
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Observable

class CardsActivityPresenterImpl(val cardsInteractor: CardsInteractor,
                                 val savedCardsInteractor: SavedCardsInteractor,
                                 val decksInteractor: DecksInteractor,
                                 val cardsPreferences: CardsPreferences,
                                 val factory: RunnerFactory,
                                 val logger: Logger) : CardsActivityPresenter {

    val idFavsRunner: Runner<IntArray> = factory.simple()
    val cardsRunner: Runner<CardsCollection> = factory.simple()

    var set: MTGSet? = null
    var search: SearchParams? = null
    var deck: Deck? = null
    var startPosition: Int = 0
    var isFavs: Boolean = false
    var currentData: CardsCollection? = null
    lateinit var favs: MutableList<Int>
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
            }
            startPosition = intent.getIntExtra(POSITION, 0)
        } else {
            view.finish()
        }

        idFavsRunner.run(cardsInteractor.loadIdFav(), object : Runner.RxWrapperListener<IntArray> {
            override fun onNext(data: IntArray) {
                favs = data.toMutableList()
            }

            override fun onError(e: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onCompleted() {
                if (set != null) {
                    set?.let { loadData(cardsInteractor.loadSet(it)) }
                } else if (deck != null) {
                    deck?.let {loadData(decksInteractor.loadDeck(it))}
                } else if (search != null) {
                    // load search
                } else {
                    loadData(savedCardsInteractor.load())
                }
            }

        })
    }

    internal fun loadData(obs: Observable<CardsCollection>) {
        logger.d()
        cardsRunner.run(obs, object : Runner.RxWrapperListener<CardsCollection> {
            override fun onNext(data: CardsCollection) {
                currentData = data
                updateView()
            }

            override fun onError(e: Throwable?) {
            }

            override fun onCompleted() {
            }
        })
    }

    private fun updateView() {
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
        if (cardsPreferences.showImage()) {
            view.setImageMenuItemChecked(true)
        } else {
            view.setImageMenuItemChecked(false)
        }
    }

    override fun favClicked(currentCard: MTGCard?) {
        currentCard?.let {
            if (favs.contains(currentCard.multiVerseId)) {
                cardsInteractor.removeFromFavourite(currentCard)
                favs.remove(currentCard.multiVerseId)
            } else {
                cardsInteractor.saveAsFavourite(currentCard)
                favs.add(currentCard.multiVerseId)
            }
            updateMenu(currentCard)
        }
    }

    override fun isDeck(): Boolean {
        return deck != null
    }

    override fun toggleImage(show: Boolean) {
        cardsPreferences.setShowImage(show)
        updateView()
    }
}
