package com.dbottillo.mtgsearchfree.ui.sets

import com.dbottillo.mtgsearchfree.interactors.SetsInteractor
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.util.Logger

class SetPickerPresenterImpl(
        val setsInteractor: SetsInteractor,
        val cardsPreferences: CardsPreferences,
        val logger: Logger) : SetPickerPresenter {

    lateinit var view: SetPickerView

    override fun init(view: SetPickerView) {
        this.view = view
    }

    override fun loadSets() {
        setsInteractor.load().subscribe {
            view.showSets(it, cardsPreferences.setPosition)
        }
    }

    override fun setSelected(pos: Int) {
        cardsPreferences.saveSetPosition(pos)
        view.close()
    }
}
