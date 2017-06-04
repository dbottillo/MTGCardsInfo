package com.dbottillo.mtgsearchfree.ui.sets

import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.model.storage.GeneralData
import com.dbottillo.mtgsearchfree.presenter.Runner
import com.dbottillo.mtgsearchfree.presenter.RunnerFactory
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.Logger

class SetsFragmentPresenterImpl(val setsInteractor: SetsInteractor,
                                val cardsInteractor: CardsInteractor,
                                val cardsPreferences: CardsPreferences,
                                val runnerFactory: RunnerFactory,
                                val generalData: GeneralData,
                                val logger: Logger) : SetsFragmentPresenter {

    val setsWrapper: Runner<List<MTGSet>> = runnerFactory.simple()
    val cardsWrapper: Runner<CardsCollection> = runnerFactory.simple()

    val sets: MutableList<MTGSet> = mutableListOf()
    var set: MTGSet? = null
    var currentPos: Int = -1

    lateinit var view: SetsFragmentView

    override fun init(view: SetsFragmentView) {
        this.view = view
    }

    override fun loadSets() {
        logger.d()
        val obs = setsInteractor.load()
        setsWrapper.run(obs, object : Runner.RxWrapperListener<List<MTGSet>> {
            override fun onNext(data: List<MTGSet>) {
                sets.clear()
                sets.addAll(data)
            }

            override fun onError(e: Throwable?) {
                LOG.e("on onError")
            }

            override fun onCompleted() {
                val newPos = cardsPreferences.setPosition
                if (newPos == currentPos){
                    return;
                }
                currentPos = cardsPreferences.setPosition
                set = sets[currentPos]
                set?.let { loadSet(it) }
            }

        })
    }

    override fun reloadSet() {
        set?.let { loadSet(it) }
    }

    private fun loadSet(set: MTGSet) {
        logger.d()
        cardsWrapper.run(cardsInteractor.loadSet(set), object : Runner.RxWrapperListener<CardsCollection> {
            override fun onError(e: Throwable?) {
            }

            override fun onCompleted() {
            }

            override fun onNext(data: CardsCollection) {
                view.showSet(set, data)
            }
        })

    }

    override fun set(): MTGSet? {
        return if (sets.size > cardsPreferences.setPosition) sets[cardsPreferences.setPosition] else null
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

    override fun saveAsFavourite(card: MTGCard) {
        cardsInteractor.saveAsFavourite(card)
    }

}
