package com.dbottillo.mtgsearchfree.ui.sets

import android.annotation.SuppressLint
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.storage.CardsPreferences

class SetPickerPresenterImpl(
    private val setsInteractor: SetsInteractor,
    private val cardsPreferences: CardsPreferences
) : SetPickerPresenter {

    lateinit var view: SetPickerView

    private var sets: List<MTGSet>? = null
    private var currentSet: MTGSet? = null

    override fun init(view: SetPickerView) {
        this.view = view
    }

    @SuppressLint("CheckResult")
    override fun loadSets() {
        setsInteractor.load().subscribe { list ->
            sets = list
            currentSet = list[cardsPreferences.setPosition]
            view.showSets(list, list.indexOf(currentSet!!))
        }
    }

    override fun search(text: String) {
        sets?.filter { it.name.contains(other = text, ignoreCase = true) || it.code == text }?.let {
            view.showSets(it, it.indexOf(currentSet))
        }
    }

    override fun setSelected(set: MTGSet) {
        cardsPreferences.saveSetPosition(sets?.indexOf(set) ?: 0)
        view.close()
    }
}
