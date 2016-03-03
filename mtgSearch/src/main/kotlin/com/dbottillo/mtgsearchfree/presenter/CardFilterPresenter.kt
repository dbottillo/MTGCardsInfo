package com.dbottillo.mtgsearchfree.presenter

import com.dbottillo.mtgsearchfree.resources.CardFilter
import com.dbottillo.mtgsearchfree.view.CardFilterView

interface CardFilterPresenter {

    fun init(view: CardFilterView)

    fun loadFilter()

    fun update(type: CardFilter.TYPE, on: Boolean)

}