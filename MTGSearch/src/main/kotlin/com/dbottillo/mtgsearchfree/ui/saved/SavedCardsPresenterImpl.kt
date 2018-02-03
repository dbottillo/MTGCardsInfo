package com.dbottillo.mtgsearchfree.ui.saved

import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractor
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.storage.GeneralData
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.disposables.Disposable

class SavedCardsPresenterImpl(val interactor: SavedCardsInteractor,
                              val generalData: GeneralData,
                              val logger: Logger) : SavedCardsPresenter {

    lateinit var view: SavedCardsView
    private var disposable: Disposable? = null

    override fun init(view: SavedCardsView) {
        this.view = view
        if (generalData.isCardsShowTypeGrid) {
            view.showCardsGrid()
        } else {
            view.showCardsList()
        }

    }

    override fun load() {
        logger.d()
        disposable = interactor.load()
                .doOnSubscribe {
                    view.showLoading()
                }
                .subscribe {
                    view.hideLoading()
                    showCards(it)
                }
    }

    override fun onPause() {
        disposable?.dispose()
    }

    override fun removeFromFavourite(card: MTGCard) {
        interactor.remove(card).subscribe {
            showCards(it)
        }
    }

    private fun showCards(collection: CardsCollection) {
        if (collection.isEmpty()) {
            view.showEmptyScreen()
        } else {
            view.showCards(collection)
        }
    }

    override fun toggleCardTypeViewPreference() {
        if (generalData.isCardsShowTypeGrid) {
            generalData.setCardsShowTypeList()
            view.showCardsList()
        } else {
            generalData.setCardsShowTypeGrid()
            view.showCardsGrid()
        }
    }

}