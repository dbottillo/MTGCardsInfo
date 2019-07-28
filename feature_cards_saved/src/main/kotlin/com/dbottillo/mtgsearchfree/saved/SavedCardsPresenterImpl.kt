package com.dbottillo.mtgsearchfree.saved

import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractor
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.storage.GeneralData
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.disposables.CompositeDisposable

class SavedCardsPresenterImpl(
    val interactor: SavedCardsInteractor,
    val generalData: GeneralData,
    val logger: Logger
) : SavedCardsPresenter {

    lateinit var view: SavedCardsView
    private var disposable = CompositeDisposable()

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
        view.render(SavedCardsUiModel.Loading)
        disposable.add(interactor.load()
                .subscribe({
                    showCards(it)
                }, {
                    logger.logNonFatal(it)
                }))
    }

    override fun onPause() {
        disposable.dispose()
    }

    override fun removeFromFavourite(card: MTGCard) {
        disposable.add(interactor.remove(card).subscribe({
            showCards(it)
        }, {
            logger.logNonFatal(it)
        }))
    }

    private fun showCards(collection: CardsCollection) {
        view.render(SavedCardsUiModel.Data(collection))
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

sealed class SavedCardsUiModel {
    object Loading : SavedCardsUiModel()
    data class Data(val collection: CardsCollection) : SavedCardsUiModel()
}