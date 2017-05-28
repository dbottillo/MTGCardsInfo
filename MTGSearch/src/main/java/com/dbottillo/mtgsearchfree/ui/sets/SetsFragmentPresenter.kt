package com.dbottillo.mtgsearchfree.ui.sets

import com.dbottillo.mtgsearchfree.model.MTGSet

interface SetsFragmentPresenter{

    fun init(view: SetsFragmentView)

    fun loadSets()

    fun toggleCardTypeViewPreference()

    fun set(): MTGSet?
}
