package com.dbottillo.mtgsearchfree.ui.sets

import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.model.storage.GeneralData
import com.dbottillo.mtgsearchfree.util.Logger

class SetsFragmentPresenterImpl(val setsInteractor: SetsInteractor,
                                val cardsInteractor: CardsInteractor,
                                val cardsPreferences: CardsPreferences,
                                val generalData: GeneralData,
                                val logger: Logger) : SetsFragmentPresenter {

    lateinit var set: MTGSet
    var currentPos: Int = -1

    lateinit var view: SetsFragmentView

    override fun init(view: SetsFragmentView) {
        this.view = view
    }

    override fun loadSets() {
        logger.d()
        setsInteractor.load().subscribe {
            val newPos = cardsPreferences.setPosition
            if (newPos != currentPos) {
                currentPos = newPos
                set = it[currentPos]
                reloadSet()
            }
        }
    }

    override fun reloadSet() {
        logger.d()
        cardsInteractor.loadSet(set).subscribe {
            view.showSet(set, it)
        }
    }

    override fun set(): MTGSet? {
        return set
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
