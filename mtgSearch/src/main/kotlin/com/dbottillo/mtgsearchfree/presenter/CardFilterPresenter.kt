package com.dbottillo.mtgsearchfree.presenter

import com.dbottillo.mtgsearchfree.resources.CardFilter
import com.dbottillo.mtgsearchfree.view.CardFilterView

class CardFilterPresenter constructor(filterView: CardFilterView) {

    val view = filterView

    fun loadFilter() {
        view.filterLoaded()
    }

    fun update(filter: CardFilter, on: Boolean) {
        filter.on = on;
        
    }


}