package com.dbottillo.mtgsearchfree.ui.search

import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.SearchParams

interface SearchPresenter {
    fun init(view: SearchActivityView)
    fun loadSet()
    fun toggleCardTypeViewPreference()
    fun doSearch(searchParams: SearchParams)
    fun saveAsFavourite(card: MTGCard)
}