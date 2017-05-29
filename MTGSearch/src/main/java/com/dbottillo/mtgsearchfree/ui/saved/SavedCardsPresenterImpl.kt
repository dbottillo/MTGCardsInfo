package com.dbottillo.mtgsearchfree.ui.saved

import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractor
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.storage.GeneralData
import com.dbottillo.mtgsearchfree.presenter.Runner
import com.dbottillo.mtgsearchfree.presenter.RunnerFactory
import com.dbottillo.mtgsearchfree.util.Logger

class SavedCardsPresenterImpl(val interactor: SavedCardsInteractor,
                              runnerFactor: RunnerFactory,
                              val generalData: GeneralData,
                              val logger: Logger) : SavedCardsPresenter {

    var cardsCollectionRunner: Runner<CardsCollection> = runnerFactor.simple<CardsCollection>()
    lateinit var view: SavedCardsView

    var listener = object : Runner.RxWrapperListener<CardsCollection>{
        override fun onNext(data: CardsCollection) {
            logger.d()
            view.showCards(data)
        }

        override fun onError(e: Throwable?) {
        }

        override fun onCompleted() {
        }
    }

    override fun load() {
        cardsCollectionRunner.run(interactor.load(),listener)
    }

    override fun removeFromFavourite(card: MTGCard) {
        cardsCollectionRunner.run(interactor.remove(card), listener)
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