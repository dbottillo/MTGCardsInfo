package com.dbottillo.mtgsearchfree.ui.sets

import com.dbottillo.mtgsearchfree.interactors.SetsInteractor
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.presenter.Runner
import com.dbottillo.mtgsearchfree.presenter.RunnerFactory
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.Logger

class SetPickerPresenterImpl(
        val setsInteractor: SetsInteractor,
        val cardsPreferences: CardsPreferences,
        val runnerFactory: RunnerFactory,
        val logger: Logger) : SetPickerPresenter {

    val setsWrapper: Runner<List<MTGSet>> = runnerFactory.simple()

    val sets: MutableList<MTGSet> = mutableListOf()

    lateinit var view: SetPickerView

    override fun init(view: SetPickerView) {
        this.view = view
    }

    override fun loadSets() {
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
                view.showSets(sets, cardsPreferences.setPosition)
            }
        })
    }

    override fun setSelected(pos: Int) {
        cardsPreferences.saveSetPosition(pos)
        view.close()
    }
}
