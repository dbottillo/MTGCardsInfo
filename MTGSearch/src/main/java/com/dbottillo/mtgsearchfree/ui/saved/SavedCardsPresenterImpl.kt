package com.dbottillo.mtgsearchfree.ui.saved

import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractor
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.SavedCards
import com.dbottillo.mtgsearchfree.model.storage.GeneralData
import com.dbottillo.mtgsearchfree.presenter.Runner
import com.dbottillo.mtgsearchfree.presenter.RunnerFactory
import com.dbottillo.mtgsearchfree.util.Logger

class SavedCardsPresenterImpl(val interactor: SavedCardsInteractor,
                              runnerFactor: RunnerFactory,
                              val generalData: GeneralData,
                              val logger: Logger) : SavedCardsPresenter {

    var cardsRunner : Runner<SavedCards> = runnerFactor.simple<SavedCards>()
    lateinit var view: SavedCardsView

    var listener = object : Runner.RxWrapperListener<SavedCards>{
        override fun onNext(data: SavedCards) {
            logger.d()
            view.showCards(data)
        }

        override fun onError(e: Throwable?) {
        }

        override fun onCompleted() {
        }
    }

    override fun load() {
        cardsRunner.run(interactor.load(),listener)
    }

    override fun removeFromFavourite(card: MTGCard) {
        cardsRunner.run(interactor.remove(card), listener)
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

    override fun init(view: SavedCardsView) {
        this.view = view
    }



}