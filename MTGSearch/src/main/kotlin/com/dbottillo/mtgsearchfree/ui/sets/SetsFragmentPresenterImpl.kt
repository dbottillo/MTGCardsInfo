package com.dbottillo.mtgsearchfree.ui.sets

import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.storage.GeneralData
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.disposables.CompositeDisposable

class SetsFragmentPresenterImpl(
    private val setsInteractor: SetsInteractor,
    private val cardsInteractor: CardsInteractor,
    private val cardsPreferences: CardsPreferences,
    private val generalData: GeneralData,
    private val logger: Logger
) : SetsFragmentPresenter {

    var set: MTGSet? = null
    var currentPos: Int = -1

    lateinit var view: SetsFragmentView

    private var disposable = CompositeDisposable()

    override fun init(view: SetsFragmentView) {
        this.view = view

        if (generalData.isCardsShowTypeGrid) {
            view.showCardsGrid()
        } else {
            view.showCardsList()
        }
    }

    override fun loadSets() {
        logger.d()
        disposable.add(setsInteractor.load().subscribe({
            val newPos = cardsPreferences.setPosition
            if (newPos != currentPos) {
                currentPos = newPos
                set = it[currentPos]
                loadSet()
            }
        }, {
        }))
    }

    override fun reloadSet() {
        logger.d()
        loadSet()
    }

    internal fun loadSet() {
        view.showLoading()
        set?.let {
            disposable.add(cardsInteractor.loadSet(it).subscribe({
                data ->
                run {
                    view.showSet(it, data)
                    view.hideLoading()
                }
            }, {
            }))
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

    override fun onDestroy() {
        disposable.clear()
    }
}
