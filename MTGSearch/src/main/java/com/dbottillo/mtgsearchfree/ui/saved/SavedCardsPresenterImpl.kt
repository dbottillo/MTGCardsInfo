package com.dbottillo.mtgsearchfree.ui.saved

import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractor
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.storage.GeneralData
import com.dbottillo.mtgsearchfree.util.Logger

class SavedCardsPresenterImpl(val interactor: SavedCardsInteractor,
                              val generalData: GeneralData,
                              val logger: Logger) : SavedCardsPresenter {

    lateinit var view: SavedCardsView

    override fun init(view: SavedCardsView) {
        this.view = view
    }

    override fun load() {
        interactor.load().subscribe {
            view.showCards(it)
        }
    }

    override fun removeFromFavourite(card: MTGCard) {
        interactor.remove(card).subscribe {
            view.showCards(it)
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